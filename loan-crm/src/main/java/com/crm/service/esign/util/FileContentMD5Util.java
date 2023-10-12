package com.crm.service.esign.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileContentMD5Util {

    private static final Logger LOG = LoggerFactory.getLogger(FileContentMD5Util.class);

    /***
     * 计算字符串的Content-MD5
     * @param str 文件路径
     * @return
     */
    public static String getStringContentMD5(String str) {
        // 获取文件MD5的二进制数组（128位）
        byte[] bytes = getFileMD5Bytes1282(str);
        // 对文件MD5的二进制数组进行base64编码
        return new String(Base64.encodeBase64(bytes));
    }
    /***
     * 获取文件MD5-二进制数组（128位）
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    private static byte[] getFileMD5Bytes1282(String filePath) {
        FileInputStream fis = null;
        byte[] md5Bytes = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }
            md5Bytes = md5.digest();
            fis.close();
        } catch (FileNotFoundException e) {
            LOG.error("获取文件内容Md5异常,FileNotFoundException:{},{}",e.getMessage(),e);
        } catch (NoSuchAlgorithmException e) {
            LOG.error("获取文件内容Md5异常,NoSuchAlgorithmException:{},{}",e.getMessage(),e);
        } catch (IOException e) {
            LOG.error("获取文件内容Md5异常,IOException:{},{}",e.getMessage(),e);
        }
        return md5Bytes;
    }
}
