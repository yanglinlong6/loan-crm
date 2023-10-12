package com.help.loan.distribute.service.user.model;

import com.help.loan.distribute.common.BasePO;
import com.help.loan.distribute.common.utils.JSONUtil;

public class UserNotAptitudeSendStrategyRecPO extends BasePO {


    private String userId;

    public UserNotAptitudeSendStrategyRecPO(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
