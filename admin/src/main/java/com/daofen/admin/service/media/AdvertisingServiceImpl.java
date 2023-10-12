package com.daofen.admin.service.media;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.daofen.admin.controller.login.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.daofen.admin.service.media.dao.AdvertisingDao;
import com.daofen.admin.service.media.model.AdvertisingPO;
import com.daofen.admin.utils.excel.XLSXCovertCSVReader;

@Component
public class AdvertisingServiceImpl implements AdvertisingService{

	private static final Logger log = LoggerFactory.getLogger(AdvertisingServiceImpl.class);
	
	@Autowired
	AdvertisingDao advertisingDao;
	
	@Override
	public void addAdvertising(MultipartFile file) {
		try {
			InputStream inputStream = file.getInputStream();
			List<String[]> readerExcel = XLSXCovertCSVReader.readerExcel(inputStream, "Sheet1", 14);
			List<AdvertisingPO> list = new ArrayList<AdvertisingPO>();
			readerExcel.remove(0);
			for(String[] array:readerExcel) {
				if(array[0]==null || array[0].contains("null")) {
					break;
				}else if("合计".equals(array[0])||"总计".equals(array[0])){
					continue;
				}else {
					AdvertisingPO po = new AdvertisingPO();
					po.setAccount(array[2]);
					po.setAdvertisingDate(array[0]);
					po.setChannel(array[1]);
					po.setCity(array[5]);
					po.setConsume(isNum(array[8])?Double.valueOf(array[8].replace(",", "")):0);
					po.setConversion(isNum(array[9])?Integer.valueOf(array[9].replace(",", "")):0);
					po.setMedia(array[4]);
					po.setOperators(array[3]);
					po.setPrice(isNum(array[10])?Double.valueOf(array[10].replace(",", "")):0);
					po.setType(1);
					Date d = new Date();
					po.setCreateBy("cps");
					po.setCreateDate(d);
					po.setUpdateBy("cps");
					po.setUpdateDate(d);
					list.add(po);
					if(list.size()>=500) {
						advertisingDao.add(list);
						list.clear();
					}
				}	
			}
			advertisingDao.add(list);
		} catch (Exception e) {
			log.error("解析运营日报表异常:{}",e.getMessage(),e);
			throw new RuntimeException();
		}
		
	}
	
	public boolean isNum(String aa) {
		if(aa==null) {
			return false;
		}
		return Pattern.matches("^[\\d+,]*\\d+\\.?\\d*$", aa);
	}
	
}
