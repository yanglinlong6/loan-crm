package com.crm.service.behind;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.behind.dao.CustomerBehindPOMapper;
import com.crm.service.behind.dao.CustomerBehindRemarkMapper;
import com.crm.service.behind.model.CustomerBehindBO;
import com.crm.service.behind.model.CustomerBehindPO;
import com.crm.service.behind.model.CustomerBehindRemarkPO;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.dao.ContractProductPOMapper;
import com.crm.service.customer.dao.CustomerRemarkMapper;
import com.crm.service.customer.model.ContractProductPO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.customer.model.CustomerRemarkPO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.DateUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class BehindCustomerServiceImpl implements BehindCustomerService {

    @Autowired
    private CustomerBehindPOMapper customerBehindMapper;

    @Autowired
    private CustomerBehindRemarkMapper customerBehindRemarkMapper;

    @Autowired
    private ContractProductPOMapper contractProductPOMapper;

    @Autowired
    private CustomerService customerService;

    @Override
    public void getPage(PageBO<CustomerBehindBO> pageBO, OrgEmployeeBO employee) {
        CustomerBehindBO paramObject = pageBO.getParamObject();
        setParamObject(paramObject,employee);
        pageBO.setParamObject(paramObject);

        List<CustomerBehindBO> list = customerBehindMapper.getBehindPage(pageBO);
        if(ListUtil.isEmpty(list)){
            pageBO.setPageCount(0);
            pageBO.setTotalCount(0);
            return;
        }
        for(CustomerBehindBO bo : list){
            CustomerPO customer = customerService.getCustomerById(bo.getCustomerId());
            bo.setCustomer(customer);
            bo.setRemarkList(customerBehindRemarkMapper.selectAllCustomerBehindRemark(bo.getCustomerId()));
            bo.setProductList(contractProductPOMapper.selectContractProduct(bo.getOrgId(),bo.getContractId()));
        }
        pageBO.setDataList(list);
        int totalCount = customerBehindMapper.getBehindPageCount(pageBO);
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
    public ResultVO disCustomer(CustomerBehindBO bo) {
        if(null == bo.getShopId() || bo.getShopId() <= 0){
            return ResultVO.fail("分配客户：缺少门店id",null);
        }
        if(null == bo.getTeamId() || bo.getTeamId() <= 0){
            return ResultVO.fail("分配客户：缺少团队id",null);
        }
        if(null == bo.getEmployeeId() || bo.getEmployeeId() <= 0){
            return ResultVO.fail("分配客户：缺少员工id",null);
        }
        if(ListUtil.isEmpty(bo.getCustomerIds())){
            return ResultVO.fail("分配客户：请选择客户",null);
        }
        Date date = new Date();
        for(int i=0;i<bo.getCustomerIds().size();i++){
            CustomerBehindPO po = new CustomerBehindPO();
            po.setEmployeeId(bo.getEmployeeId());
            po.setFirstEmployee(bo.getEmployeeId());
            po.setCustomerId(bo.getCustomerIds().get(i));
            po.setShopId(bo.getShopId());
            po.setTeamId(bo.getTeamId());
            po.setProcess(CrmConstant.BehindCustomer.Process.ASSIGND);
            po.setDisDate(date);
            po.setRemarkDate(date);
            po.setCreateBy(LoginUtil.getLoginEmployee().getName());
            po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
            customerBehindMapper.updateCustomerBehind(po);
        }
        return ResultVO.success("分配客户成功",null);
    }

    @Transactional
    @Override
    public ResultVO addCustomerRemark(CustomerBehindRemarkPO po) {
        if(null == po.getCustomerId() || StringUtils.isBlank(po.getRemark())){
            return ResultVO.fail("后端添加跟进记录：客户id或者remark信息",null);
        }
        po.setCreateBy(LoginUtil.getLoginEmployee().getName());
        customerBehindRemarkMapper.insertCustomerBehindRemark(po);

        CustomerBehindPO behind = new CustomerBehindPO();
        behind.setCustomerId(po.getCustomerId());
        behind.setRemarkDate(new Date());
        customerBehindMapper.updateCustomerBehind(behind);


        return ResultVO.success("后端添加跟进记录成功",LoginUtil.getLoginEmployee().getName());
    }

    @Autowired
    CustomerRemarkMapper customerRemarkMapper;

    String REMARK_MODEL = "产品[%s]%s: %s";
    @Override
    public ResultVO updateProductProcess(ContractProductPO po) {
        if(null == po || null == po.getId() ){
            return ResultVO.fail("修改产品进度",null);
        }
        contractProductPOMapper.updateContractProductById(po);
        ContractProductPO productPO = getProduct(po.getId());
        if(null != productPO
                && JudgeUtil.in(productPO.getProcess(),CrmConstant.Product.Process.PRO,CrmConstant.Product.Process.COMPLETE)){
            CustomerRemarkPO remarkPO = new CustomerRemarkPO();
            remarkPO.setCustomerId(productPO.getCustomerId());
            remarkPO.setEmployeeId(LoginUtil.getLoginEmployee().getId());
            String remark;
            if(po.getProcess() == CrmConstant.Product.Process.PRO){
                remark = String.format(REMARK_MODEL,productPO.getProductName(),productPO.getRemark(),"后端处理中");
            }else{
                remark = String.format(REMARK_MODEL,productPO.getProductName(),productPO.getRemark(),"后端处理完成");
            }
            remarkPO.setRemark(remark);
            remarkPO.setCreateBy(LoginUtil.getLoginEmployee().getName());
            customerRemarkMapper.insertCustomerRemark(remarkPO);
        }
        return ResultVO.success("修改产品进度成功",null);
    }

    @Override
    public ContractProductPO getProduct(Long id) {
        if(null == id || id <= 0){
            return null;
        }
        return contractProductPOMapper.selectContractProductById(id);
    }


}
