package org.example;

import java.util.ArrayList;
import java.util.List;

public class HanoiLogic {

    /**
     * checks if a state transition is legal
     * @param currentState
     * @param newState
     * @param rings
     * @return
     */
    public static Boolean isLegalNeighbor(int currentState, int newState, int rings) {
        if (currentState == newState) {
            return false;
        }

        int changedRing = -1;
        int sourceRod = -1;
        int destRod = -1;

        int curr = currentState;
        int next = newState;

        for (int i = 0; i < rings; i++) {
            int currentRod = curr % 3; 
            int targetRod = next % 3; 

            if (currentRod != targetRod) {
                if (changedRing != -1) {
                    return false;
                }
                changedRing = i;
                sourceRod = currentRod;
                destRod = targetRod;
            }

            curr /= 3;
            next /= 3;
        }


        curr = currentState;
        for (int i = 0; i < changedRing; i++) {
            int smallerRingRod = curr % 3;
            if (smallerRingRod == sourceRod || smallerRingRod == destRod) {
                return false;
            }
            curr /= 3;
        }
        return true;
    }

    /**
     * gets all legal neighbors of the current state without creating a graph
     * @param currentState
     * @param rings
     * @return
     */
    public static int[] getLegalNeighbors(int currentState, int rings) {
        List<Integer> validNeighbors = new ArrayList<>();
        
        int maxState = (int) Math.pow(3, rings);

        for (int ringToMove = 0; ringToMove < rings; ringToMove++) {
            int divisor = (int) Math.pow(3, ringToMove);
            int currentRod = (currentState / divisor) % 3;

            for (int targetRod = 0; targetRod < 3; targetRod++) {
                if (currentRod != targetRod) {
                    int difference = (targetRod - currentRod) * divisor;
                    int hypotheticalState = currentState + difference;

                    if (hypotheticalState >= 0 && hypotheticalState < maxState) {
                        if (isLegalNeighbor(currentState, hypotheticalState, rings)) {
                            validNeighbors.add(hypotheticalState);
                        }
                    }
                }
            }
        }
        int[] result = validNeighbors.stream().mapToInt(i -> i).toArray();
        java.util.Arrays.sort(result);
        return result;
    }
}
