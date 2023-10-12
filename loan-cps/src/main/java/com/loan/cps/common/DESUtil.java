package com.loan.cps.common;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Base64;

public class DESUtil {

    private static final Logger log = LoggerFactory.getLogger(DESUtil.class);

    /**
     * 偏移变量，固定占8位字节
     */
    private final static String IV_PARAMETER = "12345678";

    private static final String ALGORITHM = "DES";

    private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";

    private static final String CHARSET = "utf-8";

    /**
     * 解密
     * @param key 密钥
     * @param data 密文
     * @return  解密后的内容
     */
    public static String decrypt(String key, String data) {
        if(StringUtils.isBlank(key))
            throw new RuntimeException("缺少key");
        if(StringUtils.isBlank(data))
            throw new RuntimeException("加密对象空的");
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.DECRYPT_MODE, secretKey,iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data.getBytes(CHARSET))), CHARSET);
        } catch (Exception e) {
            log.error("key-{},data-{},DES解密异常:{}-{}",key,data,e.getMessage(),e);
            return null;
        }
    }


    /**
     * 加密
     * @param key 密钥
     * @param data 待加密的内容
     * @return 密文
     */
    public static String encrypt(String key, String data) {
        if (key== null || key.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null)
            return null;
        try {
            Key secretKey = generateKey(key);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,iv);
            byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
            //JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            //Android平台可以使用android.util.Base64
            return new String(Base64.getEncoder().encode(bytes));
        } catch (Exception e) {
            log.error("key-{},data-{},DES加密异常:{}-{}",key,data,e.getMessage(),e);
            return null;
        }
    }

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        return keyFactory.generateSecret(dks);
    }

}
