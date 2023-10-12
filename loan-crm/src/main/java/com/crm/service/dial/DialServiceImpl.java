package com.crm.service.dial;

import com.crm.service.dial.dao.CustomerDialPOMapper;
import com.crm.service.dial.model.CustomerDialPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DialServiceImpl implements DialService {

    @Autowired
    private CustomerDialPOMapper customerDialPOMapper;

    @Override
    public void addDial(CustomerDialPO customerDialPO) {

        customerDialPOMapper.insert(customerDialPO);
    }
}
