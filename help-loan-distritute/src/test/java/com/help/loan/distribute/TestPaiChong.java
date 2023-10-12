package com.help.loan.distribute;

import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.common.utils.UnicodeUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TestPaiChong {

    public static void main(String[] args) throws IOException {
        String str = "重庆、天津、南通、扬州、马鞍山、宣城、芜湖、滁州、六安、资阳、德阳、宜宾、内江、巴中、泸州、自贡、遂宁、南充、广元、达州、绵阳、广安、眉山、成都、乐山、合肥";
        String[] array = str.split("、");
        StringBuffer stringBuffer = new StringBuffer();
        for(String s : array){
            stringBuffer.append(s).append("市,");
        }
        System.out.println(stringBuffer.toString());

        File file = new File("D:\\排重包\\排重包-北京-20220513.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

        File outFile = new File("D:\\排重包\\排重包-北京-20220608.txt");
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
        OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "UTF-8");

        String line;
        Map<String,Integer> map = new HashMap<>();
        int index = 0;
        while(StringUtils.isNotBlank(line = reader.readLine())){
            if(StringUtils.isBlank(line) || line.length() != 32 )
                continue;
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
//        System.out.println(UnicodeUtil.toCN("\\u6e20\\u9053\\u53f7\\u4e0d\\u6b63\\u786e"));
    }
}
