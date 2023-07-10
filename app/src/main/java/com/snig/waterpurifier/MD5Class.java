package com.snig.waterpurifier;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Class {
    public static String encode(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(input.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null; // Error occurred, return null or handle it accordingly
    }

    public static String decode(String md5Hash) {
        try {
            // Create MD5 instance
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Convert the MD5 hash to byte array
            byte[] hashBytes = hexStringToByteArray(md5Hash);

            // Compute the MD5 hash
            byte[] decodedBytes = md.digest(hashBytes);

            // Convert the byte array to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : decodedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
