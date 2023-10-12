package com.help.loan.distribute.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.dao.WechatUserBindDao;

@Service
public class IConversionReportService implements ConversionReportService{
	
	@Autowired
	private UserAptitudeDao userAptitudeDao;
	
	@Autowired
	private WechatUserBindDao wechatUserBindDao;

	@Override
	public void getConversionReport(String startDate, String endDate,HttpServletResponse res) {
		JSONObject o = new JSONObject();
		o.put("startDate", startDate);
		o.put("endDate",endDate);
		JSONObject conversion = userAptitudeDao.selectCount(o);
		Integer sub = wechatUserBindDao.selectCount(o);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("table");
		Object[][] datas = {{"节点", "节点数量", "完成数量","流失数量", "节点流失率", "占总流失率"}, 
				{"欢迎语",sub,conversion.getInteger("total"),sub-conversion.getInteger("total"),
					BigDecimal.valueOf(sub-conversion.getInteger("total")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(sub-conversion.getInteger("total")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"协议+职业",conversion.getInteger("total"), conversion.getInteger("occ"),conversion.getInteger("total")-conversion.getInteger("occ"),
					BigDecimal.valueOf(conversion.getInteger("total")-conversion.getInteger("occ")).divide(BigDecimal.valueOf(conversion.getInteger("total")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("total")-conversion.getInteger("occ")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"公积金",conversion.getInteger("occ"), conversion.getInteger("fund"),conversion.getInteger("occ")-conversion.getInteger("fund"),
					BigDecimal.valueOf(conversion.getInteger("occ")-conversion.getInteger("fund")).divide(BigDecimal.valueOf(conversion.getInteger("occ")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("occ")-conversion.getInteger("fund")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"打卡工资",conversion.getInteger("fund"), conversion.getInteger("income"),conversion.getInteger("fund")-conversion.getInteger("income"),
					BigDecimal.valueOf(conversion.getInteger("fund")-conversion.getInteger("income")).divide(BigDecimal.valueOf(conversion.getInteger("fund")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("fund")-conversion.getInteger("income")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"房",conversion.getInteger("income"), conversion.getInteger("house"),conversion.getInteger("income")-conversion.getInteger("house"),
					BigDecimal.valueOf(conversion.getInteger("income")-conversion.getInteger("house")).divide(BigDecimal.valueOf(conversion.getInteger("income")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("income")-conversion.getInteger("house")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"车",conversion.getInteger("house"), conversion.getInteger("car"),conversion.getInteger("house")- conversion.getInteger("car"),
					BigDecimal.valueOf(conversion.getInteger("house")-conversion.getInteger("car")).divide(BigDecimal.valueOf(conversion.getInteger("house")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("house")-conversion.getInteger("car")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"营业执照",conversion.getInteger("car"), conversion.getInteger("company"),conversion.getInteger("car")-conversion.getInteger("company"),
					BigDecimal.valueOf(conversion.getInteger("car")-conversion.getInteger("company")).divide(BigDecimal.valueOf(conversion.getInteger("car")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("car")-conversion.getInteger("company")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"保单",conversion.getInteger("company"), conversion.getInteger("insurance"),conversion.getInteger("company")-conversion.getInteger("insurance"),
					BigDecimal.valueOf(conversion.getInteger("company")-conversion.getInteger("insurance")).divide(BigDecimal.valueOf(conversion.getInteger("company")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("company")-conversion.getInteger("insurance")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"手机号",conversion.getInteger("mobile1")+conversion.getInteger("mobile2"), conversion.getInteger("mobile2"),conversion.getInteger("mobile1"),
					BigDecimal.valueOf(conversion.getInteger("mobile1")).divide(BigDecimal.valueOf(conversion.getInteger("mobile1")+conversion.getInteger("mobile2")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
					BigDecimal.valueOf(conversion.getInteger("mobile1")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"},
				{"城市",conversion.getInteger("city1")+conversion.getInteger("city2"), conversion.getInteger("city2"),conversion.getInteger("city1"),
						BigDecimal.valueOf(conversion.getInteger("city1")).divide(BigDecimal.valueOf(conversion.getInteger("city1")+conversion.getInteger("city2")),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%",
						BigDecimal.valueOf(conversion.getInteger("city1")).divide(BigDecimal.valueOf(sub),3, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()+"%"}
		};
		HSSFRow row;
		HSSFCell cell;
		for(int i = 0; i < datas.length; i++) {
			row = sheet.createRow(i);
			for(int j = 0; j < datas[i].length; j++) {
				cell = row.createCell(j);//根据表格行创建单元格
				cell.setCellValue(String.valueOf(datas[i][j]));
			}
		}
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/vnd.ms-excel;charset=utf-8");// 设置contentType为excel格式
		res.setHeader("Content-Disposition", "Attachment;Filename="+ "AI_Report_"+startDate+"-"+endDate+".xls");
		try {
			ServletOutputStream outputStream = res.getOutputStream();
			wb.write(outputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
