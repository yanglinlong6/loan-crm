package com.help.loan.distribute.service.api;

import com.help.loan.distribute.common.utils.JSONUtil;

public class SendResult {

    private boolean success = false;

    private String resultMsg;


    public SendResult() {
    }

    public SendResult(boolean success, String resultMsg) {
        this.success = success;
        this.resultMsg = resultMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
