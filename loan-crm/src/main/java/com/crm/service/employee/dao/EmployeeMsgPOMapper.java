package com.crm.service.employee.dao;


import com.crm.service.employee.model.EmployeeMsgPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface EmployeeMsgPOMapper {

    List<EmployeeMsgPO> selectMsgByEmployeeId(@Param("employeeId") Long employeeId,@Param("status") Byte status);

    List<EmployeeMsgPO> selectMsgByMobile(@Param("mobile")String mobile,@Param("status")Byte status);

    int insertMsg(EmployeeMsgPO msg);

    int updateMsg(EmployeeMsgPO record);

}