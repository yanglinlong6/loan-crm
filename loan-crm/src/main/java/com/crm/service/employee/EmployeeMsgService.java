package com.crm.service.employee;

import com.crm.service.employee.model.EmployeeMsgPO;

import java.util.List;

public interface EmployeeMsgService {


    void addMsg(EmployeeMsgPO msg);

    void updateMsg(EmployeeMsgPO msg);

    List<EmployeeMsgPO> getMsg(Long employeeId, String mobile);


}
