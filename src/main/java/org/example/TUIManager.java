package org.example;

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

    public int getInputNextState(int state, int rings) throws InterruptedException {
        Scanner Scanner = new Scanner(System.in);
        int origin;
        int destination;
        int newstate = -1;
        System.out.println("choose a peg to take from(A/B/C):");
        origin = Scanner.next().charAt(0) - 'A';
        System.out.println("choose a peg to take to(A/B/C)):");
        destination = Scanner.next().charAt(0) - 'A';
        newstate = HanoiLogic.isLegalNeighbor(state, (origin*3+destination), rings)? (origin*3+destination) : -1;
        if(newstate == -1){
            printWrong(origin, rings);
            getInputNextState(state,rings);
        }
        Scanner.close();
        return newstate;

    }
    private static void printWrong(int returnstate, int rings) throws InterruptedException {
        System.out.println("wrong input");
        Thread.sleep(3000);
        printState(returnstate, rings);
    }
}
