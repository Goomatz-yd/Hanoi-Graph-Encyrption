package org.example;

import java.math.BigInteger;
import java.util.List;

public class KeyGenerator {
    /**
     * generates a keystream from a seed inspired by extrapolateNumFromPath
     * in psuedocode this function utilizes extrapolateNumFromPath to generate a path and then uses that path to generate the keystream
     * @param r
     * @param k
     * @param seed
     * @return
     */
    public static byte[] seedToKey(int r, int k, BigInteger seed) {
        List<Integer> path = HanoiMath.generateNthPath(r, k, seed);
        int numBytes = (k * 2 + 7) / 8;
        byte[] keystream = new byte[numBytes];

        int bitIndex = 0;
        for (int i = 0; i < k; i++) {
            int currentState = path.get(i);
            int nextState = path.get(i + 1);
            int[] neighbors = HanoiLogic.getLegalNeighbors(currentState, r);
            int choiceIndex = 0;
            for (int j = 0; j < neighbors.length; j++) {
                if (neighbors[j] == nextState) {
                    choiceIndex = j;
                    break;
                }
            }
            int bytePos = bitIndex / 8;
            int bitPos = 6 - (bitIndex % 8);
            keystream[bytePos] |= (choiceIndex << bitPos);
            bitIndex += 2;
        }

        return keystream;
    }

    /**
     * dictates the flow of the key generation process
     * @param r
     * @param l
     * @param k
     * @return
     * @throws InterruptedException
     */
    public static KeyBundle generateKey(int r, int l, int k) throws InterruptedException {
        List<Integer> seedPath = TUIManager.getPlayerHanoiInput(r, l);
        BigInteger baseSeed = HanoiMath.extrapolateNumFromPath(seedPath);
        BigInteger maxPaths = HanoiMath.sumFirstRowWarshallForKLong(r, k);
        BigInteger finalSeed = HanoiMath.magnifySeed(l, baseSeed, maxPaths);
        byte[] keystream = seedToKey(r, k, finalSeed);
        return new KeyBundle(keystream, finalSeed);
    }
}
