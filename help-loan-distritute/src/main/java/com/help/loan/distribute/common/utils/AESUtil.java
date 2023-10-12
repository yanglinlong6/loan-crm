package com.help.loan.distribute.common.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESUtil {

    private static byte[] Keys = { 0x41, 0x72, 0x65, 0x79, 0x6F, 0x75, 0x6D, 0x79, 0x53,0x6E, 0x6F, 0x77, 0x6D, 0x61, 0x6E, 0x3F };

    private static String iv="dongfangshengshi";
    /**
     * @author wangcheng
     *  AES算法加密明文
     * @param data 明文
     * @param key 密钥，长度16
     * @return 密文
     */
    public static String encrypt(String data,String key) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            String iv = "0000000000000000";
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;

            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return encode(encrypted).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encrypt(String data,String key,String iv) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            byte[] dataBytes = data.getBytes();
            int plaintextLength = dataBytes.length;

            if (plaintextLength % blockSize != 0) {
                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
            }

            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return encode(encrypted).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @author wangcheng
     *  AES算法解密密文
     * @param data 密文
     * @param key 密钥，长度16
     * @return 明文
     */
    public static String decrypt(String data,String key) throws Exception {
        try
        {

            byte[] encrypted1 = decode(data);
            String iv = "0000000000000000";
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString.trim();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 编码
     * @param byteArray
     * @return
     */
    private static String encode(byte[] byteArray) {
        return new String(new org.apache.tomcat.util.codec.binary.Base64().encode(byteArray));
    }

    /**
     * 解码
     * @param base64EncodedString
     * @return
     */
    private static byte[] decode(String base64EncodedString) {
        return new org.apache.tomcat.util.codec.binary.Base64().decode(base64EncodedString);
    }



    public static String java_openssl_encrypt(String data, String sKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        StringBuilder sb = new StringBuilder("");
        char[] chars = "0123456789abcdef".toCharArray();
        byte[] bs = cipher.doFinal(data.getBytes());
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    //AES：加密方式   CBC：工作模式   PKCS5Padding：填充模式
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    public static String java_openssl_encrypt(String data, String sKey,String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
        SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(), AES);
        IvParameterSpec ivs = new IvParameterSpec(iv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,ivs);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        Base64 base = new Base64();
        return base.encodeToString(encrypted);//此处使用BASE64做转码。
    }
}
