package com.crm.service.behind;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.service.behind.model.CustomerBehindBO;
import com.crm.service.behind.model.CustomerBehindRemarkPO;
import com.crm.service.customer.model.ContractProductPO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.DateUtil;
import com.crm.util.JudgeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public interface BehindCustomerService {

    /**
     * 分页
     * @param pageBO PageBO<CustomerBehindBO> pageBO
     * @param employee OrgEmployeeBO
     */
    void getPage(PageBO<CustomerBehindBO> pageBO, OrgEmployeeBO employee);

    /**
     * 分配客户
     * @param bo CustomerBehindBO
     */
    ResultVO disCustomer(CustomerBehindBO bo);

    /**
     * 增加客户备注
     * @param po CustomerBehindRemarkPO
     * @return ResultVO
     */
    ResultVO addCustomerRemark(CustomerBehindRemarkPO po);

    ResultVO updateProductProcess(ContractProductPO po);

    /**
     * 获取签约产品
     * @param id
     * @return
     */
    ContractProductPO getProduct(Long id);



    default void setParamObject(CustomerBehindBO paramObject,OrgEmployeeBO employee){
        if(null == paramObject || employee == null)
            return;
        // 设置查询范围
        paramObject.setOrgId(employee.getOrgId());
        if(JudgeUtil.in(employee.getRole().getType(),CrmConstant.Role.Type.SHOP))
            paramObject.setShopId(employee.getShopId());
        if(JudgeUtil.in(employee.getRole().getType(),CrmConstant.Role.Type.TEAM))
            paramObject.setTeamId(employee.getTeamId());
        if(JudgeUtil.in(employee.getRole().getType(),CrmConstant.Role.Type.COMMON))
            paramObject.setEmployeeId(employee.getId());

        // 查询日期
        if(StringUtils.isBlank(paramObject.getStartDate())){
            paramObject.setStartDate(DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START);
        }else{
            paramObject.setStartDate(DateUtil.cumputeStartDate(paramObject.getStartDate()));
        }
        paramObject.setEndDate(DateUtil.computeEndDate(paramObject.getEndDate()));
    }


}
