package com.loan.cps.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.AIUtil;
import com.loan.cps.common.WXCenterUtil;
import com.loan.cps.common.WechatMsgFactory;
import com.loan.cps.entity.Session;

public class NameNode extends Node{
	
	private static String[] SURNAME = {"赵","孙","李","周","吴","郑","王","冯","陈","褚","卫","蒋","沈","杨",
			               "朱","芦","秦","许","何","吕","张","孔","曹","严","魏","姜",
			               "戚","邹","喻","柏","窦","章","潘","葛","奚","范","彭","郎",
			               "韦","昌","苗","凤","俞","任","袁","柳","酆","鲍","史","唐",
			               "费","廉","岑","薛","雷","贺","倪","汤","滕","殷","罗","毕","郝","邬",
			               "于","傅","卞","康","伍","余","卜","顾","孟","黄",
			               "穆","萧","尹","姚","邵","湛","汪","祁","毛","禹","狄","臧",
			               "计","伏","戴","谈","宋","茅","庞","熊","纪","舒","屈","项","祝","董","梁",
			               "杜","阮","蓝","闵","席","季","麻","贾","路","娄","危","童","颜","郭",
			               "梅","盛","刁","锺","徐","邱","骆","高","夏","蔡","樊","胡","凌","霍",
			               "虞","万","支","柯","昝","管","卢","莫","经","房","裘","缪","干","解","应","宗",
			               "丁","宣","贲","邓","郁","单","洪","诸","崔","钮","龚",
			               "程","嵇","邢","滑","裴","陆","荣","翁","荀","羊","於","惠","甄","麴","家","封",
			               "芮","羿","储","靳","汲","邴","糜","松","井","段","富","巫","乌","焦","巴","弓",
			               "牧","隗","侯","宓","蓬","全","郗","班","仰","秋","仲","伊","宫",
			               "宁","仇","栾","甘","钭","历","戎","祖","武","刘","景","詹","束",
			               "幸","司","韶","郜","黎","蓟","溥","印","宿","怀","蒲","邰","鄂",
			               "索","咸","籍","赖","卓","蔺","屠","蒙","池","乔","郁","胥","能","苍",
			               "闻","莘","党","翟","谭","贡","劳","逄","姬","申","扶","堵","冉","宰","郦","雍",
			               "却","璩","桑","桂","濮","寿","通","边","扈","燕","冀","僪","浦","尚",
			               "温","庄","晏","柴","瞿","阎","充","慕","连","茹","习","宦","艾","鱼","容",
			               "古","易","慎","戈","廖","庾","终","暨","居","衡","步","都","耿","满","弘",
			               "匡","国","文","寇","广","禄","阙","东","欧","殳","沃","利","蔚","越","夔","隆",
			               "师","巩","厍","聂","晁","勾","敖","融","冷","訾","辛","阚","那","简","饶","空",
			               "曾","毋","沙","乜","养","鞠","须","丰","巢","关","蒯","相","查","后","荆",
			               "游","竺","权","逮","盍","益","桓","公",
			               "万俟","司马","上官","欧阳","夏侯","诸葛","闻人","东方","赫连","皇甫","尉迟",
			               "公羊","澹台","公冶","宗政","濮阳","淳于","单于","太叔","申屠","公孙","仲孙",
			               "轩辕","令狐","钟离","宇文","长孙","慕容","司徒","司空","召","舜","叶赫那拉",
			               "岳","寸","贰","皇","侨","彤","竭","端","赫","实","甫","集","象","翠",
			               "辟","典","良","函","芒","苦","其","京","夕","之","章佳","那拉","冠","宾",
			               "香","依尔根觉罗","依尔觉罗","萨嘛喇","赫舍里","额尔德特","萨克达",
			               "钮祜禄","他塔喇","喜塔腊","讷殷富察","叶赫那兰","库雅喇","瓜尔佳","舒穆禄",
			               "爱新觉罗","索绰络","纳喇","乌雅","范姜","碧鲁","张廖","张简","图门","太史","公叔",
			               "乌孙","完颜","马佳","佟佳","富察","费莫","蹇","称","诺","繁","戊","朴",
			               "毓","鉏","税","荤","靖","绪","愈","硕","巧","枚","撒","泰",
			               "亥","绍","以","壬","森","斋","释","奕","姒","真","穰",
			               "翦","闾","漆","贵","贯","旁","崇","栋","告","休","褒","谏","锐","皋","闳",
			               "歧","禾","示","委","钊","频","嬴","呼","威","昂","冒",
			               "系","抄","化","莱","校","么","抗","祢","綦","悟","宏","庚","务","敏",
			               "捷","拱","兆","丙","畅","苟","随","类","卯","俟","答","乙","允","甲",
			               "留","尾","佼","玄","乘","裔","延","植","环","矫","赛","昔","侍","度","旷","遇",
			               "偶","前","由","咎","塞","敛","受","泷","袭","衅","叔","圣","御","夫","仆","镇",
			               "藩","邸","府","掌","首","员","焉","戏","智","尔","凭","悉","进","笃","厚",
			               "肇","资","合","仍","衷","哀","刑","仵","圭","夷","徭","蛮",
			               "汗","孛","乾","帖","罕","洛","淦","洋","邶","郸","郯","邗","邛","剑","虢","隋",
			               "蒿","茆","菅","苌","树","桐","锁","钟","机","盘","铎","斛","线","针","箕",
			               "庹","绳","磨","蒉","瓮","弭","疏","牵","浑","恽","势","世","仝","同","蚁",
			               "止","戢","睢","冼","种","涂","肖","己","泣","潜","卷","脱","谬","蹉","赧","浮",
			               "顿","说","次","错","念","夙","斯","完","丹","表","聊","源","寻","展",
			               "户","闭","才","书","学","愚","性","雪","霜","烟","寒",
			               "字","桥","板","斐","独","诗","嘉","善","揭","祈","析","赤","紫","青",
			               "柔","刚","奇","拜","佛","陀","弥","阿","素","僧","隐","仙","隽","宇","祭",
			               "酒","淡","塔","琦","闪","始","星","接","波","碧","速","禚","腾","潮",
			               "镜","似","澄","潭","謇","纵","渠","奈","春","濯","沐","茂",
			               "檀","藤","枝","检","折","登","驹","骑","貊","鹿","雀","野","禽",
			               "宜","鲜","粟","栗","豆","帛","官","布","藏","宝",
			               "盈","庆","及","普","建","营","巨","望","希","道","载","声","漫","犁","力",
			               "贸","勤","革","改","兴","亓","睦","修","信","闽","守","坚","勇","练",
			               "尉","士","旅","令","将","旗","军","行","奉","敬","恭","仪","母","堂","丘",
			               "礼","慈","孝","理","伦","卿","问","永","辉","位","让","尧","依","犹","介",
			               "承","市","所","苑","杞","剧","第","零","谌","招","续","达","忻","鄞",
			               "迟","候","宛","励","粘","萨","邝","覃","辜","初","楼","城","区","局","台","原",
			               "考","妫","纳","泉","清","德","卑","麦","曲","竹","福","第五",
			               "佟","年","笪","谯","哈","墨","连","南宫","赏","伯","佴","佘","牟","商","西门",
			               "东门","左丘","梁丘","琴","后","况","亢","缑","帅","微生","羊舌","海","归","呼延",
			               "南门","东郭","百里","钦","鄢","汝","法","闫","楚","晋","谷梁","宰父","夹谷","拓跋",
			               "壤驷","乐正","漆雕","公西","巫马","端木","颛孙","子车","督","仉","司寇","亓官","三小",
			               "鲜于","锺离","盖","逯","库","郏","逢","阴","薄","厉","稽","闾丘","公良","段干",
			               "操","瑞","眭","运","摩","伟","迮"};
	
	private static final List<String> SURNAME_LIST = new ArrayList<String>(Arrays.asList(SURNAME));


//	@Override
//	public NodeResult answerHandle(Session session, JSONObject userMsg) {
//		String menuid = userMsg.getString("bizmsgmenuid");
//		NodeResult result = new NodeResult();
//		result.setState(NodeResult.NODE_FAIL);
//		if (StringUtils.isEmpty(menuid)) {
//			String content = userMsg.getString("Content").trim();
//			if (!StringUtils.isEmpty(content)) {
//				if (checkIsName(content)) {
//					if(Pattern.matches("[\u4e00-\u9fa5]+", content)&&content.length()<10) {
//						JSONObject qualification = new JSONObject();
//						qualification.put("name", content);
//						qualification.put("level", 4);
//						result.setQualification(qualification);
//						result.setState(NodeResult.NODE_SUCCESS);
//						nodeManager.getNode(super.getConfig().getRightNodeId(session.getLocation())).sendQuestion(session);
//					}else {
//						WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
//					}
//				} else {
//					AIUtil.answerAIMsg(content, userMsg,session);
//					this.sendQuestion(session);
//				}
//			}
//		}
//		return result;
//	}
	
	public boolean checkIsName(String content) {
		for(String surname:SURNAME_LIST) {
			if(content.startsWith(surname)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public NodeResult parseQualification(JSONObject menu, NodeResult result) {
		JSONObject qualification = new JSONObject();
		qualification.put("name", menu.getString("name"));
		qualification.put("level", 4);
		result.setQualification(qualification);
		result.setState(NodeResult.NODE_SUCCESS);
		return result;
	}

	@Override
	public JSONObject parseMsg(Session session, JSONObject userMsg) {
		String content = userMsg.getString("Content").trim();
		JSONObject qualification = new JSONObject();
		if (!StringUtils.isEmpty(content)) {
			if (checkIsName(content)) {
				if(Pattern.matches("[\u4e00-\u9fa5]+", content)&&content.length()<10) {
					qualification.put("name", content);
					qualification.put("choose", 1);
				}else {
					WXCenterUtil.sendCustMsg(WechatMsgFactory.createCustMsg(super.getConfig().getErrMsg(), userMsg.getString("FromUserName")), userMsg.getString("ToUserName"), userMsg.getString("domain2"));
					return null;
				}
			} else {
				AIUtil.answerAIMsg(content, userMsg,session);
				this.sendQuestion(session);
				return null;
			}
		}
		return qualification;
	}

}
