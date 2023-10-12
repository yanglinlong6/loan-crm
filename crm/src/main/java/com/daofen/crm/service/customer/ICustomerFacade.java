package com.daofen.crm.service.customer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.daofen.crm.base.*;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.CompanyService;
import com.daofen.crm.service.company.model.CompanyPO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.customer.dao.MediaCustomerBO;
import com.daofen.crm.service.customer.model.*;
import com.daofen.crm.service.media.MediaService;
import com.daofen.crm.service.media.SourceService;
import com.daofen.crm.service.media.model.MediaPO;
import com.daofen.crm.service.media.model.SourcePO;
import com.daofen.crm.service.sms.AliyunSms;
import com.daofen.crm.utils.JSONUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ICustomerFacade implements CustomerFacade{
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CirculationLogService circulationLogService;
	
	@Autowired
	private RemarkService remarkService;
	
	@Autowired
	private CallLogService callLogService;
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired
	private SourceService sourceService;
	
	@Autowired
	private CompanyService companyService;
	
	private static final Logger LOG = LoggerFactory.getLogger(ICustomerFacade.class);
	
	@Override
	public PageVO<CustomerPO> getMyCustomerList(PageVO<CustomerPO> vo) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		param.setBusinessId(loginThreadLocal.getId());
		param.setDataState(CrmConstant.Customer.DataState.ALLOCATED);
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}
	
	@Transactional
	@Override
	public void addCustomer(CustomerPO po) {
		if(po.getMobile()==null) {
			throw new CrmException("000001", "手机号不能为空");
		}
		if(!checkMoblie(po.getMobile())) {
			throw new CrmException("000001", "手机号格式错误");
		}
		if(po.getCity()==null) {
			throw new CrmException("000001", "城市不能为空");
		}
		if(po.getGender()==null) {
			throw new CrmException("000001", "性别不能为空");
		}
		if(po.getAge()==null) {
			throw new CrmException("000001", "年龄不能为空");
		}
		if(po.getAge()<=25 ||po.getAge()>=55) {
			throw new CrmException("000001", "年龄不符合");
		}
		if(po.getQuota()==null) {
			throw new CrmException("000001", "贷款额度不能为空");
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setBusinessId(loginThreadLocal.getId());
		po.setMobileMd5(DigestUtil.digestMd5LowerCase32(po.getMobile()));
		po.setMobileCity(MobileLocationUtil.get(po.getMobile()).getString("city"));
		po.setCompanyId(loginThreadLocal.getCompanyId());
		customerService.addCustomer(po);
		CallLogPO callLog = new CallLogPO();
		callLog.setBusinessId(loginThreadLocal.getId());
		callLog.setCustomerId(po.getId());
		callLog.setType(0);
		callLog.setCallTime(new Date());
		callLog.setCreateBy(loginThreadLocal.getName());
		callLog.setCreateDate(new Date());
		callLogService.addCallLog(callLog);
	}
	
	public boolean checkMoblie(String mobile) {
		String regex = "^((13[0-9])|(14[0-9])|(16[0-9])|(15[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$";
		boolean m = false;
		if(mobile.length()==11) {
			m = Pattern.matches(regex, mobile);
		}
		return m;
	}
	
	@Transactional
	@Override
	public void joinPublicPool(Long customerId) {
		CustomerPO po = new CustomerPO();
		po.setId(customerId);
		po.setDataState(CrmConstant.Customer.DataState.PUBLIC_POOL);
		po.setBusinessId(0l);
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setUpdateBy(loginThreadLocal.getName());
		po.setUpdateDate(new Date());
		customerService.updateCustomer(po);
		CirculationLogPO log = new CirculationLogPO();
		log.setBusinessId(loginThreadLocal.getId());
		log.setCustomerId(customerId);
		log.setType(CrmConstant.Customer.CirculationState.JOIN_PUBLIC_POOL);
		log.setCreateBy(loginThreadLocal.getName());
		log.setCreateDate(new Date());
		circulationLogService.addCirculationLog(log);
	}
	

	@Override
	public void processCustomer(CustomerPO po) {
		po.setBusinessId(null);//保证不在此方法内不被修改所属业务员
		po.setDataState(null);//保证不在此方法内不被修改数据状态
		if(po.getLevel()!=null&&(po.getLevel()<0||po.getLevel()>10)) {
			throw new CrmException("000001", "星级不符合");
		}
		if(po.getState()!=null&&(po.getState()<0||po.getState()>20)) {
			throw new CrmException("000001", "业务状态不符合");
		}
		if(po.getQualification()!=null&&po.getQualification().length()<=10) {
			throw new CrmException("000001", "资质信息需要输入10个字以上");
		}
		if(po.getLevel()!=null||po.getState()!=null||po.getQualification()!=null) {
			customerService.updateCustomer(po);
		}
		if(StringUtils.isNotBlank(po.getRemark())) {
			CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
			RemarkPO rpo = new RemarkPO();
			rpo.setContent(po.getRemark());
			rpo.setCustomerId(po.getId());
			rpo.setBusinessId(loginThreadLocal.getId());
			rpo.setCreateBy(loginThreadLocal.getName());
			rpo.setCreateDate(new Date());
			remarkService.addRemark(rpo);
		}
	}

	@Override
	public PageVO<RemarkPO> getCustomerRemarks(PageVO<RemarkPO> vo) {
		return remarkService.getRemarkList(vo);
	}

	@Override
	public PageVO<CirculationLogPO> getCustomerCirculationLog(PageVO<CirculationLogPO> vo) {
		return circulationLogService.getCirculationLogPage(vo);
	}

	@Override
	public List<UncontactedVO> getUncontactedUsersNum(Long id) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		return customerService.getUncontactedUsersNum(loginThreadLocal.getId());
	}

	@Override
	public PageVO<CustomerPO> getCustomerList(PageVO<CustomerPO> vo) {
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		param.setCompanyId(loginThreadLocal.getCompanyId());
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}
	
	@Override
	public PageVO<CustomerPO> getTeamCustomerList(PageVO<CustomerPO> vo) {
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		param.setCompanyId(loginThreadLocal.getCompanyId());
		param.setTeamId(loginThreadLocal.getTeamId());
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}

	@Override
	public PageVO<CustomerPO> getCirculationCustomerList(PageVO<CustomerPO> vo) {
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		param.setBusinessId(loginThreadLocal.getId());
		param.setDataState(CrmConstant.Customer.DataState.AGAIN);
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}

	@Override
	public PageVO<CustomerPO> getWaitCustomerList(PageVO<CustomerPO> vo) {
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		param.setCompanyId(loginThreadLocal.getCompanyId());
		param.setDataState(CrmConstant.Customer.DataState.WAIT_POOL);
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}

	@Override
	public PageVO<CustomerPO> getPublicPoolList(PageVO<CustomerPO> vo) {
		CustomerPO param = vo.getParam();
		if(param ==null) {
			param = new CustomerPO();
		}
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		param.setCompanyId(loginThreadLocal.getCompanyId());
		param.setDataState(CrmConstant.Customer.DataState.PUBLIC_POOL);
		vo.setParam(param);
		return customerService.getCustomerList(vo);
	}

	@Override
	public List<UncontactedVO> getUncontactedTeamUsersNum(Long teamId) {
		CompanyCounselorBO loginUser = LoginUtil.getLoginUser();
		JSONObject o = new JSONObject();
		o.put("teamId", loginUser.getTeamId());
		o.put("companyId", loginUser.getCompanyId()==0?null:loginUser.getCompanyId());
		o.put("cc", 1);
		return customerService.getUncontactedTeamUsersNum(o);
	}

	@Override
	public void addRemark(RemarkPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setBusinessId(loginThreadLocal.getId());
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		remarkService.addRemark(po);
	}

	@Override
	public void delRemark(Long id) {
		remarkService.delRemark(id);
	}

	@Override
	public void addCallLog(CallLogPO po) {
		callLogService.addCallLog(po);
	}

	@Override
	public void distributionCust(CustomerPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		CirculationLogPO log = new CirculationLogPO();
		log.setBusinessId(po.getBusinessId());
		log.setCustomerId(po.getId());
		log.setType(CrmConstant.Customer.CirculationState.WAIT_POOL);
		log.setCreateBy(loginThreadLocal.getName());
		log.setCreateDate(new Date());
		circulationLogService.addCirculationLog(log);
		po.setDataState(CrmConstant.Customer.DataState.ALLOCATED);
		customerService.updateCustomer(po);
	}

	@Override
	public void distributionAgainCust(CustomerPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		CirculationLogPO log = new CirculationLogPO();
		log.setBusinessId(po.getBusinessId());
		log.setCustomerId(po.getId());
		log.setType(CrmConstant.Customer.CirculationState.PUBLIC_POOL);
		log.setCreateBy(loginThreadLocal.getName());
		log.setCreateDate(new Date());
		circulationLogService.addCirculationLog(log);
		po.setDataState(CrmConstant.Customer.DataState.AGAIN);
		customerService.updateCustomer(po);
	}

	@Override
	public void joinMyCustomer(CustomerPO po) {
		po.setDataState(CrmConstant.Customer.DataState.ALLOCATED);
		customerService.updateCustomer(po);
	}
	
	
	@Override
	public List<JSONObject> selMediaReport(JSONObject params) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		if(StringUtils.isEmpty(loginThreadLocal.getMedia())) {
			params.put("mediaId", loginThreadLocal.getMedia());
		}else {
			params.put("companyId", loginThreadLocal.getCompanyId());
		}
		if(params.getDate("startDate")==null ||params.getDate("endDate")==null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String format = dateFormat.format(new Date());
			String start = format + " 00:00:00";
			String end = format + " 23:59:59";
			try {
				params.put("startDate", dateFormat2.parse(start).getTime());
				params.put("endDate", dateFormat2.parse(end).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		params.put("companyId", loginThreadLocal.getCompanyId());
		List<JSONObject> selMediaReport = customerService.selMediaReport(params);
		List<JSONObject> result = new ArrayList<JSONObject>();
		if(selMediaReport==null||selMediaReport.size()==0) {
			return result;
		}
		Map<Long, List<JSONObject>> collect = selMediaReport.stream().collect(Collectors.groupingBy(o ->o.getLong("sourceId")));
		Iterator<Long> iterator = collect.keySet().iterator();
		while(iterator.hasNext()) {
			Long next = iterator.next();
			List<JSONObject> list = collect.get(next);
			JSONObject obj = new JSONObject();
			obj.put("mediaId", list.get(0).getLong("mediaId"));
			obj.put("sourceId", list.get(0).getLong("sourceId"));
			obj.put("name", list.get(0).getString("name"));
			obj.put("forumName", list.get(0).getString("forumName"));
			obj.put("sourceName", list.get(0).getString("sourceName"));
			obj.put("city", list.get(0).getString("city"));
			obj.put("0", 0);
			obj.put("1", 0);
			obj.put("2", 0);
			obj.put("3", 0);
			obj.put("4", 0);
			obj.put("5", 0);
			obj.put("total", 0);
			obj.put("orderNum", 0);
			obj.put("orderRate", 0);
			obj.put("qualityRate", 0);
			int total = 0;
			int quality = 0;
			int orderNum = 0;
			for(JSONObject o :list) {
				obj.put(o.getString("level"), o.getIntValue("num"));
				total = total + o.getIntValue("num");
				orderNum = orderNum + o.getIntValue("orderNum");
				if(o.getIntValue("level")>=4) {
					quality = quality+ o.getIntValue("num");
				}
			}
			obj.put("total", total);
			obj.put("orderNum",orderNum);
			obj.put("orderRate", BigDecimal.valueOf(orderNum).divide(BigDecimal.valueOf(total), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			obj.put("qualityRate",  BigDecimal.valueOf(quality).divide(BigDecimal.valueOf(total), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			result.add(obj);
		}
		return result;
	}
	
	public static void main1(String[] args) {
		System.out.println(BigDecimal.valueOf(1).divide(BigDecimal.valueOf(3), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
	}

	@Override
	public List<JSONObject> selCustReport(JSONObject params) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		int level = params.getIntValue("level");
		if(params.getInteger("companyId")==null) {
			params.put("companyId", loginThreadLocal.getCompanyId());
		}
		if(params.getDate("startDate")==null ||params.getDate("endDate")==null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String format = dateFormat.format(new Date());
			String start = format + " 00:00:00";
			String end = format + " 23:59:59";
			try {
				params.put("startDate", dateFormat2.parse(start).getTime());
				params.put("endDate", dateFormat2.parse(end).getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<JSONObject> selMediaReport = customerService.selCustReport(params);
		List<JSONObject> result = new ArrayList<JSONObject>();
		if(selMediaReport==null||selMediaReport.size()==0) {
			return result;
		}
		Map<String, List<JSONObject>> collect = new HashMap<String, List<JSONObject>>();
		if(level==0) {
			collect = selMediaReport.stream().collect(Collectors.groupingBy(o ->o.getString("companyName")));
		}else if(level==1) {
			collect = selMediaReport.stream().collect(Collectors.groupingBy(o ->o.getString("shopName")));
		}else if(level==2) {
			collect = selMediaReport.stream().collect(Collectors.groupingBy(o ->o.getString("teamName")));
		}else if(level==3) {
			collect = selMediaReport.stream().collect(Collectors.groupingBy(o ->o.getString("name")));
		}
		Iterator<String> iterator = collect.keySet().iterator();
		while(iterator.hasNext()) {
			String next = iterator.next();
			List<JSONObject> list = collect.get(next);
			JSONObject obj = new JSONObject();
			obj.put("companyName", list.get(0).getString("companyName"));
			obj.put("shopName", list.get(0).getString("shopName"));
			obj.put("teamName", list.get(0).getString("teamName"));
			obj.put("name", list.get(0).getString("name"));
			obj.put("0", 0);
			obj.put("1", 0);
			obj.put("2", 0);
			obj.put("3", 0);
			obj.put("4", 0);
			obj.put("5", 0);
			obj.put("total", 0);
			obj.put("orderNum", 0);
			obj.put("callRate",0);
			obj.put("orderRate", 0);
			obj.put("qualityRate", 0);
			int total = 0;
			int quality = 0;
			int orderNum = 0;
			int callNum = 0 ;
			for(JSONObject o :list) {
				int num =obj.getIntValue(o.getString("level"))+ o.getIntValue("num");
				obj.put(o.getString("level"),num);
				total = total + o.getIntValue("num");
				orderNum = orderNum + o.getIntValue("orderNum");
				callNum = callNum + o.getIntValue("callNum");
				if(o.getIntValue("level")>=4) {
					quality = quality+ o.getIntValue("num");
				}
			}
			obj.put("total", total);
			obj.put("orderNum",orderNum);
			obj.put("callRate",BigDecimal.valueOf(callNum).divide(BigDecimal.valueOf(total), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			obj.put("orderRate", BigDecimal.valueOf(orderNum).divide(BigDecimal.valueOf(total), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			obj.put("qualityRate",  BigDecimal.valueOf(quality).divide(BigDecimal.valueOf(total), 3,RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue());
			result.add(obj);
		}
		return result;
	}

	@Override
	public void addMeidaCustomer(MediaCustomerBO bo) {
		PageVO<MediaPO> selparams= new PageVO<MediaPO>();
		MediaPO po = new MediaPO();
		po.setAppid(bo.getAppid());
		selparams.setParam(po);
		PageVO<MediaPO> cpMediaList = mediaService.getCPMediaList(selparams);
		List<MediaPO> data = cpMediaList.getData();
		if(data==null || data.size()==0) {
			throw new CrmException("000001", "不存在媒体渠道");
		}
		MediaPO mediaPO = data.get(0);
		if(mediaPO.getState()==1) {
			throw new CrmException("000002", "媒体已暂停");
		}
		JSONObject parseObject = JSON.parseObject(JSON.toJSONString(bo));
		parseObject.remove("sign");
		parseObject.put("secret", mediaPO.getSecret());
		Set<String> keySet = parseObject.keySet();
		List<String> sortList = new ArrayList<String>(keySet);
		sortList.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		Iterator<String> iterator = sortList.iterator();
		StringBuilder builder = null;
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (builder == null) {
				builder = new StringBuilder();
			}else {
				builder.append("&");
			}
			builder.append(next);
			builder.append("=");
			builder.append(parseObject.get(next));
		}
		String result = "";
		if(builder!=null) {
			result = builder.toString();
		}
		if(!bo.getSign().equals(sha1(result))) {
			throw new CrmException("000002", "非法请求");
		}
		String params = bo.getParams();
		if(bo.getEncryption()==1) {
			try {
				params = decrypt(params,mediaPO.getPrivateKey());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CustomerPO parseObject2 = JSON.parseObject(params,CustomerPO.class);
		PageVO<SourcePO> selsource= new PageVO<SourcePO>();
		SourcePO source = new SourcePO();
		source.setId(parseObject2.getSourceId());
		selsource.setParam(source);
		PageVO<SourcePO> sourceList = sourceService.getSourceList(selsource);
		List<SourcePO> data2 = sourceList.getData();
		if(data2 == null || data2.size()==0) {
			throw new CrmException("000002", "来源不存在");
		}
		SourcePO sourcePO = data2.get(0);
		if(sourcePO.getState()==1) {
			throw new CrmException("000002", "来源已暂停");
		}
		if(!sourcePO.getCity().contains(parseObject2.getCity())) {
			throw new CrmException("000002", "来源城市不符合");
		}
		PageVO<CustomerPO> selcust= new PageVO<CustomerPO>();
		CustomerPO cust = new CustomerPO();
		cust.setSourceId(parseObject2.getSourceId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String format = sdf.format(now);
		cust.setStartDate(format);
		cust.setEndDate(format);
		selcust.setParam(cust);
		PageVO<CustomerPO> customerList = customerService.getCustomerList(selcust);
		List<CustomerPO> custList = customerList.getData();
		int count = 0;
		if(custList!=null) {
			count = custList.size();
		}
		if(count>=sourcePO.getNum()) {
			throw new CrmException("000002", "渠道城市已到限量");
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
		String duanDate = sourcePO.getDuanDate();
		String[] split = duanDate.split("-");
		String format2 = sdf2.format(now);
		if(Integer.valueOf(format2)<Integer.valueOf(split[0])||Integer.valueOf(format2)>Integer.valueOf(split[1])) {
			throw new CrmException("000002", "超出进量时间端");
		}
		parseObject2.setCompanyId(mediaPO.getCompanyId());
		addMediaCustomer(parseObject2);
	}
	
	public void addMediaCustomer(CustomerPO po) {
		if(po.getMobile()==null) {
			throw new CrmException("000001", "手机号不能为空");
		}
		if(!checkMoblie(po.getMobile())) {
			throw new CrmException("000001", "手机号格式错误");
		}
		if(po.getCity()==null) {
			throw new CrmException("000001", "城市不能为空");
		}
		if(po.getGender()==null) {
			throw new CrmException("000001", "性别不能为空");
		}
		if(po.getAge()==null) {
			throw new CrmException("000001", "年龄不能为空");
		}
		if(po.getAge()<=21 ||po.getAge()>=60) {
			throw new CrmException("000001", "年龄不符合");
		}
		if(po.getQuota()==null) {
			throw new CrmException("000001", "贷款额度不能为空");
		}
		PageVO<CustomerPO> params = new PageVO<CustomerPO>();
		CustomerPO cust = new CustomerPO();
		cust.setSearchWord(po.getMobile());
		cust.setCompanyId(po.getCompanyId());
		params.setParam(cust);
		PageVO<CustomerPO> customerList = customerService.getCustomerList(params);
		if(customerList.getData()!=null&& customerList.getData().size()>0) {
			throw new CrmException("000001", "手机号已存在");
		}
		po.setMobileMd5(DigestUtil.digestMd5LowerCase32(po.getMobile()));
		po.setMobileCity("");
		customerService.addCustomer(po);
		PageVO<CustomerPO> customerList2 = customerService.getCustomerList(params);
		po = customerList2.getData().get(0);
		disCust(po);
	}
	
	private String sha1(String param) {
		try {
			MessageDigest instance = MessageDigest.getInstance("sha1");
			byte[] digest = instance.digest(param.getBytes());
			return Hex.encodeHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String str, String privateKey) throws Exception{
		//64位解码加密后的字符串
		byte[] inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
		//base64编码的私钥
		byte[] decoded = Base64.decodeBase64(privateKey);  
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));  
		//RSA解密
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, priKey);
		String outStr = new String(cipher.doFinal(inputByte));
		return outStr;
	}
	
	private void disCust(CustomerPO cust) {
		Long companyId = cust.getCompanyId();
		List<CompanyPO> allCompany = companyService.getAllCompany(companyId);
		for(CompanyPO po:allCompany) {
			if(po.getCity().contains(cust.getCity())) {
				Set<String> keys = AppContextUtil.getBean(StringRedisTemplate.class).keys(LoginUtil.LOGIN_PREFIX+po.getId().toString()+"_"+"*");
				cust.setCompanyId(po.getId());
				if(keys==null || keys.size()==0) {
					cust.setDataState(CrmConstant.Customer.DataState.WAIT_POOL);
				}else {
					List<String> multiGet = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().multiGet(keys);
					List<CompanyCounselorBO> listCounselor = new ArrayList<CompanyCounselorBO>();
					for(String s:multiGet) {
						CompanyCounselorBO counselorBO = JSONUtil.toJavaBean(s, CompanyCounselorBO.class);
						listCounselor.add(counselorBO);
					}
					List<CompanyCounselorBO> collect = listCounselor.stream().filter(bo -> bo.getStatus()==1 && bo.getOpen()==1).filter(bo->{
						boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, bo.getMobile());
						int num1 = 0;
						if(hasKey) {
							num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, bo.getMobile());
						}
						return num1<bo.getAllocationCount();
					}).sorted((b1,b2)->{
						boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, b1.getMobile());
						boolean hasKey2 = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, b2.getMobile());
						int num1 = 0;
						int num2 = 0;
						if(hasKey) {
							num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, b1.getMobile());
						}
						if(hasKey2) {
							num2 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, b2.getMobile());
						}
						return num1-num2;
					}).collect(Collectors.toList());
					if(collect.size()==0) {
						cust.setDataState(CrmConstant.Customer.DataState.WAIT_POOL);
					}else {
						CompanyCounselorBO companyCounselorBO = collect.get(0);
						cust.setBusinessId(companyCounselorBO.getId());
						cust.setDataState(CrmConstant.Customer.DataState.ALLOCATED);
						CallLogPO callLog = new CallLogPO();
						callLog.setBusinessId(companyCounselorBO.getId());
						callLog.setCustomerId(cust.getId());
						callLog.setType(0);
						callLog.setCallTime(new Date());
						callLog.setCreateBy("system");
						callLog.setCreateDate(new Date());
						callLogService.addCallLog(callLog);
						CirculationLogPO log = new CirculationLogPO();
						log.setBusinessId(companyCounselorBO.getId());
						log.setCustomerId(cust.getId());
						log.setType(CrmConstant.Customer.CirculationState.AUTOMATIC);
						log.setCreateBy("system");
						log.setCreateDate(new Date());
						circulationLogService.addCirculationLog(log);
						boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, companyCounselorBO.getMobile());
						int num1 = 0;
						if(hasKey) {
							num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, companyCounselorBO.getMobile());
						}
						num1 =num1+1;
						AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().put(DIS_NUM, companyCounselorBO.getMobile(), num1);
						new AliyunSms().sendMessage(companyCounselorBO.getMobile(), companyCounselorBO.getName());
						ServerEventListener.sendNewCustMsg(getUserLogin(keys,companyCounselorBO.getMobile()));
					}
				}
				LOG.info("用户：{}-{},分配给：{}-{}",cust.getName(),cust.getMobile(),cust.getCompanyId(),cust.getBusinessId());
				customerService.updateCustomer(cust);
			}
		}
	}
	
	private String getUserLogin(Set<String> keys,String mobile) {
		for(String s:keys) {
			if(s.contains(mobile)) {
				return s;
			}
		}
		return "";
	}
	
	private static final String DIS_NUM = "DIS_NUM";
	
//	@Scheduled(fixedRate = 2*60*1000)
	public void distribution() {
		LOG.info("自动分配开始");
		PageVO<CustomerPO> vo =new PageVO<CustomerPO>();
		CustomerPO param = new CustomerPO();
		param.setBusinessId(0l);
		param.setDataState(CrmConstant.Customer.DataState.UNALLOCATED);
		vo.setParam(param);
		vo.setSize(100000);
		vo.setIndex(1);
		PageVO<CustomerPO> customerList = customerService.getCustomerList(vo);
		LOG.info("自动分配数量："+customerList.getSize());
		List<CustomerPO> data = customerList.getData();
		for(CustomerPO cust:data) {
			Long companyId = cust.getCompanyId();
			List<CompanyPO> allCompany = companyService.getAllCompany(companyId);
			for(CompanyPO po:allCompany) {
				if(po.getCity().contains(cust.getCity())) {
					Set<String> keys = AppContextUtil.getBean(StringRedisTemplate.class).keys(LoginUtil.LOGIN_PREFIX+po.getId().toString()+"_"+"*");
					cust.setCompanyId(po.getId());
					if(keys==null || keys.size()==0) {
						cust.setDataState(CrmConstant.Customer.DataState.WAIT_POOL);
					}else {
						List<String> multiGet = AppContextUtil.getBean(StringRedisTemplate.class).opsForValue().multiGet(keys);
						List<CompanyCounselorBO> listCounselor = new ArrayList<CompanyCounselorBO>();
						for(String s:multiGet) {
							CompanyCounselorBO counselorBO = JSONUtil.toJavaBean(s, CompanyCounselorBO.class);
							listCounselor.add(counselorBO);
						}
						List<CompanyCounselorBO> collect = listCounselor.stream().filter(bo -> bo.getStatus()==1 && bo.getOpen()==1).filter(bo->{
							boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, bo.getMobile());
							int num1 = 0;
							if(hasKey) {
								num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, bo.getMobile());
							}
							return num1<bo.getAllocationCount();
						}).sorted((b1,b2)->{
							boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, b1.getMobile());
							boolean hasKey2 = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, b2.getMobile());
							int num1 = 0;
							int num2 = 0;
							if(hasKey) {
								num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, b1.getMobile());
							}
							if(hasKey2) {
								num2 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, b2.getMobile());
							}
							return num1-num2;
						}).collect(Collectors.toList());
						if(collect.size()==0) {
							cust.setDataState(CrmConstant.Customer.DataState.WAIT_POOL);
						}else {
							CompanyCounselorBO companyCounselorBO = collect.get(0);
							cust.setBusinessId(companyCounselorBO.getId());
							cust.setDataState(CrmConstant.Customer.DataState.ALLOCATED);
							CallLogPO callLog = new CallLogPO();
							callLog.setBusinessId(companyCounselorBO.getId());
							callLog.setCustomerId(cust.getId());
							callLog.setType(0);
							callLog.setCallTime(new Date());
							callLog.setCreateBy("system");
							callLog.setCreateDate(new Date());
							callLogService.addCallLog(callLog);
							CirculationLogPO log = new CirculationLogPO();
							log.setBusinessId(companyCounselorBO.getId());
							log.setCustomerId(cust.getId());
							log.setType(CrmConstant.Customer.CirculationState.AUTOMATIC);
							log.setCreateBy("system");
							log.setCreateDate(new Date());
							circulationLogService.addCirculationLog(log);
							boolean hasKey = AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().hasKey(DIS_NUM, companyCounselorBO.getMobile());
							int num1 = 0;
							if(hasKey) {
								num1 = (int) AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().get(DIS_NUM, companyCounselorBO.getMobile());
							}
							num1 =num1+1;
							AppContextUtil.getBean(StringRedisTemplate.class).opsForHash().put(DIS_NUM, companyCounselorBO.getMobile(), num1);
							new AliyunSms().sendMessage(companyCounselorBO.getMobile(), companyCounselorBO.getName());
							AppContextUtil.getBean(StringRedisTemplate.class).opsForList().leftPush("new_cust_redis_prefix_"+companyCounselorBO.getMobile(), "1");
						}
					}
					LOG.info("用户：{}-{},分配给：{}-{}",cust.getName(),cust.getMobile(),cust.getCompanyId(),cust.getBusinessId());
					customerService.updateCustomer(cust);
				}
			}
		}
		LOG.info("自动分配 OK");
	}
	
	@Scheduled(cron ="0 0 0 * * ?")
	public void delDisNum() {
		AppContextUtil.getBean(StringRedisTemplate.class).delete(DIS_NUM);
	}

	@Override
	public double getMatrix(JSONObject o) {
		return customerService.getUserWeightScore(o);
	}

	@Override
	public void replaceMatrix() {
		customerService.replaceMatrix();
	}

	@Override
	public boolean checkMobile(MediaCustomerBO bo) {
		PageVO<MediaPO> selparams= new PageVO<MediaPO>();
		MediaPO po = new MediaPO();
		po.setAppid(bo.getAppid());
		selparams.setParam(po);
		PageVO<MediaPO> cpMediaList = mediaService.getCPMediaList(selparams);
		List<MediaPO> data = cpMediaList.getData();
		if(data==null || data.size()==0) {
			throw new CrmException("000003", "不存在媒体渠道");
		}
		MediaPO mediaPO = data.get(0);
		if(mediaPO.getState()==1) {
			throw new CrmException("000003", "媒体已暂停");
		}
		JSONObject parseObject = JSON.parseObject(JSON.toJSONString(bo));
		parseObject.remove("sign");
		parseObject.put("secret", mediaPO.getSecret());
		Set<String> keySet = parseObject.keySet();
		List<String> sortList = new ArrayList<String>(keySet);
		sortList.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		Iterator<String> iterator = sortList.iterator();
		StringBuilder builder = null;
		while (iterator.hasNext()) {
			String next = iterator.next();
			if (builder == null) {
				builder = new StringBuilder();
			}else {
				builder.append("&");
			}
			builder.append(next);
			builder.append("=");
			builder.append(parseObject.get(next));
		}
		String result = "";
		if(builder!=null) {
			result = builder.toString();
		}
		if(!bo.getSign().equals(sha1(result))) {
			throw new CrmException("000003", "非法请求");
		}
		String params = bo.getParams();
		if(bo.getEncryption()==1) {
			try {
				params = decrypt(params,mediaPO.getPrivateKey());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		PageVO<CustomerPO> selCust= new PageVO<CustomerPO>();
		CustomerPO parseObject2 = JSON.parseObject(params,CustomerPO.class);
		String mobileMd5 = parseObject2.getMobileMd5();
		if(StringUtils.isEmpty(mobileMd5)) {
			throw new CrmException("000003", "参数不能为空");
		}
		CustomerPO custSelParams = new CustomerPO();
		custSelParams.setMobileMd5(mobileMd5);
		custSelParams.setCompanyId(mediaPO.getCompanyId());
		selCust.setParam(custSelParams);
		List<CustomerPO> customerList = customerService.getCustomer(selCust);
		if(customerList == null || customerList.size()==0) {
			return true;
		}
		return false;
	}
	
}
