package com.help.loan.distribute.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.user.dao.CustReportDao;

@Service
public class IReportService implements ReportService{
	
	@Autowired
	private CustReportDao custReportDao;

	@Override
	public List<JSONObject> getMediaChannelCustApt(JSONObject params) {
		// TODO Auto-generated method stub
		return custReportDao.getMediaChannelCustApt(params);
	}

	@Override
	public List<JSONObject> getOrgApt(JSONObject params) {
		// TODO Auto-generated method stub
		return custReportDao.getOrgApt(params);
	}

	@Override
	public void insertMediaCustAptReport(List<JSONObject> params) {
		// TODO Auto-generated method stub
		custReportDao.insertMediaCustAptReport(params);
	}

	@Override
	public void insertOrgCustAptReport(List<JSONObject> params) {
		// TODO Auto-generated method stub
		custReportDao.insertOrgCustAptReport(params);
	}

	@Override
	public List<JSONObject> selOrgDetailedReport() {
		// TODO Auto-generated method stub
		return custReportDao.selOrgDetailedReport();
	}

}
