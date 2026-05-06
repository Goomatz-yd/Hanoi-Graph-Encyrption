package org.example;
import java.math.BigInteger;

public class CipherResult {
    public final byte[] ciphertext;
    public final BigInteger seed;

    public CipherResult(byte[] ciphertext, BigInteger seed) {
        this.ciphertext = ciphertext;
        this.seed = seed;
    }
}