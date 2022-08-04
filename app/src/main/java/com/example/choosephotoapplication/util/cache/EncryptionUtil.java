package com.example.choosephotoapplication.util.cache;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密的工具类
 *
 * @author 86159
 */
public class EncryptionUtil {
    public String encrypt(String password) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
                hexValue.append(Integer.toHexString(val));
            }
        }
        return hexValue.toString();

    }

    public String encryptPath(String path) {
        String hashedPwd = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(path.getBytes());
            hashedPwd = new BigInteger(1, md.digest()).toString(16);
            System.out.println(hashedPwd);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hashedPwd;
    }

}
