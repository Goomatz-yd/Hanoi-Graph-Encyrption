package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TUIManager {
    private static final int NUM_OF_PEGS = 3;

    /**
     * Prints the current state of the hanoi puzzle.
     * @param state
     * @param rings
     */
    public static void printState(int state, int rings) {
        Stack<Integer>[] pegs = new Stack[NUM_OF_PEGS];
        for (int i = 0; i < NUM_OF_PEGS; i++) {
            pegs[i] = new Stack<>();
        }

        for (int i = rings; i >= 1; i--) {
            int pegIndex = (state / (int) Math.pow(NUM_OF_PEGS, i - 1)) % NUM_OF_PEGS;
            pegs[pegIndex].Push(i);
        }


        for (int row = 0; row < rings; row++) {
            for (int peg = 0; peg < NUM_OF_PEGS; peg++) {
                int currentSize = -1;

                if (pegs[peg].size() == rings - row) {
                    currentSize = pegs[peg].Pop();
                }
                printRing(currentSize, rings);
                System.out.print("  ");
            }
            System.out.println();
        }

        for (int peg = 0; peg < 3; peg++) {
            int width = (rings * 2) - 1;
            int center = width / 2;
            for (int i = 0; i < width; i++) {
                if (i == center) System.out.print(new String[]{"A", "B", "C"}[peg]);
                else System.out.print(" ");
            }
            System.out.print("  ");
        }
        System.out.println("\n");
    }

    /**
     * prints a single ring of the hanoi puzzle.
     * @param currentSize
     * @param rings
     */
    private static void printRing(int currentSize, int rings) {
        rings = (rings * 2) - 1;

        if (currentSize == -1) {
            for (int i = 0; i < rings / 2; i++) { System.out.print(" "); }
            System.out.print("|");
            for (int i = 0; i < rings / 2; i++) { System.out.print(" "); }
            rings = 0;
        }

        for (int i = 0; i < rings; i++) {
            System.out.print(((rings / 2) - i < 0 ? -((rings / 2) - i) : ((rings / 2) - i)) < currentSize ? "X" : " ");
        }
    }

    /**
     * Gets the input from the player for l moves
     * @param r
     * @param l
     * @return
     * @throws InterruptedException
     */
    public static List<Integer> getPlayerHanoiInput(int r, int l) throws InterruptedException {
        List<Integer> moves = new ArrayList<>();
        int currentState = 0;

        System.out.println("make " + l + " legal moves.");

        for (int i = 0; i < l; i++) {
            printState(currentState, r);
            int newState = getInputNextState(currentState, r);
            int destPeg = -1;
            for (int ring = 0; ring < r; ring++) {
                int div = (int) Math.pow(NUM_OF_PEGS, ring);
                int oldPeg = (currentState / div) % 3;
                int newPeg = (newState / div) % 3;

                if (oldPeg != newPeg) {
                    destPeg = newPeg;
                    break;
                }
            }
            moves.add(destPeg);
            currentState = newState;
            System.out.println("move successfully preformed (" + moves.size() + "/" + l + ")\n");
        }
        return moves;
    }

    /**
     * checks if the input is a legal move and returns the new state
     * @param state
     * @param rings
     * @return
     * @throws InterruptedException - Scanner necessity in static functions
     */
    public static int getInputNextState(int state, int rings) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("pick a peg to take from (A/B/C): ");
        int origin = scanner.next().toUpperCase().charAt(0) - 'A';
        System.out.print("pick the peg to place the ring on (A/B/C): ");
        int destination = scanner.next().toUpperCase().charAt(0) - 'A';
        int newstate = calculateNextState(state, origin, destination, rings);
        if (newstate != -1 && HanoiLogic.isLegalNeighbor(state, newstate, rings)) {
            return newstate;
        } else {
            System.out.println("illegal move\ntry again.");
            return getInputNextState(state, rings);
        }
    }

    /**
     * calculates if a move is legal and returns the new state
     * @param currentState
     * @param origin
     * @param dest
     * @param r
     * @return
     */
    private static int calculateNextState(int currentState, int origin, int dest, int r) {
        int[] neighbors = HanoiLogic.getLegalNeighbors(currentState, r);
        for (int n : neighbors) {
            for (int i = 0; i < r; i++) {
                int div = (int) Math.pow(3, i);
                if ((currentState / div) % 3 == origin && (n / div) % 3 == dest) {
                    return n;
                }
            }
        }
        return -1;
    }
}
