package com.loan.cps.api.jiejiedai;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSAUtils {
	
	/**
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public static PrivateKey getPrivateKey(String privateKey) throws Exception{
		byte[] encodedKey = Base64.decodeBase64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}
	
	public static PublicKey getPublicKey(String publicKey) throws Exception{
		byte[] encodedKey = Base64.decodeBase64(publicKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(keySpec);
	}
	
	public static String sign(String data,PrivateKey privateKey) throws Exception{
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(privateKey);
		signature.update(data.getBytes("UTF-8"));
		return new Base64().encodeToString(signature.sign());
	}
	
	public static boolean verify(String data,String sign,PublicKey publicKey) throws Exception{
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initVerify(publicKey);
		signature.update(data.getBytes("UTF-8"));
		return signature.verify(Base64.decodeBase64(sign));
	}
	private static final Integer MAX_ENCRYPT_BLOCK = 117;
	private static final Integer MAX_DECRYPT_BLOCK = 128;
	
	public static String encrypt(String content,PrivateKey privateKey) throws Exception{
		if (privateKey == null) {  
            throw new Exception("加密私钥为空, 请设置");  
        } 
		ByteArrayOutputStream out = new ByteArrayOutputStream(); 
		Cipher cipher = null;  
	        try {  
	        	byte[] contentBytes = content.getBytes("UTF-8");
	        	int len = contentBytes.length; 
	        	int offSet = 0;
	        	byte[] part ;
	        	int i=0;
	        	 
	            // 使用默认RSA  
	            cipher = Cipher.getInstance("RSA");  
	            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
	            while (len - offSet > 0) { 
	            	if (len - offSet > MAX_ENCRYPT_BLOCK) {
	            		part = cipher.doFinal(contentBytes, offSet, MAX_ENCRYPT_BLOCK); 
	            	} else {
	            		part = cipher.doFinal(contentBytes, offSet, len - offSet); 
	            	}
	            	out.write(part, 0, part.length);
	            	i++;
	            	offSet = i * MAX_ENCRYPT_BLOCK; 
	            }
	            byte[] data = out.toByteArray();  
	            out.close();
	            return new Base64().encodeToString(data);
	        } catch (NoSuchAlgorithmException e) {  
	            throw new Exception("无此加密算法");  
	        } catch (NoSuchPaddingException e) {  
	            e.printStackTrace();  
	            return null;  
	        } catch (InvalidKeyException e) {  
	            throw new Exception("加密私钥非法,请检查");  
	        } catch (IllegalBlockSizeException e) {  
	            throw new Exception("明文长度非法");  
	        } catch (BadPaddingException e) {  
	            throw new Exception("明文数据已损坏");  
	        }
	 }
	
	public static byte[] decrypt(PublicKey publicKey, String content)  
            throws Exception {
        if (publicKey == null) {
            throw new Exception("解密公钥为空, 请设置");  
        }  
        Cipher cipher = null;  
        try {  
        	byte[] contentBytes = Base64.decodeBase64(content);
        	int len = contentBytes.length; 
        	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
        	int offSet = 0; 
        	byte[] part; 
        	int i = 0; 
        	
        	 // 使用默认RSA  
            cipher = Cipher.getInstance("RSA");  
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            
        	while (len - offSet > 0) { 
        		 if (len - offSet > MAX_DECRYPT_BLOCK) { 
        			 part= cipher.doFinal(contentBytes, offSet, MAX_DECRYPT_BLOCK);
        		 } else { 
        			 part= cipher.doFinal(contentBytes, offSet, len - offSet);
        		 }
        		 out.write(part, 0, part.length); 
 				 i++; 
 				 offSet = i * MAX_DECRYPT_BLOCK; 
        	}
        	byte[] output = out.toByteArray();
 			out.close(); 
            return output;  
        } catch (NoSuchAlgorithmException e) {  
            throw new Exception("无此解密算法");  
        } catch (NoSuchPaddingException e) {  
            e.printStackTrace();  
            return null;  
        } catch (InvalidKeyException e) {  
            throw new Exception("解密公钥非法,请检查");  
        } catch (IllegalBlockSizeException e) {  
            throw new Exception("密文长度非法");  
        } catch (BadPaddingException e) {  
            throw new Exception("密文数据已损坏");  
        }  
    }  
	
	public static void main1(String[] args) throws Exception {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(1024);
		KeyPair keyPair = generator.generateKeyPair();
		String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
		String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
//		privateKey = "";
//		publicKey = "";
//		
//		privateKey += "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMUIytaKkSy4tCMi";
//		privateKey += "jRwsFa/Yuw60H1hsGrG5MLFs4G11I3+XoUGmTwN4pBuDmKaU2cMvb0RLGg1wMlRu";
//		privateKey += "owfiAqtT0JkW0eCGpnkYRg2Tj99M8XKXs9vi7O/aMuaW7pnJLzS1sJbzXMV9+5c1";
//		privateKey += "7WGgCl45GeqRkXptWIWzt2VaWWQNAgMBAAECgYB6QrxqZ80xbOMKMjRRWOGUHe3k";
//		privateKey += "b8xLd7kQspMOZTrVcKw4TgRX0jSRONjL4dpk81Q0k0h9ngRqdkYksgojqBBy3ARm";
//		privateKey += "k+0QtO7a5+rEvsvOUquGz+f8xjgppBLBmcaPvjLpZuxeJntQ8VV804s9uS5a1YMI";
//		privateKey += "o21K8nJ9YQmTwMxeQQJBAO2FY0WV6iPCTMSyz1YLceWSK0E2dfXQLyZl32RhJ22f";
//		privateKey += "VzVXk3t1rBLe1EXqtCpQuO3gWnznyqrk3obpc7MF1YUCQQDUXQwKWK1WRBM0LUUB";
//		privateKey += "CtKQfkeHwDxzxeSFPTxM4MKd0issIdX2H+KvGP72QT5lxc7RVpucT9oXDM2qsIcj";
//		privateKey += "ADbpAkBJffpHb6lwww/p1MHeh9AIDoAfv3AkTPQp11+VuZHvk1vyf1R84N4LQLNB";
//		privateKey += "put0JGH0CHU3LKlC02ofKGDKRcXZAkEArhxrlsAFvciqhgjnnmclJFqkguRVrAX/";
//		privateKey += "yk3edpmAdqytwM0tA1I5JJ41y+jKI97+JhwAETRW9rcEmIGLCmNOoQJAC2YAKXnY";
//		privateKey += "ooagEpp0ct6jlE08jih1zEoEtehqXeMNDuK/918LLshmZ410hPEoKFdv1Vi/ftN6";
//		privateKey += "Z9cCCNhchRzCFg==";
//		
//		publicKey  += "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDFCMrWipEsuLQjIo0cLBWv2LsO";
//		publicKey  += "tB9YbBqxuTCxbOBtdSN/l6FBpk8DeKQbg5imlNnDL29ESxoNcDJUbqMH4gKrU9CZ";
//		publicKey  += "FtHghqZ5GEYNk4/fTPFyl7Pb4uzv2jLmlu6ZyS80tbCW81zFffuXNe1hoApeORnq";
//		publicKey  += "kZF6bViFs7dlWllkDQIDAQAB";
		
		System.out.println("privateKey:"+privateKey);
		System.out.println("publicKey:"+publicKey);
		
		String data = "appId=1234&bid=12948d&did=9802&signType=RSA";
		String sign = sign(data, getPrivateKey(privateKey));
		System.out.println(sign);
		
		System.out.println(verify(data, sign, getPublicKey(publicKey)));
	}
	
	public static void main(String[] args) {
		String aa = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbX3nbQL1/REa8Gw2s0gcaRYPpfsGiSOGLB4PFSGzjAf6rK8ntw3YFJUFpAAtIklh/NR83IkWFCa3rT+ADMKFdWroWX5AUO3ZpMiZj+yXLajoHxg+WO1K9gW6unOXCEYvfpeeI7TFyA5OPiNM3LcO6/FCSv2eqEhvpKAlj9htgbAgMBAAECgYAb4m3ygsuvJ8PgGhz37W3sGiXWQugmfJlF0FdX5Pi5RgSTuw6WOSgJZVcgR+L9m9vQpy8V4HTXiOK7gwG+i/8M1HyGapZjaEY+zxSXP9b5U4Gg/034xFnlrnbf+VGYK14SjxnT/L7rjnrBdAJy1dFlwPfXC00AL3FBb5P3dbzlqQJBAOi1rn9WuzY9v9zUFGiuwz8s2p3pqSBqqy7cWblqfGKBmKiSHN9A8SI7yCdxzBUsuBI5biUpwvVmHF+c9mheWD8CQQCl8Kol8/aZDJHS7lTtXq50q7TfcmyPfbBBCbJS3Xs4+KOAoLTCfSOfRaWc+CdsxL2GQcSR1FH6/fCu7OVzviklAkBPkff1sKlY9apKDvZfiQpX6rVh84iR/gdEgbHoVYHsiNWzJirbJ7CL/RcGbcHTp7PU/3ArIul30Y94HgqfS0svAkA2cM+RpgloSuxolFJ+kcqQcmjjygl9xhGWNNUxRIKRvVj+8Tp8eEvAjDv6VJuynpVDP756zXqb6sYUzZm1sFSVAkEA2TUZPmhRDxllFwyyp+FvH5t2p90IBqWs4FBG+O8Ivyd3vHIKk1Esr3FJYzhNjtCMDBDeGrhpvz5w1gBfUEGcfg==";
		int index = 0;
		while(aa.length()>index) {
			System.out.println(index);
			int a = index;
			int b = index+100;
			if(b>aa.length()) {
				b = aa.length();
			}
			System.out.println(aa.substring(a, b));
			index = b;
		}
	}
}
