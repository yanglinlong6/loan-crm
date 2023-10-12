package com.help.loan.distribute;

import com.help.loan.distribute.service.api.utils.JudgeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SearchWordTest {


    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\张先森\\Desktop\\搜索-信贷-素材\\公积金关键词-2.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        File outFile = new File("C:\\Users\\张先森\\Desktop\\搜索-信贷-素材\\公积金关键词-3.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "UTF-8");

        String line;
        Map<String,Integer> map = new HashMap<>();
        int index = 0;
        while(StringUtils.isNotBlank(line = reader.readLine())){
            if(StringUtils.isBlank(line)){
                System.out.println("跳过关键词:"+line);
                continue;
            }
            line = line.replaceAll("2010","")
                    .replaceAll("2011","")
                    .replaceAll("2012","")
                    .replaceAll("2013","")
                    .replaceAll("2014","")
                    .replaceAll("2015","")
                    .replaceAll("2016","")
                    .replaceAll("2017","")
                    .replaceAll("2018","")
                    .replaceAll("2019","")
                    .replaceAll("2020","")
                    .replaceAll("2021","")
                    .replaceAll("2022","");

            line = line.replaceAll("东莞","")
                    .replaceAll("东阳","")
                    .replaceAll("佛山","")
                    .replaceAll("保定","")
                    .replaceAll("凤阳县","")
                    .replaceAll("北京","")
                    .replaceAll("北京市","")
                    .replaceAll("北部湾","")
                    .replaceAll("博罗","")
                    .replaceAll("博山","")
                    .replaceAll("大兴区","")
                    .replaceAll("大兴区","")
                    .replaceAll("大庆","")
                    .replaceAll("大庆市","")
                    .replaceAll("大理州","")
                    .replaceAll("大理","")
                    .replaceAll("大连","")
                    .replaceAll("安庆","")
                    .replaceAll("安庆市","")
                    .replaceAll("安徽","")
                    .replaceAll("安溪","")
                    .replaceAll("安阳市","")
                    .replaceAll("安阳","")
                    .replaceAll("定州","")
                    .replaceAll("定州市","")
                    .replaceAll("定西","")
                    .replaceAll("定边","")
                    .replaceAll("定远县","")
                    .replaceAll("宝山区","")
                    .replaceAll("宝应","")
                    .replaceAll("宝鸡","")
                    .replaceAll("宝鸡市","")
                    .replaceAll("巢湖市","")
                    .replaceAll("巢湖","")
                    .replaceAll("巴中市","")
                    .replaceAll("巴中","")
                    .replaceAll("巴彦淖尔","")
                    .replaceAll("常州","")
                    .replaceAll("常德","")
                    .replaceAll("常德市","")
                    .replaceAll("常熟","")
                    .replaceAll("德阳","")
                    .replaceAll("德清","")
                    .replaceAll("德州市","")
                    .replaceAll("德州","")
                    .replaceAll("承德","")
                    .replaceAll("抚州市","")
                    .replaceAll("抚顺","")
                    .replaceAll("扶余","")
                    .replaceAll("方庄","")
                    .replaceAll("昌乐","")
                    .replaceAll("本溪","")
                    .replaceAll("毕节","")
                    .replaceAll("沧州","")
                    .replaceAll("滁州","")
                    .replaceAll("滨州","")
                    .replaceAll("潮州","")
                    .replaceAll("甘井子","")
                    .replaceAll("甘肃省","")
                    .replaceAll("白云区","")
                    .replaceAll("白银","")
                    .replaceAll("百色","")
                    .replaceAll("福山","")
                    .replaceAll("福州","")
                    .replaceAll("福建省","")
                    .replaceAll("苍南","")
                    .replaceAll("苍溪","")
                    .replaceAll("葛洲坝","")
                    .replaceAll("蚌埠","")
                    .replaceAll("邓州市","")
                    .replaceAll("达州市","")
                    .replaceAll("达州","")
                    .replaceAll("郴州","")
                    .replaceAll("鄂尔多斯","")
                    .replaceAll("阜阳","")
                    .replaceAll("沧州","")
                    .replaceAll("沧州","")
            ;
            if(map.containsKey(line)){
                continue;
            }
            map.put(line,1);
            System.out.println(index+"-->"+line);
            if(index == 0){
                writer.write(line);
            }else
                writer.write("\n"+line);
            index++;
        }
        reader.close();
        writer.close();
    }

}
