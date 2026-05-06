package org.example;
import java.math.BigInteger;

public class KeyBundle {
    public final byte[] keystream;
    public final BigInteger publicSeed;

    public KeyBundle(byte[] keystream, BigInteger publicSeed) {
        this.keystream = keystream;
        this.publicSeed = publicSeed;
    }
}