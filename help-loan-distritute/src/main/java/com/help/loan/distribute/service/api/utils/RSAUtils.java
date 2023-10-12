package com.help.loan.distribute.service.api.utils;

import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Java RSA 加密工具类
 * 参考： https://blog.csdn.net/qy20115549/article/details/83105736
 */
public class RSAUtils {
    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private final static int KEY_SIZE = 1024;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 随机生成密钥对
     *
     * @throws Exception
     */
    public static void genKeyPair() throws Exception {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = encryptBASE64(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = encryptBASE64(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        //0表示公钥
        keyMap.put(0, publicKeyString);
        //1表示私钥
        keyMap.put(1, privateKeyString);
    }

    //编码返回字符串
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }

    //解码返回byte
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        //base64编码的公钥
        byte[] decoded = decryptBASE64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        //RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] data = str.getBytes();
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        String outStr = encryptBASE64(encryptedData);

        return outStr;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = decryptBASE64(str);
        //base64编码的私钥
        byte[] decoded = decryptBASE64(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        //RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        int inputLen = inputByte.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(inputByte, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(inputByte, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        String outStr = new String(decryptedData);
        return outStr;
    }


    public static void main(String[] args) throws Exception {
        long temp = System.currentTimeMillis();
        //生成公钥和私钥
        genKeyPair();
        //加密字符串
//        System.out.println("公钥:" + keyMap.get(0));
//        System.out.println("私钥:" + keyMap.get(1));
//        System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");


        String message = "{\"reqBody\":[{\"age\":\"18\",\"annualIncome\":\"1\",\"businessLicense\":1,\"car\":1,\"corporateTax\":1,\"creditCard\":1,\"custName\":\"测试\",\"custRemark\":\"这仅仅只是一个测试\",\"dataFrom\":\"朋友圈测试\",\"education\":1,\"house\":1,\"insurancePolicy\":1,\"loanAmount\":100000.0,\"marryStatus\":1,\"microLoan\":1,\"onBehalfOf\":\"1\",\"phoneNum\":\"18612345678\",\"profession\":\"测试工\",\"providentFund\":1,\"sex\":1,\"socialSecurity\":1}]}";
        System.out.println("原文:" + message);

        temp = System.currentTimeMillis();
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIS7ibl2Wds7Go5IkC8hVztKnIXK0nXDLJmenK" +
                "Gv0BuRDR9B8k9irwW+YGydEnmjjI92b9JHDidwunETorKfYLWTRKU3QDqnTQiWWaqK1nK4+F55Uh" +
                "cHshxvZJNGT+b6k6LXxNHgpEJaUyBUhUTx69Em/WsWAisoYqRerUwp0ErQIDAQAB";
        System.out.println(pubKey);
        String messageEn = encrypt(message, pubKey);
        System.out.println("密文:" + messageEn);
        System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");

        temp = System.currentTimeMillis();

        String priKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIhLuJuXZZ2zsajkiQLyFXO0qchc" +
                "rSdcMsmZ6coa/QG5ENH0HyT2KvBb5gbJ0SeaOMj3Zv0kcOJ3C6cROisp9gtZNEpTdAOqdNCJZZqo" +
                "rWcrj4XnlSFweyHG9kk0ZP5vqTotfE0eCkQlpTIFSFRPHr0Sb9axYCKyhipF6tTCnQStAgMBAAEC" +
                "gYB3z/Cxvg4S6qLzcCrNTxfyhc92lZmVbyQqOsXc54qf2BrkJbC3Ijsu3epA72qQYK8oYuvC/iTJ" +
                "v5Kbw7YZuaJSoSF0uMNVoOmN0o/7TfJPH4JdZp1Vd1tdqqz/zr02Hgk2ljKyEk+oIUY282KL02KZ" +
                "Z8z5DLDXY0WQBdOZzcb3wQJBAL0qB0nRvyuubhMe750eFlUqa57zHugXtv4N4FmSFjCwhXw3FV1M" +
                "Ki2WIEGj/erUACnLEVl33w1omyWffCfGKj0CQQC4c7oROAT6NDIo3E+vZZzAr5B852+vxvHWvODD" +
                "DhMHH3gP7TYPUwfyMRjyZS8kTD7Hq6QurPbPbXeK0WAgTJsxAkAHi4Oa6CjN3zk4vgUkqSyO3RBJ" +
                "2Lyk2T1NSNRn/jGwY3oPiErr27va8Z/7vkTEdwxCnnzkqqlU6ZU2nPPgykCpAkEAo7cMCqZ3PIDB" +
                "mKTbEWoWNKxfiY1+Az2If7nLoTVHzEWxMimwlu9ymRPc+aC3s/b8rgr5wfgBZODbftoSAPkdsQJB" +
                "AI49zM6mf3spJ0eCc9aOsrMSQX5pF7f4AduAK6ioZr0yhGxBWII0GH2DlaerfYETcMeJBoukckk9" +
                "IOLb8FI0Z+0=";
//        messageEn = "V0JE7azjTQkXaQLuZcHAW+duFZdAjBUEdMNPt/9KN1hsqxO7l4Ueooao6XaiHPZvbAxNfnIXNkTv\n" +
//                "xWVx19YWNRwNKy753EZW4HZzf2y/2NuR4Prj204UpDOpM6NdadkRHC7B1vkzRoWeviN46YWo9B1O\n" +
//                "230mHC63SUeha3XEFk9aSuLpMwkYwk1JQlXKkOl9N44HBPueS5eqKuQ2XqcPXF5EuYMIk1ZpXAHI\n" +
//                "BFNZnitKJok7MrxL1Kw5/gAMb3mFl4Ji/e7dZkXY5YO3WoyfAhKBSZUdEM1S/axRQrik4T0MQOZF\n" +
//                "mO/P5YqoP5RyiSHyosiw7ubqag6UgbpYJuqImoEDh2uRbXkWrN8UOG28pN/VXQMvM01Sb5t/9XkJ\n" +
//                "aM+eaxaEHwG7w531CbiKtm/2+WrU+Zv9evnHiE48OxwCx4AKpzYd4ZSBmzYMyaYKBSaTLyt+igBN\n" +
//                "wtPjB6vmkWgXMhAO4rZNHQwigXX3nMrAzllf5pdHl8d3EspUgnvwcyYlW6u/qnOPjeQUEaefDrny\n" +
//                "sKa3wDKKUSRr1dO90ADuZ5ctQMzT7uUJOAsizU2B/5XMJFJoZ3SZ41g8LcGiDsF3Jad4o/Fv9iQB\n" +
//                "g0xN5VtanF4EG7GdYX47NyvxQzO4XA2CR7MEv3j2vEplFZAe55cRjUmUhFHxrWxO5by3FgpIPNs=";

        message = "IsoYzeiwZT4qd7AUTyV9dn98Iczr2HGjZ9hvfJE3hVOkcEnc5SmiL/KSpFY3lJbluf+TuYxYD/4pBYdgfpUTFN4hEPfW4QJx9kpYdj2eIHMGJe8eWBIb9+Jas/0VZEZ3JLXqqvJeiJ72v/BRZBDTVZv0eSbYh5PQNool/z4ggN4bafQrd6nvS+1KoBIDQDyMfo/yLVVl6EnaZxfe+YzsY8wO4N0LVEZux/eFucz2P5ks2hVI8Z48LjkR2tXICfq2mcXHIXfH6A4aMF88V53VqausHdBaAVx+yhpJQAEeSf/SrOLU+wgBa2/ds3Y057no0nTDqqxNHejD7YaN+wKRGg==";
        String messageDe = decrypt(messageEn, priKey);
        System.out.println("解密:" + messageDe);
        System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");;
    }
}