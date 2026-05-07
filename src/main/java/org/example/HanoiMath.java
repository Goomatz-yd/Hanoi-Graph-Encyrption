package org.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class HanoiMath {
    /**
     * effectivly sums the first row of an adjecancy matrix for k moves like in warshall but again without generating the graph
     * instead it takes a dfs approach in the loop to count the amount of paths from the first state(00000) to every other state
     * @param r
     * @param k
     * @return
     */
    public static BigInteger sumFirstRowWarshallForKLong(int r, int k) {
        int statesAmount = 1;
        for (int i = r; i >= 1; i--) {
            statesAmount *= 3;
        }

        BigInteger[] currentPaths = new BigInteger[statesAmount];
        for (int i = 0; i < statesAmount; i++) {
            currentPaths[i] = BigInteger.ZERO;
        }
        currentPaths[0] = BigInteger.ONE;

        for (int step = 0; step < k; step++) {
            BigInteger[] nextPaths = new BigInteger[statesAmount];
            for (int j = 0; j < statesAmount; j++) {
                nextPaths[j] = BigInteger.ZERO;
            }
            for (int node = 0; node < statesAmount; node++) {
                if (currentPaths[node].compareTo(BigInteger.ZERO) > 0) {
                    int[] neighbors = HanoiLogic.getLegalNeighbors(node, r);
                    for (int neighbor : neighbors) {
                        nextPaths[neighbor] = nextPaths[neighbor].add(currentPaths[node]);
                    }
                }
            }
            currentPaths = nextPaths;
        }

        int corner0 = 0;
        int corner1 = (statesAmount - 1) / 2;
        int corner2 = statesAmount - 1;

        return currentPaths[corner0].add(currentPaths[corner1]).add(currentPaths[corner2]);
    }

    /**
     * used in generate nth path, build somewhat of an adjecancy matrix for k moves but in reverse so generate nth path is O(k)
     * @param r
     * @param k
     * @return
     */
    private static BigInteger[][] buildDPTable(int r, int k) {
        int statesAmount = 1;
        for (int i = r; i >= 1; i--) {
            statesAmount *= 3;
        }
        BigInteger[][] dpTable = new BigInteger[k + 1][statesAmount];
        int corner0 = 0, corner1 = (statesAmount - 1) / 2, corner2 = statesAmount - 1;

        for (int node = 0; node < statesAmount; node++) {
            if (node == corner0 || node == corner1 || node == corner2) {
                dpTable[0][node] = BigInteger.ONE;
            } else {
                dpTable[0][node] = BigInteger.ZERO;
            }
        }

        for (int step = 1; step <= k; step++) {
            for (int node = 0; node < statesAmount; node++) {
                dpTable[step][node] = BigInteger.ZERO;
                int[] neighbors = HanoiLogic.getLegalNeighbors(node, r);
                for (int neighbor : neighbors) {
                    dpTable[step][node] = dpTable[step][node].add(dpTable[step - 1][neighbor]);
                }
            }
        }
        return dpTable;
    }

    /**
     * utilizes the dp table to generate the nth path in O(k) time by searching for the bigger decrease in the dp table each time(combi-unranking)
     * @param r
     * @param k
     * @param targetN - path number in order of all paths
     * @return
     */
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

    /**
     * extrapolates the number from a path by multiplying the running total of moves by 3 and adding the move - therefore matching the added number to the move placement in order
     * @param path
     * @return
     */
    public static BigInteger extrapolateNumFromPath(List<Integer> path) {
        BigInteger seed = BigInteger.ZERO;
        BigInteger multiplier = BigInteger.valueOf(3);
        for (int move : path) {
            seed = seed.multiply(multiplier).add(BigInteger.valueOf(move));
        }
        return seed;
    }

    /**
     * magnifies a number from the player moves from its range to the range of the conjectures
     * @param numOfMoves
     * @param baseSeed
     * @param maxConjectures
     * @return
     */
    public static BigInteger magnifySeed(int numOfMoves, BigInteger baseSeed, BigInteger maxConjectures) {
        BigInteger maxBaseSeed = BigInteger.valueOf(3).pow(numOfMoves);
        BigInteger scalingFactor = maxConjectures.divide(maxBaseSeed);
        return baseSeed.multiply(scalingFactor).add(BigInteger.ONE);
    }
}


