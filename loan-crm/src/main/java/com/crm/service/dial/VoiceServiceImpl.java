package com.crm.service.dial;

import com.crm.service.dial.dao.CustomerVoiceMapper;
import com.crm.service.dial.model.CustomerVoicePO;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.dao.OrgEmployeePOMapper;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoiceServiceImpl implements VoiceService {

    private static final Logger LOG = LoggerFactory.getLogger(VoiceServiceImpl.class);

    @Autowired
    private CustomerVoiceMapper customerVoiceMapper;

    @Autowired
    private OrgEmployeePOMapper orgEmployeePOMapper;

    @Override
    public List<CustomerVoicePO> getCustomerVoice(Long orgId, String customerPhone) {
        if(null == orgId ||orgId <= 0 || StringUtils.isBlank(customerPhone)){
            return null;
        }
        List<CustomerVoicePO> data = customerVoiceMapper.selectCustomerVoiceList(orgId,customerPhone);
        if(ListUtil.isEmpty(data))
            return data;
        for(CustomerVoicePO po : data){
            OrgEmployeePO employee = orgEmployeePOMapper.selectEmployeeByPhone(po.getEmployeePhone());
            if(null != employee)
                po.setEmployeeName(employee.getName());
        }
        return data;
    }
}
