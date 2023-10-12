package com.help.loan.distribute;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class JavaTest {

    private static final String URL = "http://sh.tongchengyoudai.com/buffgeDev.php";
    private static final String PUB_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwA+2i+pWLXBvEXFtN/f7"
            + "26jvAATWqJeFDiUfDwiKUzqZXCqHkj7UTzoXz+sKK1F1VqQ1m4yjZBGtqceDl/49"
            + "EVz2Dwt+971XjZ8RgKwoYgiFLDljp0VqrBXKUuywc17vSQdmYUQya06swtqMkJgO"
            + "FswybfqJulD9F3ZE67ouRregy3pmL+i/Sv2G9acvBJoMwbDYL8pQQFBIl2c3XOan"
            + "oLG1iEuhvn17FvazdeuaDJmVhsvfNO6c+OPoPAPuRefdxUSogBkZ1mZjrC3yxfFd"
            + "DhZqDR2BrrYq8dJH6voBNWx8fWjmfEv0tfepU4ZVUHUJum8x9CvoCLFXGmzioB0y"
            + "fQIDAQAB";
    private static final int MAX_ENCRYPT_BLOCK = 245;

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException,
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
            IOException {
        Map postData = new HashMap();
        postData.put("name", "buffge-test4151946");
        postData.put("birthday", "1974-11-19");
        postData.put("account", "15902152422");
        postData.put("city", "上海市");
        postData.put("agentId", "SHCD");
        postData.put("createTime", "2018-05-28 09:01:19");
        postData.put("creditSituation", "信用良好");
        postData.put("desc", "");
        postData.put("education", "");
        postData.put("hasCar", "有车无贷");
        postData.put("housingFund", "一年以上");
        postData.put("housingType", "有房还贷");
        postData.put("id", "923473sdf123sdfdsfs24234345f2e31");
        postData.put("income", 20000);
        postData.put("ip", "101.90.127.116");
        postData.put("money", 50000);
        postData.put("payType", "银行");
        postData.put("profession", "公务员");
        postData.put("sex", "男");
        postData.put("socialSecurity", "有");
        postData.put("status", "待处理");
        postData.put("temp4", "是");
        postData.put("username", "buffge");
        postData.put("password", "jskdhfjkweyiuryzbmn!@#!ddghjk76ADFF.sdfghjg");
        postData.put("origin", "buffge测试");
        String json = JSON.toJSONString(postData);
        Base64.Encoder encoder = Base64.getEncoder();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] keyBytes = decoder.decode(PUB_KEY);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key pubKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte[] jsonBytes = json.getBytes();
        int inputLen = jsonBytes.length;
        List enStrArr = new ArrayList<String>();
        int offSet = 0;
        byte[] enStr;
        int i = 0;
        while(inputLen - offSet > 0) {
            if(inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                enStr = cipher.doFinal(jsonBytes, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                enStr = cipher.doFinal(jsonBytes, offSet, inputLen - offSet);
            }
            enStrArr.add(encoder.encodeToString(enStr));
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(URL);
        post.setHeader("Auth", "buffge");
        List<NameValuePair> parameters = new ArrayList<>(0);
        parameters.add(new BasicNameValuePair("signData", JSON.toJSONString(enStrArr)));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        String content = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.printf("return code: %d,res: %s\n", statusCode, content);
    }

}
