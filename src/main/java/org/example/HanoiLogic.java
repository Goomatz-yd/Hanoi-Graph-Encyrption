package org.example;

public class HanoiLogic {
    public static int rings = 5;

    public static char isLegalNeighbor(int currentState, int newState) {
        //check if the new state isn't already the current one - no move
        if (currentState == newState) {
            return 0;
        }

        int changedRing = -1;
        int sourceRod = -1;
        int destRod = -1;

        int curr = currentState;
        int next = newState;

        //parse the state into logic
        for (int i = 0; i < rings; i++) {
            int currentRod = curr % 3; 
            int targetRod = next % 3; 

            if (currentRod != targetRod) {
                //if there is a required change in the middle, move is illegal
                if (changedRing != -1) {
                    return 0;
                }
                changedRing = i;
                sourceRod = currentRod;
                destRod = targetRod;
            }

            //check the next ring
            curr /= 3;
            next /= 3;
        }


        //check all rings are up to the rules
        curr = currentState;
        for (int i = 0; i < changedRing; i++) {
            int smallerRingRod = curr % 3;
            if (smallerRingRod == sourceRod || smallerRingRod == destRod) {
                return 0;
            }
            curr /= 3;
        }
        return 1;
    }
}
