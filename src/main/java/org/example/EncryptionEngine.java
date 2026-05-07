package org.example;
import java.math.BigInteger;

public class EncryptionEngine {
    /**
     * Encrypts a message using the Hanoi algorithm.
     * @param message
     * @param r
     * @param l
     * @return
     * @throws InterruptedException
     */
    public static CipherResult encrypt(byte[] message, int r, int l) throws InterruptedException {
        int k = message.length * 4;
        KeyBundle bundle = KeyGenerator.generateKey(r, l, k);
        byte[] ciphertext = new byte[message.length];
        for (int i = 0; i < message.length; i++) {
            ciphertext[i] = (byte) (message[i] ^ bundle.keystream[i]);
        }
        return new CipherResult(ciphertext, bundle.publicSeed);
    }

    /**
     * Decrypts a message using the Hanoi algorithm.
     * @param r
     * @param ciphertext
     * @param seed
     * @return
     */
    public static byte[] decrypt(int r, byte[] ciphertext, BigInteger seed) {
        int k = ciphertext.length * 4;
        byte[] keystream = KeyGenerator.seedToKey(r, k, seed);
        byte[] plaintext = new byte[ciphertext.length];
        for (int i = 0; i < ciphertext.length; i++) {
            plaintext[i] = (byte) (ciphertext[i] ^ keystream[i]);
        }
        return plaintext;
    }
}