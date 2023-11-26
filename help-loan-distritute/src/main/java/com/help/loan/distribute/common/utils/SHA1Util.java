package com.help.loan.distribute.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * sha1加密算法工具类
 */
@Slf4j
public class SHA1Util {

    /**
     * sha1加密算法
     *
     * @param inStr
     * @return
     * @throws Exception
     */
    public static String shaEncode(String inStr) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] byteArray = inStr.getBytes(StandardCharsets.UTF_8);
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String shaEncode02(String inStr) {
        MessageDigest messageDigest = null; // 此处的sha代表sha1
        try {
            messageDigest = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 调用digest方法，进行加密操作
        byte[] cipherBytes = messageDigest.digest(inStr.getBytes());
        return Hex.encodeHexString(cipherBytes);
    }

    public static void main(String[] args) throws Exception {
        String str = "amigoxiexiexingxing";
        System.out.println("原始：" + str);
        System.out.println("SHA后：" + shaEncode(str));
    }
}
