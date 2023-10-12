package com.loan.cps.process;

import java.util.UUID;

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
	
	public Integer getLeftNodeId(Integer locationNode) {
		if(NodeManager.MOBILE.equals(rightNodeId)&&locationNode!=null) {
			if(NodeManager.MOBILE.equals(NodeManager.getNode(leftNodeId).getConfig().getRightNodeId())) {
				return leftNodeId;
			}else {
				return NodeManager.FINISH2;
			}
		}
		return leftNodeId;
	}

	public void setLeftNodeId(Integer leftNodeId) {
		this.leftNodeId = leftNodeId;
	}

	public Integer getRightNodeId(Integer locationNode) {
		if(NodeManager.MOBILE.equals(rightNodeId)&&locationNode!=null) {
			if(NodeManager.MOBILE.equals(NodeManager.getNode(leftNodeId).getConfig().getRightNodeId())) {
				return leftNodeId;
			}else {
				return NodeManager.FINISH2;
			}
		}
		return rightNodeId;
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
