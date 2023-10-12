package com.daofen.crm.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 非对称加密RSA算法 工具类
 */
public class RsaUtil {
    /**
     * RSA最大加密明文大小
     * 1024位的证书，加密时最大支持117个字节，解密时为128
     * 2048位的证书，加密时最大支持245个字节，解密时为256
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     * 1024位的证书，加密时最大支持117个字节，解密时为128
     * 2048位的证书，加密时最大支持245个字节，解密时为256
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 获取公钥
     */
    private static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = com.daofen.crm.utils.Base64Util.decode(key);

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 获取私钥
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = Base64Util.decode(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * RSA的签名算法
     */
    public static enum SignatureAlgorithm {
        MD5withRSA("MD5withRSA"),
        SHA1withRSA("SHA1withRSA"),
        ;

        private String name;

        public String getName() {
            return name;
        }

        SignatureAlgorithm(String name) {
            this.name = name;
        }
    }

    /**
     * 使用私钥签名
     * @param data 明文数据
     * @param privateKey 私钥
     * @param signatureAlgorithm 签名算法
     */
    private static String signByPrivate(byte[] data, String privateKey, SignatureAlgorithm signatureAlgorithm) throws Exception {
        PrivateKey key = getPrivateKey(privateKey);
        return signByPrivate(data, key, signatureAlgorithm);
    }

    private static String signByPrivate(byte[] data, PrivateKey privateKey, SignatureAlgorithm signatureAlgorithm) throws Exception {
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(signatureAlgorithm.getName());
        signature.initSign(privateKey);
        signature.update(data);
        byte[] sign = signature.sign();
        return Base64Util.encode(sign);
    }

    /**
     * 使用公钥验证签名
     * @param data 密文数据
     * @param publicKey 公钥
     * @param signatureAlgorithm 签名算法
     * @param sign 待验证签名字符串
     */
    private static boolean verifyByPublicKey(byte[] data, String publicKey, SignatureAlgorithm signatureAlgorithm, String sign) throws Exception {
        PublicKey key = getPublicKey(publicKey);
        return verifyByPublicKey(data, key, signatureAlgorithm, sign);
    }

    private static boolean verifyByPublicKey(byte[] data, PublicKey publicKey, SignatureAlgorithm signatureAlgorithm, String sign) throws Exception {
        Signature signature = Signature.getInstance(signatureAlgorithm.getName());
        signature.initVerify(publicKey);
        signature.update(data);

        // 验证签名是否正常
        byte[] decode = Base64Util.decode(sign);
        return signature.verify(decode);
    }

    /**
     * 使用私钥加密数据
     * @param data 明文数据
     * @param privateKey 私钥
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        // 获取私钥对象
        PrivateKey key = getPrivateKey(privateKey);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

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
        return encryptedData;
    }

    /**
     * 使用公钥加密数据
     * @param data 明文数据
     * @param publicKey 公钥
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        // 获取公钥对象
        PublicKey key = getPublicKey(publicKey);

        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

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
        return encryptedData;
    }

    /**
     * 使用私钥解密数据
     * @param encryptedData 密文数据
     * @param privateKey 私钥
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        // 获取私钥对象
        PrivateKey key = getPrivateKey(privateKey);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 使用公钥解密
     * @param encryptedData 密文数据
     * @param publicKey 公钥
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        // 获取公钥对象
        PublicKey key = getPublicKey(publicKey);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 生成密钥对
     * @param keysize 密钥长度：1024、2048
     */
    private static void generateKeyPair(Integer keysize) throws Exception {
        // 生成RSA Key
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keysize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        System.out.println("公钥: " + Base64Util.encode(publicKey.getEncoded()));
        System.out.println("私钥: " + Base64Util.encode(privateKey.getEncoded()));
    }

    public static void main(String[] args) throws Exception {
        // 生成密钥对
//
        RsaUtil.generateKeyPair(1024);
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCah+/uDqD9nqU4gikWWfLWIEwkb0uarlaIbDBs6WS1AyQ7v5nn2I4C91zHG8BqtWd0T210tjZ37NkNSPAEKIdcpue80lHv0e2PVIVufebnGBzGoLq9APZB/31ct77+91tVvnltwxfgxF0m3oAK3paXgEaU5yXCbjAyTZ1s9bdALwIDAQAB";
//        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJqH7+4OoP2epTiCKRZZ8tYgTCRvS5quVohsMGzpZLUDJDu/mefYjgL3XMcbwGq1Z3RPbXS2Nnfs2Q1I8AQoh1ym57zSUe/R7Y9UhW595ucYHMagur0A9kH/fVy3vv73W1W+eW3DF+DEXSbegArelpeARpTnJcJuMDJNnWz1t0AvAgMBAAECgYAPjiF7wCM6hG6zOn7yTgmgvk2L2Vwt7OCvCNzzCadmhTNvhED00pqw979yF3wOAcaxMZGeDv16ou/SY4YtlPrTE7CJiEkCRp45DGECW38WH6YqxcpOn1RUbz9LohWs7SnI3SSEnOl6R5b4W6GC1JNQiqMukwapGJ4/7rEvVEEQ0QJBANAa/yDcx0ZcSBgM4FMWzZJze7r361BrE7jR00L9CyuUB8QRwOTdJHoJVXQM0g2y/dTbo4CuEuqREoLRFw3H5RkCQQC+GHsSLcX0cIAj+37RxptqGR0b0GKQkd1OK4WtXOb/FzRAvpvupAHnoqJSR4JPbaCAUMejMGUm5tquixBZdvCHAkAaMZu1TjTC9XCZnl1J+A/OBD0prnTu/VtRIw/9WY5jYNGNa1KlO/SQa8ZWwhpaYRI0DaVJ2B9HaRU0ZA8LzAJZAkEAsnqRsrO/VI38AcyzeYobiTYjGmZA5LPPMQGz9N1xFLhYmyFbjmf2UeFkvhPdyW2IcHFnv1RE5I2DbLWsmBMQKQJATvPj3FK2siaHf2fre+1qdcYSTakuaFGis+gcHKIVHUdj1zPRGr2/ux1edQVthzOyTXxalKneGVXpchb63J0ItQ==";
//        byte[] data = "反对或回复迪欧".getBytes(Charsets.UTF_8);
//
//        byte[] encryptByPrivateKey = RsaUtil.encryptByPrivateKey(data, privateKey);
//        System.out.println("encryptByPrivateKey: " + Base64Util.encode(encryptByPrivateKey));
//        byte[] encryptByPublicKey = RsaUtil.encryptByPublicKey(data, publicKey);
//        System.out.println("encryptByPublicKey: " + Base64Util.encode(encryptByPublicKey));
//
//        byte[] decryptByPublicKey = RsaUtil.decryptByPublicKey(encryptByPrivateKey, publicKey);
//        System.out.println("decryptByPublicKey: " + new String(decryptByPublicKey, Charsets.UTF_8));
//        byte[] decryptByPrivateKey = RsaUtil.decryptByPrivateKey(encryptByPublicKey, privateKey);
//        System.out.println("decryptByPrivateKey: " + new String(decryptByPrivateKey, Charsets.UTF_8));
    }

}
