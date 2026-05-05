package org.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class HanoiMath {
    static public BigInteger sumFirstRowWarshallForKLong(int r, int k) {
        int statesAmount = 3;
        for (int i = r; i >= 1; i--) {
            statesAmount *= 3;
        }
        BigInteger[] currentPaths = new BigInteger[statesAmount];
        for (int i = 0; i < statesAmount; i++) {
            currentPaths[i] = BigInteger.ZERO;
        }
        currentPaths[0] = BigInteger.ONE;
        for (int i = 0; i < k; i++) {
            BigInteger[] nextPaths = new BigInteger[statesAmount];
            for (int j = 0; j < statesAmount; j++) {
                nextPaths[j] = BigInteger.ZERO;
            }
            for (int node = 0; node < statesAmount; node++) {
                int[] neighbours = HanoiLogic.getLegalNeighbors(node, r);
                for (int neighbour : neighbours) {
                    nextPaths[neighbour] = nextPaths[neighbour].add(currentPaths[node]);
                }
            }
            System.arraycopy(nextPaths, 0, currentPaths, 0, statesAmount);
        }
        BigInteger totalPaths = BigInteger.ZERO;
        for (int i = 0; i < statesAmount; i++) {
            totalPaths = totalPaths.add(currentPaths[i]);
        }
        return totalPaths;
    }

    private static BigInteger[][] buildDPTable(int r, int k) {
        int numNodes = (int) Math.pow(3, r);
        BigInteger[][] dpTable = new BigInteger[k + 1][numNodes];
        int corner0 = 0, corner1 = (numNodes - 1) / 2, corner2 = numNodes - 1;

        for (int node = 0; node < numNodes; node++) {
            if (node == corner0 || node == corner1 || node == corner2) {
                dpTable[0][node] = BigInteger.ONE;
            } else {
                dpTable[0][node] = BigInteger.ZERO;
            }
        }

        for (int step = 1; step <= k; step++) {
            for (int node = 0; node < numNodes; node++) {
                dpTable[step][node] = BigInteger.ZERO;
                int[] neighbors = HanoiLogic.getLegalNeighbors(node, r);
                for (int neighbor : neighbors) {
                    dpTable[step][node] = dpTable[step][node].add(dpTable[step - 1][neighbor]);
                }
            }
        }
        return dpTable;
    }

    public static List<Integer> generateNthPath(int r, int k, BigInteger targetN) {
        BigInteger[][] dpTable = buildDPTable(r, k);
        List<Integer> path = new ArrayList<>();
        int currentNode = 0;
        path.add(currentNode);

        for (int step = k; step >= 1; step--) {
            int[] neighbors = HanoiLogic.getLegalNeighbors(currentNode, r);

            for (int neighbor : neighbors) {
                BigInteger pathsDownThisBranch = dpTable[step - 1][neighbor];
                if (targetN.compareTo(pathsDownThisBranch) <= 0) {
                    currentNode = neighbor;
                    path.add(currentNode);
                    break;
                } else {
                    targetN = targetN.subtract(pathsDownThisBranch);
                }
            }
        }
        return path;
    }

    public static BigInteger extrapolateNumFromPath(List<Integer> path) {
        BigInteger seed = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.valueOf(3);
        for (int move : path) {
            seed = seed.multiply(multiplier).add(BigInteger.valueOf(move));
        }
        return seed;
    }

    public static BigInteger magnifySeed(BigInteger baseSeed, BigInteger maxConjectures) {
        return null;
    }
}


