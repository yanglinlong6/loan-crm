package com.help.loan.distribute.service.api.utils;

import com.alibaba.fastjson.JSON;
import com.help.loan.distribute.common.utils.JSONUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class PingAnTransport {

    public static String encodeForm(Map<String, String> data)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, UnsupportedEncodingException {
        // 转换成json
        String json = JSONUtil.toJsonString(data);
        // 加密数据
        String cipher = PingAnTripleDES.encrypt(json);
        // 生成urlencoded格式
        return cipher;
    }

    public static Map<String, String> decodeForm(String cipher)
            throws InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException, InvalidAlgorithmParameterException {
        // 解密数据
        String json = PingAnTripleDES.decrypt(cipher);
        // 解析json
        Map<String, String> param=  (Map<String, String>) JSON.parse(json);
        return param;
    }

}
