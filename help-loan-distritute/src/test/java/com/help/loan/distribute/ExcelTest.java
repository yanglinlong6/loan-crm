package com.help.loan.distribute;

import com.help.loan.distribute.common.ResultVO;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.excel.XLSXCovertCSVReader;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.mockito.internal.util.collections.ListUtil;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ExcelTest {

    public static void main(String[] args) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
//        FileInputStream inputStream = new FileInputStream(new File("D:\\排重包\\排重包-南通-20220306.txt"));
//        List<String[]> datas = XLSXCovertCSVReader.readerExcel(inputStream, "Sheet1", 2);
//        if(null == datas || datas.isEmpty()){
//            return;
//        }
        System.out.println(MD5Util.getMd5String("123456"));
    }

}
