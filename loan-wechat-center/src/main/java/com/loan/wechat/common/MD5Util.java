package com.loan.wechat.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class MD5Util {
	
	public static String digest(String content) {
		MessageDigest instance = null;
		try {
			instance = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] digest = instance.digest(content.getBytes());
		return Hex.encodeHexString(digest);
	}
	
	
}
