package com.crm.service.dial.dao;


import com.crm.service.dial.model.CustomerVoicePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CustomerVoiceMapper {

    /**
     * 查询客户通话列表
     * @param orgId 机构id
     * @param customerPhone 客户号码
     * @return List<CustomerVoicePO>
     */
    List<CustomerVoicePO> selectCustomerVoiceList(@Param("orgId") Long orgId, @Param("customerPhone")String customerPhone);

}