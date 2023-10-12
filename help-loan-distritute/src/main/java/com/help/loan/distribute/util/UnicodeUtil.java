package com.help.loan.distribute.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnicodeUtil {


    public static String toCN(String unicodeStr) {
        try {
            Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
            Matcher matcher = pattern.matcher(unicodeStr);
            char ch;
            while (matcher.find()) {
                //group
                String group = matcher.group(2);
                //ch:'李四'
                ch = (char) Integer.parseInt(group, 16);
                //group1
                String group1 = matcher.group(1);
                unicodeStr = unicodeStr.replace(group1, ch + "");
            }
            return unicodeStr.replace("\\", "").trim();
        }catch(Exception e){
            return unicodeStr;
        }
    }


    public static String toUnicode(String CN) {

        try {
            StringBuffer out = new StringBuffer("");
            //直接获取字符串的unicode二进制
            byte[] bytes = CN.getBytes("unicode");
            //然后将其byte转换成对应的16进制表示即可
            for(int i = 0; i < bytes.length - 1; i += 2) {
                out.append("\\u");
                String str = Integer.toHexString(bytes[i + 1] & 0xff);
                for(int j = str.length(); j < 2; j++) {
                    out.append("0");
                }
                String str1 = Integer.toHexString(bytes[i] & 0xff);
                out.append(str1);
                out.append(str);
            }
            return out.toString();
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
