package com.crm.service.customer;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.dao.*;
import com.crm.service.customer.model.*;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.esign.ESignService;
import com.crm.util.DateUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


@Service
public class ContractServiceImpl implements ContractService{

    @Autowired
    private CustomerContractMapper customerContractMapper;

    @Autowired
    private CustomerImportMapper customerImportMapper;

    @Autowired
    private ContractProductPOMapper contractProductPOMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    ESignService eSignService;

    @Autowired
    CacheConfigService cacheConfigService;


    @Override
    public void getPage(PageBO<CustomerContractBO> page) {
        // 设置参数
        CustomerContractBO paramObject = page.getParamObject();
        if(null == paramObject){
            paramObject = new CustomerContractBO();
        }
        paramObject.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        if(StringUtils.isNotBlank(paramObject.getStartDate()))
            paramObject.setStartDate(DateUtil.cumputeStartDate(paramObject.getStartDate()));
        if(StringUtils.isNotBlank(paramObject.getEndDate()))
            paramObject.setEndDate(DateUtil.computeEndDate(paramObject.getEndDate()));
        page.setParamObject(paramObject);


        List<CustomerContractBO> datas = customerContractMapper.selectPage(page);
        if(ListUtil.isEmpty(datas)){
            page.setPageCount(0);
            page.setPageCount((0));
            return;
        }
        for(CustomerContractBO bo : datas){
            String images = bo.getImages();
            if(StringUtils.isNotBlank(images) && images.split(",").length == 1 && JudgeUtil.endWith(images,"doc","docs","pdf")){
                bo.setIsDoc(CrmConstant.Contract.Doc.YES);
            }
            bo.setDays(DateUtil.computeDayDifference(bo.getUpdateDate(),new Date())+"天");
            List<CustomerImportPO> importList = customerImportMapper.selectImportsByContract(bo.getId(),bo.getOrgId(),null,null,null);
            if(!ListUtil.isEmpty(importList)){
                bo.setIncomeCount(importList.size());
            }
            Double incomeAmount = 0d;
            for(CustomerImportPO importPO : importList){
                incomeAmount += importPO.getIncome().doubleValue();
            }
            bo.setIncomeAmount(incomeAmount);
            bo.setSurplusAmount(bo.getDeposit().doubleValue() - bo.getIncomeAmount());
            bo.setProductList(contractProductPOMapper.selectContractProduct(LoginUtil.getLoginEmployee().getOrgId(),bo.getId()));
        }
        page.setDataList(datas);
        int totalCount = customerContractMapper.selectPageCount(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }



    @Transactional
    @Override
    public void addCustomerContract(CustomerContractPO po) {
        if(null == po)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"未提交签约信息");
        if(null == po.getCustomerId() || null == customerMapper.selectCustomerById(po.getCustomerId()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：未提交客户id或者客户存在，请先添加客户");
        if(null != customerContractMapper.selectCustomerContractByCustomerIdAndState(po.getCustomerId(),CrmConstant.Contract.State.INIT+""))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：客户正在其签约，请勿重复提交签约");
        if(StringUtils.isBlank(po.getContractCode()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：未提交合同编号");
        if(null == po.getCostRate() || po.getCostRate().doubleValue() >=100d){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：未提交费率，或者费率超过100%");
        }
        if(StringUtils.isBlank(po.getImages()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：未提交合同文件(文件未pdf，或者图片，多个以,号隔开)");
        if(null == po.getDeposit() || po.getDeposit().doubleValue() <=0d) // 合同金额
            po.setDeposit(new BigDecimal(0));
        if(null == po.getWay() || po.getWay() <= 0 )//支付方式- 该字段已不用
            po.setWay(Byte.valueOf(CrmConstant.Customer.init));
        if(null == po.getState() || po.getState() <=0)
            po.setState(Byte.valueOf(CrmConstant.Customer.init));
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        po.setOrgId(employee.getOrgId());
        po.setShopId(employee.getShopId());
        po.setTeamId(employee.getTeamId());
        po.setEmployeeId(employee.getId());
        po.setCreateBy(employee.getName());

        eSignService.esign(po);
        // 到这里表示已签约
        customerContractMapper.insertCustomerContract(po);

        CustomerPO customerPO = customerMapper.selectCustomerById(po.getCustomerId());
        String processStr = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,LoginUtil.getLoginEmployee().getOrgId().toString());
        Byte process = cacheConfigService.parseCustomerProcess(processStr,"签约");
        if(null != process && customerPO.getProgress() <= process){
            customerPO.setProgress(process);
            customerMapper.updateCustomer(customerPO);
        }


        if(ListUtil.isEmpty(po.getProductList())){
            return;
        }
        for(ContractProductPO cp : po.getProductList()){
            if(null == cp.getId()){
                cp.setOrgId(po.getOrgId());
                cp.setContractId(po.getId());
                cp.setCustomerId(po.getCustomerId());
                contractProductPOMapper.insertContractProduct(cp);
            }else{
                contractProductPOMapper.updateContractProductById(cp);
            }
        }
    }

    @Override
    public void updateCustomerContract(CustomerContractPO po) {
        if(null == po || null == po.getId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"缺少签约信息");
        if(null != po.getCostRate() && po.getCostRate().doubleValue() >=100d){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"签约：费率超过100%");
        }
        po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        customerContractMapper.updateCustomerContract(po);
    }

    @Override
    public List<CustomerContractBO> getEmployeeContract(CustomerContractBO bo) {
        bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        return customerContractMapper.selectContractAll(bo);
    }

    @Override
    public List<CustomerContractBO> getCustomerContract(Long customerId, String state) {
        List<CustomerContractBO> list = customerContractMapper.selectCustomerContractByCustomerId(customerId);
        if(ListUtil.isEmpty(list))
            return list;
        if(StringUtils.isBlank(state))
            return list;
        Iterator<CustomerContractBO> iterator = list.iterator();
        while (iterator.hasNext()){
            CustomerContractBO bo = iterator.next();
            if(!JudgeUtil.in(bo.getState().toString(),state.split(","))){
                iterator.remove();
            }
        }
        return list;
    }

    @Override
    public CustomerContractBO getCustomerContractByFlowId(String flowId) {
        if(StringUtils.isBlank(flowId))
            return null;
        CustomerContractBO bo = customerContractMapper.selectCustomerContractByFlowId(flowId);
        return bo;
    }
}
