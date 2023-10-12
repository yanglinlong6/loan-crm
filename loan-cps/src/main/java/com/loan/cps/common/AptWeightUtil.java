package com.loan.cps.common;

import com.loan.cps.entity.UserAptitudePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AptWeightUtil {

	private static final Logger LOG = LoggerFactory.getLogger(AptWeightUtil.class);

	private static final String APT_WEIGHT_URL = "http://10.0.1.163/crm/customer/matrix/get";


	public static double getAptWeight(UserAptitudePO po) {
		try {

			String str = po.toString();
			String postForObject = HttpUtil.postForObject(APT_WEIGHT_URL, str);
			System.out.println("客户:"+po.getName()+"-"+po.getMobile()+",获取计算分:"+postForObject);
			LOG.info("客户:{}-{},获取计算分:{}",po.getName(),po.getMobile(),postForObject);
			return Double.valueOf(postForObject);
		} catch (Exception e) {
			LOG.info("计算客户分值异常:{}",e.getMessage(),e);
			return 0;
		}
	}



}
