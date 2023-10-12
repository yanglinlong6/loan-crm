package com.crm.service.customer;

import com.crm.common.PageBO;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.customer.model.CustomerCommentPO;
import com.crm.service.customer.model.CustomerFieldPO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.JudgeUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 客户服务类接口
 */
public interface CustomerService {



    /**
     * 客户字段说明列表
     * @param orgId 机构id
     * @return List<CustomerFieldPO>
     */
    List<CustomerFieldPO> getAllField(Long orgId);

    /**
     * 客户分页方法
     * @param pageBO PageBO<CustomerBO>
     */
    void getCustomerPage(PageBO<CustomerBO> pageBO);

    /**
     * 获取所有进件客户列表
     * @param pageBO PageBO<CustomerBO>
     */
    void getImportCustomerPage(PageBO<CustomerBO> pageBO);

    List<CustomerBO> getMyAllCustomer(CustomerBO bo);

    CustomerPO getCustomerByMd5(Long orgId,String mobileMd5);

    List<CustomerPO> getNewCustomerList(Long orgId);


    CustomerPO getCustomerById(Long id);
    /**
     * 返回时间范围内的进量
     * @param orgId 机构id
     * @param channelId 渠道id
     * @param city 城市
     * @param media 媒体
     * @param startDate  查询开始日期
     * @return 返回时间范围内的进量
     */
    int getChannelNewCustomerCount(Long orgId, Long channelId, String city, String media, String startDate);

    int getEmployeeDistributeCount(Long orgId,Long employeeId,String startDate);

    /**
     * 添加客户
     * @param po CustomerPO po
     */
    void addCustomer(CustomerPO po);

    /**
     * 更新客户信息
     * @param po CustomerPO
     */
    void updateCustomer(CustomerPO po);

    /**
     * 跟进方法
     * @param bo CustomerBO
     */
    void updateCustomer(CustomerBO bo);

    void delCustomer(Long id);

    /**
     * 更新客户信息,主要用于客户迁移门店或者团队
     * @param employee OrgEmployeePO
     *
     */
    void updateCustomerByEmployee(OrgEmployeePO employee);

    /**
     * 加入公共吃
     * @param bo CustomerBO
     */
    void addPublicPool(CustomerBO bo);

    /**
     * 增加主管点评
     * @param comment CustomerCommentPO
     */
    void addCustomerComment(CustomerCommentPO comment);

    void addCustomerDistributeRec(Long customerId,Long employeeId,Byte status,String createBy);

    default String getZhuti(CustomerPO customer){
        if(null == customer){
            return "聚我贷";
        }
        String zhuti = JudgeUtil.contain(customer.getRemark(),"聚我贷","通融","小康","博能");
        return StringUtils.isBlank(zhuti)?"聚我贷":zhuti;
    }

    default CustomerBO setCustomerParam(CustomerBO bo){
        if(bo == null){
            bo = new CustomerBO();
        }
        if(StringUtils.isBlank(bo.getCity()))
            bo.setCity(null);
        if(StringUtils.isBlank(bo.getField1())){
            bo.setField1(null);
        }
        if(StringUtils.isBlank(bo.getField2())){
            bo.setField2(null);
        }
        if(StringUtils.isBlank(bo.getField3())){
            bo.setField3(null);
        }
        if(StringUtils.isBlank(bo.getField4())){
            bo.setField4(null);
        }
        if(StringUtils.isBlank(bo.getField5())){
            bo.setField5(null);
        }
        if(StringUtils.isBlank(bo.getField6())){
            bo.setField6(null);
        }
        if(StringUtils.isBlank(bo.getField7())){
            bo.setField7(null);
        }
        if(StringUtils.isBlank(bo.getField8())){
            bo.setField8(null);
        }
        if(StringUtils.isBlank(bo.getField9())){
            bo.setField9(null);
        }
        if(StringUtils.isBlank(bo.getField10())){
            bo.setField10(null);
        }
        if(StringUtils.isBlank(bo.getField11())){
            bo.setField11(null);
        }
        if(StringUtils.isBlank(bo.getMobile()))
            bo.setMobile(null);
        if(StringUtils.isBlank(bo.getName()))
            bo.setName(null);
        if(StringUtils.isBlank(bo.getLevel()))
            bo.setLevel(null);
        return bo;
    }

    /**
     * 判断客户多数员工id
     *
     * @param po CustomerPO
     * @return long  员工id
     */
    default Long getEmployeeId(CustomerPO po){
        if(null == po.getEmployeeId() || po.getEmployeeId() <= 0){
            return po.getAgainEmployeeId();
        }
        return po.getEmployeeId();
    }
}
