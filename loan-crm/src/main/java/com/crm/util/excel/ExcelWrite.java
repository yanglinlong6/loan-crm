package com.crm.util.excel;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExcelWrite {


    public static boolean write(String sheetName,String targetPath, List<Map<String,String>> header, List<JSONObject> content) throws IOException {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(sheetName); //创建table工作薄
        Object[][] datas = {{"区域", "总销售额(万元)", "总利润(万元)简单的表格"}, {"江苏省",9045,2256},{"广东省",3000, 690}};
        HSSFRow row;
        HSSFCell cell;
        for(int i = 0; i < datas.length; i++) {
            row = sheet.createRow(i);
            for(int j = 0; j < datas[i].length; j++) {
                cell = row.createCell(j);//根据表格行创建单元格
                cell.setCellValue(String.valueOf(datas[i][j]));
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(targetPath);
        wb.write(fileOutputStream);
        return true;
    }

}
