package tracks.singlePlayer.tools.Heuristics;

import core.game.StateObservation;
import ontology.Types;
import java.util.ArrayList;
import java.util.HashMap;

import core.game.Observation;
import core.game.StateObservation;
import ontology.Types;
import tools.Vector2d;

public class SimplestHeuristic {
    

    public SimplestHeuristic(StateObservation stateObs) {

    }

    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();
        double score = rawScore; 
        double closestnpc = Double.POSITIVE_INFINITY;

        // calculate closest npc, 1/(distance/100) is added to score
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(stateObs.getAvatarPosition());
        if (npcPositions.length > 0)
            closestnpc = npcPositions[0].get(0).sqDist/stateObs.getBlockSize();
        score += 1/(closestnpc/100);

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return -1000000;


        return score;
    }
}
