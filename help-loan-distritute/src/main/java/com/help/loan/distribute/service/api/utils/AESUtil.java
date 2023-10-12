package com.help.loan.distribute.service.api.utils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

public class AESUtil {

    public static final String AES = "AES";

    private static final String ALGORITHMSTR = "AES/ECB/PKCS7Padding";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * encrypt input text
     *
     * @param input
     * @param key
     * @return
     * @throws Exception
     */
    public static String encrypt(String input, String key) throws Exception {

        SecretKeySpec skey = new SecretKeySpec(key.getBytes(), AES);
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        cipher.init(Cipher.ENCRYPT_MODE, skey);
        byte[] crypted = cipher.doFinal(input.getBytes("UTF8"));

        return new String(Base64.encodeBase64(crypted));
    }
}
