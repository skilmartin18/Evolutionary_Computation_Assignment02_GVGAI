package tracks.singlePlayer.tools.Heuristics;

import core.game.StateObservation;
import ontology.Types;

public class SimplestHeuristic {
    

    public SimplestHeuristic(StateObservation stateObs) {

    }

    public double evaluateState(StateObservation stateObs) {
        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();
        stateObs.getNPCPositions();
        
        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return -1000000;


        return rawScore;
    }
}
