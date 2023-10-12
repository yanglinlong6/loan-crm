package com.loan.cps.service;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.*;
import com.loan.cps.controller.AptitudeUtils;
import com.loan.cps.controller.CustController;
import com.loan.cps.dao.UserAptitudeDao;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.service.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@Component
public class IUserAptitudeService implements UserAptitudeService{

	private static final Logger LOG = LoggerFactory.getLogger(IUserAptitudeService.class);

	@Autowired
	UserAptitudeDao dao;
	
	@Override
	public UserAptitudePO get(String userId) {
		UserAptitudePO userAptitudePO = dao.get(userId);
		if(userAptitudePO == null || StringUtils.isEmpty(userAptitudePO.getUserId())) {
			userAptitudePO = new UserAptitudePO();
			userAptitudePO.setUserId(userId);
			userAptitudePO.setCreateBy("ai_cps");
			Date date = new Date();
			userAptitudePO.setCreateDate(date);
			userAptitudePO.setUpdateBy("ai_cps");
			userAptitudePO.setUpdateDate(date);
			dao.create(userAptitudePO);
		}
		return userAptitudePO;
	}

	@Override
	public void update(UserAptitudePO po) {
		dao.update(po);
	}

	@Autowired
	CacheService cacheService;

	@Override
	public R add(UserAptitudePO po) {
		checkUserAptitude(po);
		UserAptitudePO byMD5 = dao.getByMobileMD5(po.getMd5());
		if(null != byMD5){
			return R.fail("1","您的申请已提交,请勿重复申请",false);
		}
		po.setLevel(7);
		String city = po.getCity();
		if("市".equals(city)) {
			return R.fail("3","请正确选择城市");
		}
		String substring = city.substring(0, city.indexOf("市")+1);
		po.setCity(substring);


		if(null == po.getCar() || !JudgeUtil.in(po.getCar(),0,1)){
			po.setCar(0);
		}
		if(StringUtils.isEmpty(po.getPublicFund())){
			po.setPublicFund("没有公积金");
		}else if("1".equals(po.getPublicFund())){
			po.setPublicFund("有，个人月缴300-800元");
		}else{
			if(po.getPublicFund().contains("有，")) {
				po.setPublicFund("有，个人月缴300-800元");
			}else {
				po.setPublicFund("没有公积金");
			}
		}
		// 1:本地有房，外地无房 2:本地有房，外地有房 3本地无房，外地有房 4本地无房，外地无房
		if (null != po.getHouseExtension()) {
			if (1 != po.getHouseExtension() && 1 == po.getHouse()) {
				po.setHouseExtension(1);
			}else if (1 == po.getHouseExtension() && 1 == po.getHouse()) {
				po.setHouseExtension(2);
			}else if (1 == po.getHouseExtension() && 1 != po.getHouse()) {
				po.setHouseExtension(3);
			}else if (1 != po.getHouseExtension() && 1 != po.getHouse()) {
				po.setHouseExtension(4);
			}
			if (1 == po.getHouseExtension() || 2 == po.getHouseExtension() || 3 == po.getHouseExtension()) {
				po.setHouse(1);
			} else if (4 == po.getHouseExtension()) {
				po.setHouse(4);
			}
		}else {
			if(null == po.getHouse() || !JudgeUtil.in(po.getHouse(),0,1)){
				po.setHouse(0);
			}
		}
		if(null == po.getCompany() || !JudgeUtil.in(po.getCompany(),0,1)) {
			po.setCompany(0);
		}

		if(po.getGetwayIncome()==null || !JudgeUtil.in(po.getGetwayIncome(),0,1)) {
			po.setGetwayIncome(0);
		}

		if(po.getInsurance()==null || !JudgeUtil.in(po.getInsurance(),0,1)) {
			po.setInsurance(0);
		}

		po.setUserId(UUID.randomUUID().toString().replace("-", ""));
		po.setCreateBy(CPSConstant.CreateBy.LOAN);//web-信贷,house-房抵,car-车抵,credit-信用卡逾期
		po.setWeight(AptWeightUtil.getAptWeight(po));
		String value = cacheService.getValue(CPSConstant.Redis.Advertising.FIELD,CPSConstant.Redis.Advertising.CITY);
		// 判断是否是在投城市,不是则标记为异地
		if(!StringUtils.isEmpty(value)){
			if(!JudgeUtil.in(po.getCity(),value.split(","))){
				po.setLevel(99);
			}
		}
		dao.add(po);
		if(JudgeUtil.in(po.getChannel(),CustController.channels)){
			String result = HttpUtil.getForObject("http://10.0.1.164:8080/dis/send?userId="+po.getUserId());
			JSONObject resultJson = JSONObject.parseObject(result);
			if(resultJson.getString("code").equals("1")){
				return R.fail("3","客户已存在");
			}
			return R.ok();
		}
		HttpUtil.syncHttpGet("http://10.0.1.164:8080/dis/send?userId="+po.getUserId());
		return R.ok();
	}

	@Override
	public R addShengBei(UserAptitudePO po) {
		checkUserAptitude(po);
		UserAptitudePO byMobile = dao.getByMobile(po.getMobile());
		if(byMobile != null && !StringUtils.isEmpty(po.getMobile())) {
			return R.fail("0","您的申请已提交",false);
		}else {
			po.setLevel(7);
		}

		String city = po.getCity();
		if("市".equals(city)) {
			return R.fail("3","请正确选择城市");
		}
		String substring = city.substring(0, city.indexOf("市")+1);
		po.setCity(substring);


		if(null == po.getCar() || !JudgeUtil.in(po.getCar(),0,1)){
			po.setCar(0);
		}

		if(StringUtils.isEmpty(po.getPublicFund())){
			po.setPublicFund("没有公积金");
		}else if("1".equals(po.getPublicFund())){
			po.setPublicFund("有，个人月缴300-800元");
		}else{
			if(po.getPublicFund().contains("有，")) {
				po.setPublicFund("有，个人月缴300-800元");
			}else {
				po.setPublicFund("没有公积金");
			}
		}

		// 1:本地有房，外地无房 2:本地有房，外地有房 3本地无房，外地有房 4本地无房，外地无房
		if (null != po.getHouseExtension()) {
			if (1 != po.getHouseExtension() && 1 == po.getHouse()) {
				po.setHouseExtension(1);
			}else if (1 == po.getHouseExtension() && 1 == po.getHouse()) {
				po.setHouseExtension(2);
			}else if (1 == po.getHouseExtension() && 1 != po.getHouse()) {
				po.setHouseExtension(3);
			}else if (1 != po.getHouseExtension() && 1 != po.getHouse()) {
				po.setHouseExtension(4);
			}
			if (1 == po.getHouseExtension() || 2 == po.getHouseExtension() || 3 == po.getHouseExtension()) {
				po.setHouse(1);
			} else if (4 == po.getHouseExtension()) {
				po.setHouse(4);
			}
		}else {
			if(null == po.getHouse() || !JudgeUtil.in(po.getHouse(),0,1)){
				po.setHouse(0);
			}
		}
		if(null == po.getCompany() || !JudgeUtil.in(po.getCompany(),0,1)) {
			po.setCompany(0);
		}

		if(po.getGetwayIncome()==null || !JudgeUtil.in(po.getGetwayIncome(),0,1)) {
			po.setGetwayIncome(0);
		}

		if(JudgeUtil.in(po.getShebao(),1)){
			po.setGetwayIncome(1);
		}

		if(po.getInsurance()==null || !JudgeUtil.in(po.getInsurance(),0,1)) {
			po.setInsurance(0);
		}

		po.setUserId(UUID.randomUUID().toString().replace("-", ""));
		po.setCreateBy(CPSConstant.CreateBy.LOAN);//web-信贷,house-房抵,car-车抵,credit-信用卡逾期
		po.setWeight(AptWeightUtil.getAptWeight(po));
		dao.add(po);

		// 推送给机构
		String result = HttpUtil.getForObject("http://10.0.5.134:8080/dis/send2?userId="+po.getUserId());

		return R.ok(JSONUtil.toJSON(result));
	}

	@Override
	public R addCredit(UserAptitudePO po) {
		UserAptitudePO byMobile = dao.getByMobile(po.getMobile());
		if(byMobile!=null&&!StringUtils.isEmpty(byMobile.getMobile())) {
			return R.fail("0","您的申请已提交");
		}
		if(po.getCar()==2) {
			po.setCar(3);
		}else if(po.getCar()==3) {
			po.setCar(2);
		}
		po.setLevel(4);
		po.setUserId(UUID.randomUUID().toString().replace("-", ""));
		po.setCreateBy(CPSConstant.CreateBy.CREDIT);//web-信贷,house-房抵,car-车抵,credit-信用卡逾期
		dao.add(po);
		return R.ok();
	}

	public void checkUserAptitude(UserAptitudePO po) {
		if(StringUtils.isEmpty(po.getCity())) {
			throw new RuntimeException("城市不能为空");
		}
		if(!po.getCity().endsWith("市")){
			po.setCity(po.getCity()+"市");
		}
//		List<String> ss = new ArrayList<String>();
//		ss.add(po.getCity());
//		JSONObject byName = CityUtil.getByName(ss, 2);
//		if(byName==null) {
//			throw new RuntimeException("城市不存在");
//		}
		if(StringUtils.isEmpty(po.getLoanAmount())) {
			throw new RuntimeException("金额不能为空");
		}
		if(StringUtils.isEmpty(po.getName())) {
			throw new RuntimeException("姓名不能为空");
		}
		if(StringUtils.isEmpty(po.getMobile())) {
			throw new RuntimeException("手机不能为空");
		}
		if(!AptitudeUtils.checkMoblie(po.getMobile())) {
			throw new RuntimeException("手机格式错误");
		}
		if(po.getAge() == null) {
			throw new RuntimeException("年龄不能为空");
		}
		if(po.getAge() <25 || po.getAge()>55) {
			throw new RuntimeException("年龄不符合");
		}
//		Integer valueOf = Integer.valueOf(po.getPublicFund() == null?"0":po.getPublicFund());
//		if(!JudgeUtil.in(valueOf, 0,1,2) ||!JudgeUtil.in(po.getCar(), 0,1,2)||!JudgeUtil.in(po.getHouse(), 0,1,2)||!JudgeUtil.in(po.getCompany(), 0,1,2)||!JudgeUtil.in(po.getGetwayIncome(), 0,1,2)
//				||!JudgeUtil.in(po.getInsurance(), 0,1,2)||!JudgeUtil.in(po.getGender(), 0,1,2)) {
//			throw new RuntimeException("参数错误");
//		}
	}
	
//	public boolean checkMoblie(String mobile) {
//		String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
//		boolean m = false;
//		if(mobile.length()==11) {
//			m = Pattern.matches(regex, mobile);
//		}
//		return m;
//	}

	@Override
	public UserAptitudePO getByMobile(String mobile) {
		return dao.getByMobile(mobile);
	}



}
