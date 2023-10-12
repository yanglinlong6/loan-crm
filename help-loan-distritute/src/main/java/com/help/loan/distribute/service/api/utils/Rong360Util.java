package com.help.loan.distribute.service.api.utils;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;

public class Rong360Util {

    public static final Charset CHARSET = Charset.forName("utf-8");

    public static final byte keyStrSize = 16;

    public static final byte ivStrSize = 16;

    public static final String ALGORITHM = "AES";

    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";



    /**
     * 为了平台的通用，选择 AES/CBC/NoPadding 的模式，然后手动 padding
     * 对应PHP 平台为 mcrypt_encrypt(MCRYPT_RIJNDAEL_128, $key, $data, MCRYPT_MODE_CBC, $iv)
     *
     * AES/CBC/NoPadding encrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @return
     */
    public static byte[] encryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
            int inputLength = input.length;
            int srcLength;

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            int blockSize = cipher.getBlockSize();
            byte[] srcBytes;
            if (0 != inputLength % blockSize) {
                srcLength = inputLength + (blockSize - inputLength % blockSize);
                srcBytes = new byte[srcLength];
                System.arraycopy(input, 0, srcBytes, 0, inputLength);
            } else {
                srcBytes = input;
            }

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(srcBytes);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES/CBC/NoPadding decrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param input
     * @return
     */
    public static byte[] decryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(input);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static byte[] encode(String secretStr, String ivStr, String inputStr) {
        if (keyStrSize != secretStr.length() || ivStrSize != ivStr.length()){
            return null;
        }
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = ivStr.getBytes(CHARSET);
        byte[] inputBytes = inputStr.getBytes(CHARSET);

        byte[] outputBytes = encryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     * 并对加密后的字节数组调用 sun.misc.BASE64Encoder.encode 方法，
     * 转换成 base64 字符串返回。
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static String strEncodBase64(String secretStr,String ivStr, String inputStr){
        String base64Str = Base64.encodeBase64String(encode(secretStr, ivStr, inputStr));
        return base64Str;
    }


    public static byte[] decode(String secretStr, String ivStr, byte[] inputBytes){
        if (keyStrSize != secretStr.length() || ivStrSize != ivStr.length()) {
            return null;
        }
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = ivStr.getBytes(CHARSET);

        byte[] outputBytes = decryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，ivStr作为 iv。
     * 并对加密后的字节数组调用 sun.misc.BASE64Encoder.encode 方法，
     * 转换成 base64 字符串返回。
     *
     * （仅作为测试用途，具体加密流程以接口文档为准）
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static String base64StrDecode(String secretStr, String ivStr, String inputStr){
        byte[] inputBytes;
        inputBytes = Base64.decodeBase64(inputStr);
        String outputStr = new String(decode(secretStr, ivStr, inputBytes), CHARSET);
        System.out.println("base64Decode > base64 decrypt " + outputStr);
        return outputStr;
    }


//    public static void main(String[] args) {
//        String key = "UA3MzZiMTgwZDB2B";
//        String iv = "iqZVGCCFVMkjMYUE";
//        String salt = "zyzqndyh";
//        String mobile = "15750822844";
//        String source = "rong360test";
//        String timestamp = "1537173210";
//        String mobileEncode =  strEncodBase64(key, iv, mobile);//加密
//        System.out.println("encodedStr = " + mobileEncode);
//        System.out.println("decodedStr = " + new String(base64StrDecode(key,mobileEncode,iv)));//解密
//    }



}
