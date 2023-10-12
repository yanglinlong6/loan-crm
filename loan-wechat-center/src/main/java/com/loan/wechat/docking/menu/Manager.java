package com.loan.wechat.docking.menu;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loan.wechat.common.HttpUtil;

public class Manager {

	public static void main(String[] args) {
		JSONObject wehcat = getWehcat();
		String postForObject = HttpUtil.postForObject(String.format("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=%s", wehcat.getString("token")), JSON.toJSONString(createMenu(wehcat.getString("appId"),wehcat.getString("domain2"))));
		JSONObject parseObject = JSON.parseObject(postForObject);
		if(parseObject.getInteger("errcode")==0) {
			System.out.println("菜单创建成功");
		}else {
			System.out.println("菜单创建失败："+postForObject);
		}
	}
	
	public static Menu createMenu(String appid,String domain2) {
		
//		Button frist = new Button();
//		frist.setType(MenuConstant.Type.CLICK);
//		frist.setName("在线客服");
//		frist.setKey("PROCEED");
//		
		Button Frist = new Button();
		Frist.setType(MenuConstant.Type.VIEW);
		Frist.setName("银行大额入口");
		Frist.setUrl(String.format("http://%s/h5/v8/view-03-24.html?channel=guanzhu_"+appid,domain2));

		Button second = new Button();
		second.setName("👆贷款提现");
		
		Button secondFrist = new Button();
		secondFrist.setType(MenuConstant.Type.VIEW);
		secondFrist.setName("贷款大全");
		secondFrist.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/product/list&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
//		Button secondSecond = new Button();
//		secondSecond.setType(MenuConstant.Type.VIEW);
//		secondSecond.setName("专属贷款方案");
//		secondSecond.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/plan/index&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
		Button secondThree = new Button();
		secondThree.setType(MenuConstant.Type.VIEW);
		secondThree.setName("预估额度评测");
		secondThree.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/index/test&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
		second.setSub_button(new Button[]{secondFrist,secondThree});
		
		Button three = new Button();
		three.setName("服务中心");
		
		Button threeFrist = new Button();
		threeFrist.setType(MenuConstant.Type.VIEW);
		threeFrist.setName("个人中心");
		threeFrist.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/user/center&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
		Button threeSecond = new Button();
		threeSecond.setType(MenuConstant.Type.VIEW);
		threeSecond.setName("投诉反馈");
		threeSecond.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/suggest/index&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
//		Button threeThree = new Button();
//		threeThree.setType(MenuConstant.Type.VIEW);
//		threeThree.setName("黑名单检测");
//		threeThree.setUrl("https://m.tianxiaxinyong.cn/cooperation/crp-v2/index.html?channel=M3ZNbW9TQmFqbUUrdXZYR3R5UmoyQT09");
		
		three.setSub_button(new Button[] {threeFrist,threeSecond});
		
		Button card = new Button();
		card.setType(MenuConstant.Type.VIEW);
		card.setName("信用卡超市");
		card.setUrl(String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=http://%s/card/?coding=7002&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect",appid,domain2));
		
		return new Menu(new Button[] {Frist});
	}
	
	public static JSONObject getWehcat() {
		String forObject = HttpUtil.getForObject("http://htyj.daofen100.com/center/wxcenter/wechat/get?domain2=dezy.daofen100.com");
		JSONObject parseObject = JSON.parseObject(forObject);
		if("0".equals(parseObject.getString("code"))) {
			return parseObject.getJSONObject("o");
		}else {
			throw new RuntimeException("wechat Non-existent");
		}
	}
	
}
