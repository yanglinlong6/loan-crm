package com.crm.service.org.dao;


import com.crm.service.org.model.OrgRegisterPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 机构上门登记数据库映射
 */
@Mapper
@Component
public interface OrgRegisterMapper {

    /**
     * 新增上门记录
     * @param record CustomerRegisterPO
     * @return
     */
    int insertRegister(OrgRegisterPO record);

    /**
     * 查询客户上门记录
     * @param orgId 机构id
     * @param mobile 手机号码
     * @return List<CustomerRegisterPO>
     * @author zhangqiuping
     */
    List<OrgRegisterPO> selectRegister(@Param("orgId") Long orgId, @Param("mobile")String mobile);

}