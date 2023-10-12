package com.crm.controller.api;

import com.crm.service.api.ApiService;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DistributeCustomerThread extends Thread{

    ApiService apiService;

    Long orgId;

    public DistributeCustomerThread(ApiService apiService,Long orgId){
        this.apiService = apiService;
        this.orgId = orgId;
    }

    @Override
    public void run() {
        if(null == apiService || null == orgId)
            return;
        apiService.distributeCustomer(orgId);
    }
}
