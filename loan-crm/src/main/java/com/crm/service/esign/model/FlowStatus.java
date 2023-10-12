package com.crm.service.esign.model;

import com.crm.util.JSONUtil;

/**
 * e签宝流程状态
 */
public enum FlowStatus {

    SIGN_FLOW_UPDATE("SIGN_FLOW_UPDATE","签署人签署完成"),
    SIGN_FLOW_FINISH("SIGN_FLOW_FINISH","流程结束"),
    SIGN_DOC_EXPIRE_REMIND("SIGN_DOC_EXPIRE_REMIND","文件流程过期前提醒"),
    SIGN_DOC_EXPIRE("SIGN_DOC_EXPIRE","流程文件过期"),
    WATERMARK ("WATERMARK ","文件添加数字水印完成"),
    FEEDBACK_SIGNERINFO("FEEDBACK_SIGNERINFO","签署人申请修改身份信息"),
    PROCESS_HANDOVER("PROCESS_HANDOVER","经办人转交签署任务"),
    WILL_FINISH("WILL_FINISH","意愿认证完成"),
    PARTICIPANT_MARKREAD("PARTICIPANT_MARKREAD","签署人已读"),
    FILE_ABNORMAL_REMIND("WILL_FINISH","文件已经加密/已损坏通知");

    private String action;

    private String remark;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    FlowStatus(String code, String remark){
        this.action = code;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
