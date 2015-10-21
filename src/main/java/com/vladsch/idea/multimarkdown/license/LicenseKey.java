package com.vladsch.idea.multimarkdown.license;

import org.apache.commons.codec.binary.Base64;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class LicenseKey {
    public static final int KEY_LENGTH = 4096;
    // PKCS#8 format
    final public static String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    final public static String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    // PKCS#1 format
    final public static String RSA_PRIVATE_KEY_HEADER = "-----BEGIN RSA PRIVATE KEY-----";
    final public static String RSA_PRIVATE_KEY_FOOTER = "-----END RSA PRIVATE KEY-----";
    // X509 format
    final public static String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    final public static String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
    final private Cipher cipher;
    final private Key key;
    final private int keyLength;

    public LicenseKey(String encodedKey, boolean isPrivate) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Cipher cipher = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        try {
            cipher = Cipher.getInstance(LicenseKeyPair.ALGORITHM_RSA);
            if (isPrivate) {
                privateKey = getPrivateKey(encodedKey);
            } else {
                publicKey = getPublicKey(encodedKey);
            }
        } finally {
            this.key = isPrivate ? privateKey : publicKey;
            this.keyLength = KEY_LENGTH;
            this.cipher = cipher;
        }
    }

    public LicenseKey(String wrappedKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Cipher cipher = null;
        PublicKey publicKey = null;
        PrivateKey privateKey = null;

        try {
            cipher = Cipher.getInstance(LicenseKeyPair.ALGORITHM_RSA);
            Boolean isPrivate = isKeyPrivate(wrappedKey);
            if (isPrivate == null) throw new InvalidKeySpecException();

            if (isPrivate) {
                privateKey = getPrivateKey(wrappedKey);
            } else {
                publicKey = getPublicKey(wrappedKey);
            }
        } finally {
            this.key = privateKey != null ? privateKey : publicKey;
            this.keyLength = KEY_LENGTH;
            this.cipher = cipher;
        }
    }

    public LicenseKey(RSAPublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(LicenseKeyPair.ALGORITHM_RSA);
        } finally {
            this.key = publicKey;
            this.keyLength = KEY_LENGTH;
            this.cipher = cipher;
        }
    }

    private LicenseKey(RSAPrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance(LicenseKeyPair.ALGORITHM_RSA);
        } finally {
            this.key = privateKey;
            this.keyLength = KEY_LENGTH;
            this.cipher = cipher;
        }
    }
    public static byte[][] chunkArray(byte[] source, int chunksize) {
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

        int start = 0;

        for (int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, start, start + chunksize);
            start += chunksize;
        }

        return ret;
    }
    public static String chunkString(byte[] string, int length) throws UnsupportedEncodingException {
        return chunkString(new String(string,"UTF-8"), length);
    }

    public static String chunkString(String string, int length) {
        int pos = 0;
        StringBuilder result = new StringBuilder(string.length() + (int) Math.ceil(string.length()/(double)length));
        while (pos < string.length()) {
            int len = length;
            if (pos + len > string.length()) len = string.length() - pos;
            result.append(string.substring(pos, pos+len));
            if (len == length) result.append('\n');
            pos += len;
        }
        return result.toString();
    }

    public static Boolean isKeyPrivate(String wrappedKey) {
        if (wrappedKey.startsWith(RSA_PRIVATE_KEY_HEADER)||wrappedKey.startsWith(PRIVATE_KEY_HEADER)) {
            return true;
        }
        if (wrappedKey.startsWith(PUBLIC_KEY_HEADER)) {
            return false;
        }
        return null;
    }

    protected static String getUnwrappedKey(String wrappedKey, String header, String footer) throws InvalidKeySpecException {
        if (!(wrappedKey.startsWith(header) && wrappedKey.contains(footer))) {
            throw new InvalidKeySpecException();
        }
        return wrappedKey.substring(header.length(), wrappedKey.lastIndexOf(footer) + footer.length()).replaceAll("\\s","");
    }

    public static PrivateKey getPrivateKey(String privateKeyPem) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        if (privateKeyPem.contains(PRIVATE_KEY_HEADER)) { // PKCS#8 format
            privateKeyPem = getUnwrappedKey(privateKeyPem, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);

            byte[] pkcs8EncodedKey = Base64.decodeBase64(privateKeyPem);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(new PKCS8EncodedKeySpec(pkcs8EncodedKey));

        } else if (privateKeyPem.contains(RSA_PRIVATE_KEY_HEADER)) {  // PKCS#1 format
            privateKeyPem = getUnwrappedKey(privateKeyPem, RSA_PRIVATE_KEY_HEADER, RSA_PRIVATE_KEY_FOOTER);
            DerInputStream derReader = new DerInputStream(Base64.decodeBase64(privateKeyPem));
            DerValue[] seq = derReader.getSequence(0);

            if (seq.length < 9) {
                throw new InvalidKeySpecException("Could not parse a PKCS1 private key.");
            }

            // skip version seq[0];
            BigInteger modulus = seq[1].getBigInteger();
            BigInteger publicExp = seq[2].getBigInteger();
            BigInteger privateExp = seq[3].getBigInteger();
            BigInteger prime1 = seq[4].getBigInteger();
            BigInteger prime2 = seq[5].getBigInteger();
            BigInteger exp1 = seq[6].getBigInteger();
            BigInteger exp2 = seq[7].getBigInteger();
            BigInteger crtCoef = seq[8].getBigInteger();

            RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return factory.generatePrivate(keySpec);
        }

        throw new InvalidKeySpecException("Not supported format of a private key");
    }

    public static PublicKey getPublicKey(String wrappedKey) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        String encodedKey = getUnwrappedKey(wrappedKey, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
        byte[] decodedKey = Base64.decodeBase64(encodedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(LicenseKeyPair.ALGORITHM_RSA);
        return keyFactory.generatePublic(keySpec);
    }

    public boolean isValid() {
        return cipher != null && key != null && keyLength > 0;
    }

    public String encrypt(String plaintext) throws Exception {
        assert isValid();

        this.cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] bytes = plaintext.getBytes("UTF-8");
        String encoded = new String(Base64.encodeBase64(blockCipher(bytes, Cipher.ENCRYPT_MODE)), "UTF-8");
        return chunkString(encoded, 76);
    }

    public String decrypt(String encrypted) throws Exception {
        assert isValid();

        byte[] bts = Base64.decodeBase64(encrypted.replaceAll("\\s", ""));

        this.cipher.init(Cipher.DECRYPT_MODE, this.key);
        byte[] decrypted = blockCipher(bts, Cipher.DECRYPT_MODE);
        return new String(decrypted, "UTF-8");
    }

    private byte[] blockCipher(byte[] bytes, int mode) throws IllegalBlockSizeException, BadPaddingException {
        // string initialize 2 buffers.
        // scrambled will hold intermediate results
        byte[] scrambled = new byte[0];

        // toReturn will hold the total result
        byte[] toReturn = new byte[0];
        // if we encrypt we use 100 byte long blocks. Decryption requires 128 byte long blocks (because of RSA)
        //int length = (mode == Cipher.ENCRYPT_MODE)? 100 : 128;
        int log2 = 0;
        int val = keyLength >> 10;
        while (val > 1) {
            val >>= 1;
            log2++;
        }

        int length = (mode == Cipher.ENCRYPT_MODE) ? 100 * (1 << log2) : (keyLength >> 3);

        // another buffer. this one will hold the bytes that have to be modified in this step
        //byte[] buffer = new byte[length];
        byte[] buffer = new byte[(bytes.length > length ? length : bytes.length)];

        for (int i = 0; i < bytes.length; i++) {

            // if we filled our buffer array we have our block ready for de- or encryption
            if ((i > 0) && (i % length == 0)) {
                //execute the operation
                scrambled = cipher.doFinal(buffer);
                // add the result to our total result.
                toReturn = append(toReturn, scrambled);
                // here we calculate the length of the next buffer required
                int newlength = length;

                // if newlength would be longer than remaining bytes in the bytes array we shorten it.
                if (i + length > bytes.length) {
                    newlength = bytes.length - i;
                }
                // clean the buffer array
                buffer = new byte[newlength];
            }
            // copy byte into our buffer.
            buffer[i % length] = bytes[i];
        }

        // this step is needed if we had a trailing buffer. should only happen when encrypting.
        // example: we encrypt 110 bytes. 100 bytes per run means we "forgot" the last 10 bytes. they are in the buffer array
        scrambled = cipher.doFinal(buffer);

        // final step before we can return the modified data.
        toReturn = append(toReturn, scrambled);

        return toReturn;
    }

    private byte[] append(byte[] prefix, byte[] suffix) {
        byte[] toReturn = new byte[prefix.length + suffix.length];
        //for (int i = 0; i < prefix.length; i++) {
        //    toReturn[i] = prefix[i];
        //}
        //for (int i = 0; i < suffix.length; i++) {
        //    toReturn[i + prefix.length] = suffix[i];
        //}
        System.arraycopy(prefix, 0, toReturn, 0, prefix.length);
        System.arraycopy(suffix, 0, toReturn, prefix.length, suffix.length);
        return toReturn;
    }
}
