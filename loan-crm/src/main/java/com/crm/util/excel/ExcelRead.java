/*
 * 文件名：AbstractReadFiles.java 版权：深圳融信信息咨询有限公司 修改人：zhangqiuping 修改时间：@create_date
 * 2017年11月3日 上午11:15:22 修改内容：新增
 */
package com.crm.util.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 读取Excel文件
 * @since      2.2.4
 */
public class ExcelRead
{
	
	public ExcelRead()
	{}
	
	/**
	 * 调测日志记录器。
	 */
	private static final Logger logger = LoggerFactory.getLogger(ExcelRead.class);
	
	/**
	 * @param path
	 * @param sheetName excel文件的sheet名称
	 * @param columnNum 如果读取的是excel文件，则表示该excel文件列数
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @return List<String[]> 数据格式如：List<["a","b","c"]>
	 */
	public static List<String[]> read(String path,String sheetName,int columnNum) throws IOException, ParserConfigurationException, SAXException, OpenXML4JException {
		logger.info("待读取文件路径：" + path + "，sheet名称：" + sheetName + ",列数：" + columnNum);
		if(StringUtils.isBlank(path))
		{
			return null;
		}
		File file = new File(path);
		if(!file.canRead())
		{
			return null;
		}
		if(file.isFile())
		{
			return readFile(file,sheetName,columnNum);
		}
		return readDirectory(file,sheetName,columnNum);
	}
	
	private static List<String[]> readFile(File file,String sheetName,int columnNum) throws IOException, ParserConfigurationException, SAXException, OpenXML4JException {
		List<String[]> data = XLSXCovertCSVReader.readerExcel(file,sheetName,columnNum);
		boolean isDel = file.delete();
		logger.info("文件：" + file.getPath() + ",删除结果：" + isDel);
		return data;
	}
	
	private static List<String[]> readDirectory(File directory,String sheetName,int columnNum) throws IOException, ParserConfigurationException, SAXException, OpenXML4JException {
		logger.info("读取目录：" + directory);
		File[] files = directory.listFiles();
		if(null == files || files.length <= 0)
			return null;
		List<String[]> all = new ArrayList<String[]>();
		for(File file:files)
		{
			if(file.isFile())
				all.addAll(readFile(file,sheetName,columnNum));
			else all.addAll(readDirectory(file,sheetName,columnNum));
		}
		return all;
	}
}
