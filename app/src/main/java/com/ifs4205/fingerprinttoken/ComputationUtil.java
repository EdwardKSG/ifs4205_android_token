package com.ifs4205.fingerprinttoken;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ComputationUtil {
    public static String hash (String... keys) {
        String result = "";

        try {
            for (String key : keys) {
                result = bytesToHexString(getSHA(result + key));
            }
        } catch (NoSuchAlgorithmException e) {
            //temporarily do nothing, because the exception shouldn't happen
        }

        return result;
    }

    /**
     * Use SHA-256 to compute message digest
     *
     * @param input the input message
     *
     * @return a byte array containing the message digest
     */
    public static byte[] getSHA(String input) throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // calculate message digest of an input and return a byte array
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Convert a byte array to a hex string
     *
     * @param hash the message digest presented as byte array
     *
     * @return the message digest represented as a 32-byte hex string
     */
    public static String bytesToHexString(byte[] hash)
    {
        // Convert a byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 32)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }
}