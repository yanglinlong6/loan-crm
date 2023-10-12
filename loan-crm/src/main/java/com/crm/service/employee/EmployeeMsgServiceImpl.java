package com.crm.service.employee;

import com.crm.common.CrmConstant;
import com.crm.service.employee.dao.EmployeeMsgPOMapper;
import com.crm.service.employee.model.EmployeeMsgPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeMsgServiceImpl implements EmployeeMsgService {

    @Autowired
    private EmployeeMsgPOMapper employeeMsgPOMapper;

    @Override
    public void addMsg(EmployeeMsgPO msg) {
        if(null == msg || null == msg.getEmployeeId() || StringUtils.isBlank(msg.getEmployeePhone()) || StringUtils.isBlank(msg.getMsg())){
            return;
        }
        employeeMsgPOMapper.insertMsg(msg);
    }

    @Override
    public void updateMsg(EmployeeMsgPO msg) {
        if(null == msg || null == msg.getId()){
            return;
        }
        employeeMsgPOMapper.updateMsg(msg);
    }

    @Override
    public List<EmployeeMsgPO> getMsg(Long employeeId, String mobile) {
        if(null != employeeId)
            return employeeMsgPOMapper.selectMsgByEmployeeId(employeeId, CrmConstant.Employee.Msg.Status.NEW);
        if(StringUtils.isNotBlank(mobile))
            return employeeMsgPOMapper.selectMsgByMobile(mobile, CrmConstant.Employee.Msg.Status.NEW );
        return null;
    }
}
