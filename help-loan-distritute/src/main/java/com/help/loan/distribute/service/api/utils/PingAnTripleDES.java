package com.help.loan.distribute.service.api.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;

public class PingAnTripleDES {

    /** 24位密钥 */
    private static final String key = "H$wanmPS^73w#nLA2vZJ8qDZ";
    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * AES解密(一账通登录返回信息)
     *
     * @param data
     * @return
     */
    public static String decryptToXml(String data) throws Exception {
        String decryptData = decrypt(data);
        return URLDecoder.decode(new String(base64Decode(decryptData)),DEFAULT_CHARSET);
    }

    /**
     * AES解密
     *
     * @param data
     * @return
     */
    public static String decrypt(String data) {
        try {
            String newKey = convertTo16Bit(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(newKey.getBytes(DEFAULT_CHARSET), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            IvParameterSpec iv = new IvParameterSpec(newKey.getBytes(DEFAULT_CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);// 初始化
            byte[] result = cipher.doFinal(parseHexStr2Byte(data));
            return new String(result, DEFAULT_CHARSET); // 加密
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密
     *
     * @param data
     * @return
     */
    public static String encrypt(String data) {
        try {
            String newKey = convertTo16Bit(key);
            SecretKeySpec secretKeySpec = new SecretKeySpec(newKey.getBytes(DEFAULT_CHARSET), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器
            IvParameterSpec iv = new IvParameterSpec(newKey.getBytes(DEFAULT_CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);// 初始化
            byte[] result = cipher.doFinal(data.getBytes(DEFAULT_CHARSET));
            return parseByte2HexStr(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toLowerCase());
        }
        return sb.toString();
    }

    private static byte[] base64Decode(String base64) throws Exception {
        return Base64.decodeBase64(base64.getBytes());
    }

    /**
     * 密钥变换为16位
     *
     * @param key
     * @return
     */
    private static String convertTo16Bit(String key) {
        while (key.length() < 16) {
            key = "0" + key;
        }
        return key.substring(0, 16);
    }

    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
}
