package com.crm.service.esign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.ContractService;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.*;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.esign.dao.OrgESignMapper;
import com.crm.service.esign.flow.Signer;
import com.crm.service.esign.flow.SignfieldInfo;
import com.crm.service.esign.helper.AccountHelper;
import com.crm.service.esign.helper.FileTemplateHelper;
import com.crm.service.esign.helper.SignHelper;
import com.crm.service.esign.helper.TokenHelper;
import com.crm.service.esign.model.OrgESignPO;
import com.crm.service.esign.util.DefineException;
import com.crm.service.esign.vo.EsignReceiveBO;
import com.crm.service.esign.vo.FlowConfigInfo;
import com.crm.service.esign.vo.FlowInfo;
import com.crm.service.esign.vo.IdType;
import com.crm.service.org.OrgService;
import com.crm.service.org.model.OrgPO;
import com.crm.util.DateUtil;
import com.crm.util.ListUtil;
import com.crm.util.http.HttpClientProxy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public class ESignServiceImpl implements ESignService{

    private static final Logger LOG = LoggerFactory.getLogger(ESignServiceImpl.class);
    @Autowired
    private OrgESignMapper orgESignMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    CustomerService customerService;

    @Autowired
    OrgService orgService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CacheConfigService cacheConfigService;

    @Autowired
    ContractService contractService;

    @Override
    public void esign(CustomerContractPO po) {
        if(ListUtil.isEmpty(po.getLocations())){
            return;
        }
        OrgPO org = orgService.getOrgById(po.getOrgId());
        OrgESignPO orgESignPO = org.getOrgESign();
        if(null == orgESignPO){
            if(po.getEsign()){
                throw new CrmException(CrmConstant.ResultCode.FAIL,"未开通电子签约功能!");
            }else{
                return;
            }
        }
        List<CustomerContractBO> contractList = contractService.getCustomerContract(po.getCustomerId(),CrmConstant.Contract.State.INIT.toString());
        if(!ListUtil.isEmpty(contractList)){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"电子合约:该客户已经提交了签约,请继续上次签约");
        }
        String filePath = po.getImages().split(",")[0];// 默认取第一pdf合同文件
        if(!filePath.endsWith("pdf")){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"电子合约:必须是pdf文件,请先转换!");
        }
        //获取合同路径和文件名
        Object value = cacheConfigService.getCacheConfigValue(CrmConstant.Config.ESign.FIELD, CrmConstant.Config.ESign.Key.FILE_DIR);
        String src = (null == value ? "/data/images/":value.toString());
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1,filePath.length());
        filePath = (src.endsWith(File.separator)?src:src+ File.separator)+fileName;

        // 签约归属账号
        value = cacheConfigService.getCacheConfigValue(CrmConstant.Config.ESign.FIELD, CrmConstant.Config.ESign.Key.ACCOUNT);
        if(null == value || StringUtils.isBlank(value.toString())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"电子合约:请设置归属账号");
        }
        String belongAccountId = value.toString();
        try{
            TokenHelper.getTokenData(stringRedisTemplate);
            // 合同文件上传e签宝
            JSONObject uploadJSON = FileTemplateHelper.createFileByUpload(filePath, fileName, belongAccountId);
            String uploadUrl = uploadJSON.getString("uploadUrl");
            String fileId = uploadJSON.getString("fileId");
            FileTemplateHelper.streamUpload(filePath,uploadUrl);// 上传

            // 客户签署账号
            CustomerPO customer = customerService.getCustomerById(po.getCustomerId());
            String signAccountId = customer.getAccountId();
            if(StringUtils.isBlank(signAccountId)){
                JSONObject signAccount = AccountHelper.createPersonAcct(customer.getMobile(), customer.getName(), IdType.CH_IDCARD, "", customer.getMobile(), null);
                signAccountId = signAccount.getString("accountId");

                CustomerPO newCustomer = new CustomerPO();
                newCustomer.setId(customer.getId());
                newCustomer.setAccountId(signAccountId);
                customerService.updateCustomer(newCustomer);
            }
            
            // 创建机构账号
            if(null == orgESignPO){
                throw new CrmException(CrmConstant.ResultCode.FAIL,"电子合约:当前公司没有配置电子签约参数");
            }
            // 发起签署机构
            String orgAccountId = org.getAccountId();
            if(StringUtils.isBlank(orgAccountId)){
                JSONObject startOrgAccount = AccountHelper.createOrgAcct(orgESignPO.getOrgNumber(), null, org.getName(), IdType.ORG, orgESignPO.getOrgNumber());
                orgAccountId = startOrgAccount.getString("orgId");

                // 修改机构的e签宝账户id
                OrgPO newOrg = new OrgPO();
                newOrg.setId(org.getId());
                newOrg.setAccountId(orgAccountId);
                orgService.updateOrg(newOrg);
            }
            // 创建发起人账号 , 由机构员工发起
            OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
            String startAccountId = employeeBO.getAccountId();
            if(StringUtils.isBlank(signAccountId)){
                JSONObject startAccount = AccountHelper.createPersonAcct(employeeBO.getPhone(), employeeBO.getName(), IdType.CH_IDCARD, "", employeeBO.getPhone(), null);
                startAccountId = startAccount.getString("accountId");

                OrgEmployeePO employeePO = new OrgEmployeePO();
                employeeBO.setId(employeeBO.getId());
                employeeBO.setAccountId(startAccountId);
                employeeService.updateEmployee(employeePO);
            }
            /**创建机构签署对象*/
            Signer orgSign = new Signer(Boolean.TRUE,po.getLocations().size(),null,null,null)
                    .putSignerAccount(startAccountId,orgAccountId,"1,2",null)
                    .putSignField(SignfieldInfo.createDefalutSignfield("2",fileId,null,true).putPosBean("4",orgESignPO.getX(),orgESignPO.getY(),20, DateUtil.yyyymmdd2)); //1-个人盖章,2-机构盖章, 如果是2则sealId需要传入签章编号


            FlowBuild flowBuild = FlowBuild.createFlow()
                    .setFlowInfo(new FlowInfo(fileName,null,null,new FlowConfigInfo(orgESignPO.getNoticeUrl(),"1,2",orgESignPO.getRedirectUrl(),"1,2")))
                    .addSigner(orgSign);
            int index = 0;
            for(LocationPO locationPO : po.getLocations()){
                // 客户签署对象
                Signer signerPersonal = new Signer(Boolean.FALSE,index,null,null,null)
                        .putSignerAccount(signAccountId,null,"1,2",null)
                        .putSignField(SignfieldInfo.createDefalutSignfield("0",fileId,null,false).putPosBean(locationPO.getPage().toString(),locationPO.getX(),locationPO.getY(),18, DateUtil.yyyymmdd2));
                flowBuild.addSigner(signerPersonal);
                index++;
            }
            flowBuild.addDoc(fileId,fileName).addCopier(belongAccountId,0,null);// 创建流程对象

            String flowId = SignHelper.oneStepFlow(flowBuild).getString("flowId");

            SignHelper.startSignFlow(flowId);

            po.setFileId(fileId);
            po.setFlowId(flowId);

            po.setState(CrmConstant.Contract.State.INIT);

            LOG.info("[e签宝]发起签约流程成功:客户-{}-{},{}-{}",customer.getName(),customer.getMobile(),fileId,flowId);
        }catch (Exception e){
            String msg = "[e签宝]签约失败:"+e.getMessage();
            CrmException newException = new CrmException(CrmConstant.ResultCode.EX,msg);
            newException.setStackTrace(e.getStackTrace());
            throw newException;
        }
    }

    @Override
    public OrgESignPO getOrgESign(Long orgId) {
        if(null == orgId || orgId < 0){
            return null;
        }
        OrgESignPO po = orgESignMapper.selectOrgESignByOrgId(orgId);
        return po;
    }

    @Override
    public void updateOrgESign(OrgESignPO orgESignPO) {
        orgESignMapper.updateOrgESign(orgESignPO);
    }

    @Override
    public Boolean updateFlow(EsignReceiveBO esignReceiveBO) throws DefineException {
        CustomerContractBO contractBO = contractService.getCustomerContractByFlowId(esignReceiveBO.getFlowId());
        if(null == contractBO)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"e签宝流程[{"+esignReceiveBO.getFlowId()+"}]不存在");

        CustomerContractBO newContract = new CustomerContractBO();
        newContract.setId(contractBO.getId());
        newContract.setState(CrmConstant.Contract.State.YES);

        JSONObject data = SignHelper.downFlowDoc(esignReceiveBO.getFlowId());
        JSONArray docs = data.getJSONArray("docs");
        if(ListUtil.isEmpty(docs)){
            return false;
        }
        StringBuffer images = new StringBuffer();
        for(int index=0;index<docs.size();index++){
            JSONObject doc = docs.getJSONObject(index);
            String dir = cacheConfigService.getCacheConfigValue(CrmConstant.Config.Upload.UPLOAD, CrmConstant.Config.Upload.SOURCE_DIC);
            String fileName = doc.getString("fileName");
            String outPath = dir.endsWith(File.separator)?dir+fileName : dir+File.separator+fileName;
            boolean isSuccess = HttpClientProxy.download(doc.getString("fileUrl"),outPath);
            if(isSuccess){
                if(index == 0){
                    images.append(outPath);
                }else{
                    images.append(",").append(outPath);
                }
            }
        }
        if(images.toString().isEmpty()){
            return false;
        }
        newContract.setImages(images.toString());
        contractService.updateCustomerContract(contractBO);

        // 更新客户状态
        CustomerPO customerPO = new CustomerPO();
        customerPO.setId(contractBO.getCustomerId());
        customerPO.setProgress(CustomerBO.getOrgProcess(cacheConfigService.getCacheConfigValue(CrmConstant.Config.Customer.FIELD,contractBO.getOrgId().toString()),"签约"));
        customerService.updateCustomer(customerPO);
        return true;
    }





}
