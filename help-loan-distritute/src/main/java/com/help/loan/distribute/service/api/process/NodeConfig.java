package com.help.loan.distribute.service.api.process;

import com.alibaba.fastjson.JSONObject;

public class NodeConfig {

    public static final Integer NODE_MSG_TYPE_QUESTION = 0;

    public static final Integer NODE_MSG_TYPE_RECOMMEND = 1;

    public static final Integer NODE_MSG_TYPE_ERR = 2;

    public static final Integer NODE_MSG_TYPE_URGE = 3;

    private Integer nodeId;

    private Integer leftNodeId;

    private Integer rightNodeId;

    private JSONObject questionMsg;

    private JSONObject recommendMsg;

    private JSONObject errMsg;

    private JSONObject urgeMsg;

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getLeftNodeId() {
        return leftNodeId;
    }

    public void setLeftNodeId(Integer leftNodeId) {
        this.leftNodeId = leftNodeId;
    }

    public Integer getRightNodeId() {
        return rightNodeId;
    }

    public void setRightNodeId(Integer rightNodeId) {
        this.rightNodeId = rightNodeId;
    }

    public JSONObject getQuestionMsg() {
        return questionMsg;
    }

    public void setQuestionMsg(JSONObject questionMsg) {
        this.questionMsg = questionMsg;
    }

    public JSONObject getRecommendMsg() {
        return recommendMsg;
    }

    public void setRecommendMsg(JSONObject recommendMsg) {
        this.recommendMsg = recommendMsg;
    }

    public JSONObject getUrgeMsg() {
        return urgeMsg;
    }

    public void setUrgeMsg(JSONObject urgeMsg) {
        this.urgeMsg = urgeMsg;
    }

    public JSONObject getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(JSONObject errMsg) {
        this.errMsg = errMsg;
    }

}
