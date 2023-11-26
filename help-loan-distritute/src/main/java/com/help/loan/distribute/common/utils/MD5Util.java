package com.help.loan.distribute.common.utils;

import org.springframework.util.StringUtils;

import java.security.MessageDigest;

public class MD5Util {

    /**
     * 手机号码加密
     *
     * @param mobile 手机号码
     * @return
     * @author huangzhigang 2017-04-10 add
     */
    public static Long mobileEncryptToLong(String mobile) {

        if (!StringUtils.isEmpty(mobile)) {
            // （手机号码 * 2） + （（手机号码 /2017）*10000）
            Long mobile_l = Long.parseLong(mobile);
            Long suffix = (mobile_l % 2017) * 10000;
            Long prefix = mobile_l * 2;
            return prefix + suffix;
        }
        return 0L;
    }

    private final static char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static String bytes2hex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        int t;
        for (int i = 0; i < 16; i++) {// 16 == bytes.length;
            t = bytes[i];
            if (t < 0) t += 256;
            sb.append(hexDigits[(t >>> 4)]);
            sb.append(hexDigits[(t % 16)]);
        }
        return sb.toString();
    }

    public static String getMd5String(String strSrc) {
        try {
            // 确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 加密后的字符串
            return bytes2hex(md5.digest(strSrc.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMd5String(Integer intSrc) {
        try {
            // 确定计算方法
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // 加密后的字符串
            return bytes2hex(md5.digest(intSrc.toString().getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
