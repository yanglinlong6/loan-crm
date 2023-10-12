package com.help.loan.distribute;

import com.help.loan.distribute.common.utils.MD5Util;
import org.apache.axis.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestFangXiang {

    public static void main(String[] args) throws IOException {
//        String str = "重庆、天津、南通、扬州、马鞍山、宣城、芜湖、滁州、六安、资阳、德阳、宜宾、内江、巴中、泸州、自贡、遂宁、南充、广元、达州、绵阳、广安、眉山、成都、乐山、合肥";
//        String[] array = str.split("、");
//        StringBuffer stringBuffer = new StringBuffer();
//        for(String s : array){
//            stringBuffer.append(s).append("市,");
//        }
//        System.out.println(stringBuffer.toString());

//        File file = new File("D:\\排重包\\排重包-南通-20220306.txt");
//        byte[] bytes = new byte[1024*1024*100];
//        FileInputStream fileInputStream = new FileInputStream(file);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
//
//        File outFile = new File("D:\\排重包\\排重包-南通-20220306.txt");
//        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
//        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "UTF-8");
//
//        String line;
//        List<String> list = new ArrayList<>();
//        list.clear();
//        int index = 0;
//        while(StringUtils.isNotBlank(line = reader.readLine())){
//            index++;
//            if(line.length() < 5 || list.contains(line))
//                continue;
//            if(list.contains(line)){
//                System.out.println(index+"-->"+line+"[exist]");
//                continue;
//            }
//            list.add(line);
//            System.out.println(index+"-->"+line);
//            writer.write(line+"\n");
//        }
//        reader.close();
//        writer.close();
        System.out.println(MD5Util.getMd5String("15898823777"));
    }

}
