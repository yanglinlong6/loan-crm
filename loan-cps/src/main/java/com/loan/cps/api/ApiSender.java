package com.loan.cps.api;

import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.UserDTO;

public interface ApiSender {

	SendResult send(UserAptitudePO po,UserDTO select);
	
}
