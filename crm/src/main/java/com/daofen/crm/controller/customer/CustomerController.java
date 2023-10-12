package com.daofen.crm.controller.customer;

import java.util.Random;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.customer.CustomerFacade;
import com.daofen.crm.service.customer.ServerEventListener;
import com.daofen.crm.service.customer.dao.MediaCustomerBO;
import com.daofen.crm.service.customer.model.CallLogPO;
import com.daofen.crm.service.customer.model.CirculationLogPO;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.RemarkPO;

@RestController
public class CustomerController extends AbstractController{
	
	@Autowired
	private CustomerFacade customerFacade;
	
	@RequestMapping("/customer/my/list")
    @ResponseBody
    public ResultVO getMyCustomerList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getMyCustomerList(pageVO));
    }
	
	@RequestMapping("/customer/circulation/list")
    @ResponseBody
    public ResultVO getCirculationCustomerList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getCirculationCustomerList(pageVO));
    }
	
	@RequestMapping("/customer/add")
    @ResponseBody
    public ResultVO addCustomer(@RequestBody()CustomerPO po){
		customerFacade.addCustomer(po);
        return this.success();
    }
	
	@RequestMapping("/customer/media/receive")
    @ResponseBody
    public ResultVO receiveCustomer(@RequestBody() MediaCustomerBO bo){
		customerFacade.addMeidaCustomer(bo);
        return this.success();
    }
	
	@RequestMapping("/customer/repeat/check")
    @ResponseBody
    public ResultVO repeat(@RequestBody() MediaCustomerBO bo){
		if(customerFacade.checkMobile(bo)) {
			return this.success("手机号码未在库",null);
		}
        return this.failed("手机号码重复", null);
    }
	
	@RequestMapping("/customer/public/pool/join")
    @ResponseBody
    public ResultVO joinPublicPool(@RequestParam String id){
		if(!Pattern.matches("\\d+[,\\d+]*", id)) {
			return this.failed("params err", null);
		}
		String[] split = id.split(",");
		for(String s:split) {
			customerFacade.joinPublicPool(Long.valueOf(s));
		}
//		customerFacade.joinPublicPool(id);
        return this.success();
    }
	
	@RequestMapping("/customer/business/process")
    @ResponseBody
    public ResultVO processCustomer(@RequestBody()CustomerPO po){
		customerFacade.processCustomer(po);
        return this.success();
    }
	
	@RequestMapping("/customer/distribution/set")
    @ResponseBody
    public ResultVO distributionCust(@RequestBody()JSONObject obj){
		String string = obj.getString("id");
		Long businessId = obj.getLong("businessId");
		if(StringUtils.isEmpty(string)||businessId==null||!Pattern.matches("\\d+[,\\d+]*", string)) {
			return this.failed("params err", null);
		}
		String[] split = string.split(",");
		for(String s:split) {
			CustomerPO po = new CustomerPO();
			po.setBusinessId(businessId);
			po.setId(Long.valueOf(s));
			customerFacade.distributionCust(po);
		}
        return this.success();
    }
	
	@RequestMapping("/customer/my/join")
    @ResponseBody
    public ResultVO joinMyCustomer(@RequestBody()JSONObject obj){
		String string = obj.getString("id");
		if(StringUtils.isEmpty(string) || !Pattern.matches("\\d+[,\\d+]*", string)) {
			return this.failed("params err", null);
		}
		String[] split = string.split(",");
		for(String s:split) {
			CustomerPO po = new CustomerPO();
			po.setId(Long.valueOf(s));
			customerFacade.joinMyCustomer(po);
		}
        return this.success();
    }
	
	@RequestMapping("/customer/distribution/again/set")
    @ResponseBody
    public ResultVO distributionAgainCust(@RequestBody()JSONObject obj){
		String string = obj.getString("id");
		Long businessId = obj.getLong("businessId");
		if(StringUtils.isEmpty(string)||businessId==null||!Pattern.matches("\\d+[,\\d+]*", string)) {
			return this.failed("params err", null);
		}
		String[] split = string.split(",");
		for(String s:split) {
			CustomerPO po = new CustomerPO();
			po.setBusinessId(businessId);
			po.setId(Long.valueOf(s));
			customerFacade.distributionAgainCust(po);
		}
        return this.success();
    }
	
	@RequestMapping("/customer/remarks/get")
    @ResponseBody
    public ResultVO getCustomerRemarks(@RequestBody()PageVO<RemarkPO> vo){
        return this.success(customerFacade.getCustomerRemarks(vo));
    }
	
	@RequestMapping("/customer/circulation/log/get")
    @ResponseBody
    public ResultVO getCustomerCirculationLog(@RequestBody()PageVO<CirculationLogPO> vo){
        return this.success(customerFacade.getCustomerCirculationLog(vo));
    }
	
	@RequestMapping("/customer/my/uncontacted/num/get")
    @ResponseBody
    public ResultVO getUncontactedUsersNum(@RequestParam(required = false) Long id){
        return this.success(customerFacade.getUncontactedUsersNum(id));
    }
	
	@RequestMapping("/customer/list/get")
    @ResponseBody
    public ResultVO getCustomerList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getCustomerList(pageVO));
    }
	
	@RequestMapping("/customer/team/list/get")
    @ResponseBody
    public ResultVO getTeamCustomerList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getTeamCustomerList(pageVO));
    }
	
	@RequestMapping("/customer/wait/pool/list/get")
    @ResponseBody
    public ResultVO getWaitCustomerList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getWaitCustomerList(pageVO));
    }
	
	@RequestMapping("/customer/public/pool/list/get")
    @ResponseBody
    public ResultVO getPublicPoolList(@RequestBody()PageVO<CustomerPO> pageVO){
        return this.success(customerFacade.getPublicPoolList(pageVO));
    }
	
	@RequestMapping("/customer/team/uncontacted/num/get")
    @ResponseBody
    public ResultVO getUncontactedTeamUsersNum(@RequestParam(required = false) Long id){
        return this.success(customerFacade.getUncontactedTeamUsersNum(id));
    }
	
	@RequestMapping("/customer/remark/add")
    @ResponseBody
    public ResultVO addRemark(@RequestBody() RemarkPO po){
		customerFacade.addRemark(po);
        return this.success();
    }
	
	@RequestMapping("/customer/remark/del")
    @ResponseBody
    public ResultVO delRemark(@RequestParam Long id){
		customerFacade.delRemark(id);
        return this.success();
    }
	
	@RequestMapping("/customer/call/log/add")
    @ResponseBody
    public ResultVO addCallLog(@RequestBody() CallLogPO po){
		customerFacade.addCallLog(po);
        return this.success();
    }
	
	@RequestMapping("/customer/media/report/get")
    @ResponseBody
    public ResultVO selMediaReport(@RequestBody() JSONObject params){
        return this.success(customerFacade.selMediaReport(params));
    }
	
	@RequestMapping("/customer/report/get")
    @ResponseBody
    public ResultVO selCustReport(@RequestBody() JSONObject params){
        return this.success(customerFacade.selCustReport(params));
    }
	
	@RequestMapping(value = "/customer/sse/push")
    public SseEmitter getNewCust(HttpServletRequest req){
		String loginCookie = LoginUtil.getLoginCookie(req);
		if(StringUtils.isEmpty(loginCookie)) {
			return null;
		}
		SseEmitter ss = new SseEmitter(-1l);
		ServerEventListener.addSseEmitter(loginCookie, ss);
		return ss;
    }
	
	@RequestMapping(value = "/customer/sse/send")
    public ResultVO send(HttpServletRequest req){
		ServerEventListener.sendNewCustMsg(LoginUtil.getLoginCookie(req));
		return this.success();
    }
	
	@RequestMapping("/customer/matrix/get")
    public String getMatrix(@RequestBody() JSONObject params){
        return JSON.toJSONString(customerFacade.getMatrix(params));
    }
	
	@RequestMapping("/customer/matrix/replace")
	@ResponseBody
    public ResultVO replaceMatrix(){
		customerFacade.replaceMatrix();
        return this.success();
    }
	
}
