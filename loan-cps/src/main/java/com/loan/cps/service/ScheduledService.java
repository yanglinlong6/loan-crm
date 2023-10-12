package com.loan.cps.service;

import com.loan.cps.entity.Session;
import com.loan.cps.process.NodeResult;

public interface ScheduledService {

	void saveNodeResult(NodeResult nodeResult,Session session);
	
}
