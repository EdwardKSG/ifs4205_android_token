package com.ifs4205.fingerprinttoken;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


/**
 * HMAC-based one-time password (HOTP) according to https://tools.ietf.org/html/rfc4226
 */
public class HmacOtp {
    private final Mac mac;

    private final byte[] buffer;
    private final int modDivisor;

    public static final String HOTP_HMAC_ALGORITHM = "HmacSHA1";

    /**
     * Configure a HMAC-based one-time password generating process with target password length 6 and HMAC-SHA1 algorithm
     *
     * @throws NoSuchAlgorithmException this exception is not caught and handled because HMAC-SHA is guaranteed to exist
     */
    protected HmacOtp( ) throws NoSuchAlgorithmException {
        this.mac = Mac.getInstance(HOTP_HMAC_ALGORITHM);

        // corresponds to the default password length which is 6
        this.modDivisor = 1_000_000;

        this.buffer = new byte[this.mac.getMacLength()];
    }

    /**
     * Generates a one-time password using the given key and counter value.
     *
     * @param keyString the key (in string format) to be used to generate the password
     * @param counter the counter value for which to generate the password
     *
     * @return a string representation of a one-time password
     *
     * @throws InvalidKeyException if the given key is inappropriate for initializing the {@link Mac} for this generator
     */
    public synchronized String generateHotp(final String keyString, final long counter) throws InvalidKeyException {
        byte[] decodedKey = Base64.decode(keyString, Base64.DEFAULT);
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        this.mac.init(key);

        this.buffer[0] = (byte) ((counter & 0xff00000000000000L) >>> 56);
        this.buffer[1] = (byte) ((counter & 0x00ff000000000000L) >>> 48);
        this.buffer[2] = (byte) ((counter & 0x0000ff0000000000L) >>> 40);
        this.buffer[3] = (byte) ((counter & 0x000000ff00000000L) >>> 32);
        this.buffer[4] = (byte) ((counter & 0x00000000ff000000L) >>> 24);
        this.buffer[5] = (byte) ((counter & 0x0000000000ff0000L) >>> 16);
        this.buffer[6] = (byte) ((counter & 0x000000000000ff00L) >>> 8);
        this.buffer[7] = (byte)  (counter & 0x00000000000000ffL);

        this.mac.update(this.buffer, 0, 8);

        try {
            this.mac.doFinal(this.buffer, 0);
        } catch (final ShortBufferException e) {

            // Buffers were allocated to (at least) match the size of the MAC length at construction time, so this should never happen.
            throw new RuntimeException(e);
        }

        final int offset = this.buffer[this.buffer.length - 1] & 0x0f;

        int otpInt = ((this.buffer[offset]     & 0x7f) << 24 |
                (this.buffer[offset + 1] & 0xff) << 16 |
                (this.buffer[offset + 2] & 0xff) <<  8 |
                (this.buffer[offset + 3] & 0xff)) %
                this.modDivisor;

        String otp = Integer.toString(otpInt);
        while (otp.length() < 6)
        {
            otp = "0" + otp;
        }

        return otp;
    }

}

