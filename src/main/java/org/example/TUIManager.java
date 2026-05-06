package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TUIManager {
    private static final int NUM_OF_PEGS = 3;
    static void printState(int state, int rings)
    {
        Stack<Integer>[] pegs = new Stack[NUM_OF_PEGS];
        int currentSize = -1;
        for (int i = 1; i <= rings; i++) {
            pegs[(state%(i*3))/((rings-i)*3)].Push(i);
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < rings; j++) {
                if(rings-j<pegs[i].size())
                    currentSize = pegs[i].Pop();
                else
                    currentSize = -1;
                printRing(currentSize, rings);
                System.out.println();
            }
        }

        for (int peg = 0; peg < 3; peg++) {
            for (int i = 0; i < (rings*2-1); i++) {
                if (i == (rings*2-1) / 2) {
                    System.out.print(new String[]{"A", "B", "C"}[peg]);
                } else {
                    System.out.print(" ");
                }
            }
        }
    }

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
     * Loops L times and uses the existing getInputNextState function
     * to build the user's secret passphrase.
     */
    public static List<Integer> getPlayerHanoiInput(int r, int l) throws InterruptedException {
        List<Integer> moves = new ArrayList<>();
        int currentState = 0;

        System.out.println("--- GENERATING GEOMETRIC PASSPHRASE ---");
        System.out.println("Please make " + l + " legal moves.");

        for (int i = 0; i < l; i++) {
            printState(currentState, r);
            int newState = getInputNextState(currentState, r);
            int destPeg = -1;
            for (int ring = 0; ring < r; ring++) {
                int div = (int) Math.pow(3, ring);
                int oldPeg = (currentState / div) % 3;
                int newPeg = (newState / div) % 3;

                if (oldPeg != newPeg) {
                    destPeg = newPeg;
                    break;
                }
            }
            moves.add(destPeg);
            currentState = newState;
            System.out.println("Move recorded! (" + moves.size() + "/" + l + ")\n");
        }
        return moves;
    }

    public static int getInputNextState(int state, int rings) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose a peg to take from (A/B/C): ");
        int origin = scanner.next().toUpperCase().charAt(0) - 'A';
        System.out.print("Choose a peg to take to (A/B/C): ");
        int destination = scanner.next().toUpperCase().charAt(0) - 'A';
        int newstate = calculateNextState(state, origin, destination, rings);
        if (newstate != -1 && HanoiLogic.isLegalNeighbor(state, newstate, rings)) {
            return newstate;
        } else {
            System.out.println("WRONG INPUT! That move is illegal. Try again.");
            return getInputNextState(state, rings);
        }
    }

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
