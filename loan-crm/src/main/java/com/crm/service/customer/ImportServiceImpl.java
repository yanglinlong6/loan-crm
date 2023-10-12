package com.crm.service.customer;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.behind.dao.CustomerBehindPOMapper;
import com.crm.service.behind.model.CustomerBehindPO;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.dao.CustomerContractMapper;
import com.crm.service.customer.dao.CustomerImportMapper;
import com.crm.service.customer.model.*;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.DateUtil;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImportServiceImpl implements ImportService{

    private static final Logger LOG = LoggerFactory.getLogger(ImportServiceImpl.class);

    @Autowired
    private CustomerImportMapper customerImportMapper;

    @Autowired
    private CustomerContractMapper customerContractMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerBehindPOMapper customerBehindPOMapper;

    @Autowired
    CacheConfigService cacheConfigService;

    @Override
    public void getPage(PageBO<CustomerImportBO> pageBO) {
        CustomerImportBO paramObject = pageBO.getParamObject();
        if(null == paramObject){
            paramObject = new CustomerImportBO();
        }
        paramObject.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        if(StringUtils.isNotBlank(paramObject.getStartDate()))
            paramObject.setStartDate(DateUtil.cumputeStartDate(paramObject.getStartDate()));
        if(StringUtils.isNotBlank(paramObject.getEndDate()))
            paramObject.setEndDate(DateUtil.computeEndDate(paramObject.getEndDate()));
        pageBO.setParamObject(paramObject);
        List<CustomerImportBO> datas = customerImportMapper.selectPage(pageBO);
        pageBO.setDataList(datas);
        if(ListUtil.isEmpty(datas)){
            pageBO.setTotalCount(0);
            pageBO.setPageCount(0);
            return;
        }
        int totalCount = customerImportMapper.selectPageCount(pageBO);
        pageBO.setTotalCount(totalCount);
        if(totalCount == 0){
            pageBO.setPageCount(0);
        }else if(totalCount%pageBO.getSize() == 0){
            pageBO.setPageCount(totalCount/pageBO.getSize());
        }else
            pageBO.setPageCount(totalCount/pageBO.getSize()+1);
    }

    @Transactional
    @Override
    public void addImport(CustomerImportBO bo) {
        if(null == bo || null == bo.getCustomerId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"未提交进件信息,客户id,姓名,电话等参数");
        bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        String state = CrmConstant.Contract.State.INIT+","+CrmConstant.Contract.State.YES;
        if(null == customerContractMapper.selectCustomerContractByContractIdAndState(bo.getContractId(),state)){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"客户签约信息不存在");
        }
        if(null == bo.getProductId() || StringUtils.isBlank(bo.getProductName())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"进件未选择进件产品");
        }
        CustomerImportPO po = JSONUtil.toJavaBean(bo.toString(),CustomerImportPO.class);
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        po.setOrgId(employee.getOrgId());
        po.setShopId(employee.getShopId());
        po.setTeamId(employee.getTeamId());
        po.setEmployeeId(employee.getId());
        po.setCreateBy(employee.getName());
        customerImportMapper.insertImport(po);

        // 更新客户[进件]状态
        CustomerPO customerPO = customerService.getCustomerById(po.getCustomerId());
        String processStr = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,LoginUtil.getLoginEmployee().getOrgId().toString());
        Byte process = cacheConfigService.parseCustomerProcess(processStr,"进件");
        if(null != process && customerPO.getProgress() <= process){
            customerPO.setProgress(process);
            customerService.updateCustomer(customerPO);
        }

        // 更新签约状态
        CustomerContractPO contractPO = customerContractMapper.selectCustomerContractById(bo.getContractId());
        if(null != contractPO){
            if(bo.getState().byteValue() < CrmConstant.Import.State.INCOME){
                contractPO.setState(CrmConstant.Contract.State.INCOMING);
            }else if(bo.getState().byteValue()  >= CrmConstant.Import.State.INCOME){
                contractPO.setState(CrmConstant.Contract.State.INCOME);
            }else {
                contractPO.setState(CrmConstant.Contract.State.INIT);
            }
            customerContractMapper.updateCustomerContract(contractPO);
        }

        //进入下一步,后端管理
        CustomerBehindPO customerBehindPO = customerBehindPOMapper.selectByCustomerId(po.getCustomerId());
        if(null != customerBehindPO){
            LOG.info("进件: 已进入后端,无需再次进入后端");
            return;
        }
        customerPO = customerService.getCustomerById(po.getCustomerId());
        CustomerBehindPO customerBehind = new CustomerBehindPO();
        customerBehind.setCustomerId(po.getCustomerId());
        customerBehind.setInfo(customerPO.getRemark());
        customerBehind.setOrgId(employee.getOrgId());
        customerBehind.setProcess(CrmConstant.INIT);
        customerBehind.setCreateBy(employee.getName());
        customerBehind.setContractId(po.getContractId());
        customerBehindPOMapper.insertCustomerBehindPO(customerBehind);

    }

    @Override
    public void updateImport(CustomerImportBO bo) {
        if(null == bo || null == bo.getId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"未提交进件信息");
        bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        if(null != bo.getContractId()){
            String state = CrmConstant.Contract.State.INIT+","+CrmConstant.Contract.State.YES;
            CustomerContractPO contract = customerContractMapper.selectCustomerContractByContractIdAndState(bo.getContractId(),state);
            if(null == contract){
                throw new CrmException(CrmConstant.ResultCode.FAIL,"客户签约信息不存在");
            }
        }
        CustomerImportPO po = JSONUtil.toJavaBean(bo.toString(),CustomerImportPO.class);
        po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        customerImportMapper.updateImport(po);
    }

    @Override
    public List<CustomerImportBO> getImportProduct(Long orgId, Long productId) {
        return customerImportMapper.selectImportProduct(orgId,productId);
    }
}
