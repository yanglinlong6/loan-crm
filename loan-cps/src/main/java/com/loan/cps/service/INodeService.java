package com.loan.cps.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.cps.dao.NodeConfigDao;
import com.loan.cps.process.Node;
import com.loan.cps.process.NodeConfig;
import com.loan.cps.process.NodeManager;

@Component
public class INodeService implements NodeService,InitializingBean{
	
	private static Log logger = LogFactory.getLog(INodeService.class);
	
	@Autowired
	private CustMsgService custMsgService;

	@Autowired
	private NodeConfigDao nodeConfigDao;
	
	@Autowired
	private NodeManager nodeManager;
	
	@Override
	public void resetNodeConfig() {
		Iterator<Entry<Integer, Node>> iterator = nodeManager.getEm().entrySet().iterator();
		while(iterator.hasNext()) {
			Entry<Integer, Node> next = iterator.next();
			NodeConfig nodeConfig = getNodeConfig(next.getKey());
			List<JSONObject> nodeMsg = custMsgService.getNodeMsg(next.getKey());
			for(JSONObject obj:nodeMsg) {
				if(NodeConfig.NODE_MSG_TYPE_ERR.equals(obj.getInteger("node_msg_type"))) {
					nodeConfig.setErrMsg(obj);
				}else if(NodeConfig.NODE_MSG_TYPE_QUESTION.equals(obj.getInteger("node_msg_type"))) {
					nodeConfig.setQuestionMsg(obj);
				}else if(NodeConfig.NODE_MSG_TYPE_RECOMMEND.equals(obj.getInteger("node_msg_type"))) {
					nodeConfig.setRecommendMsg(obj);
				}else if(NodeConfig.NODE_MSG_TYPE_URGE.equals(obj.getInteger("node_msg_type"))) {
					nodeConfig.setUrgeMsg(obj);
				}
				
			}
			next.getValue().setConfig(nodeConfig);
			next.getValue().setNodeManager(nodeManager);
		}
		logger.info("初始化节点集合="+JSON.toJSONString(nodeManager.getEm()));
	}
	
	private NodeConfig getNodeConfig(Integer nodeId) {
		JSONObject nodeConfig = nodeConfigDao.getNodeConfig(nodeId);
		NodeConfig config = new NodeConfig();
		config.setNodeId(nodeId);
		config.setRightNodeId(nodeConfig.getInteger("right_node_id"));
		config.setLeftNodeId(nodeConfig.getInteger("left_node_id"));
		return config;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		resetNodeConfig();
	}

}
