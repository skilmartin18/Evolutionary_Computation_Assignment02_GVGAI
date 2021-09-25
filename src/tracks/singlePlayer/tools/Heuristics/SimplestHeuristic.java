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

    public double evaluateState(StateObservation stateObs, StateObservation previousObs) {

        boolean gameOver = stateObs.isGameOver();
        Types.WINNER win = stateObs.getGameWinner();
        double rawScore = stateObs.getGameScore();
        double score = rawScore; 
        double closestResource = Double.POSITIVE_INFINITY;
        double closestNPC = Double.POSITIVE_INFINITY;
        double closestImmovable = Double.POSITIVE_INFINITY;

        // calculate closest npc or resource, 1/(distance/100) is added to score
        // plan to allow this to take different parameters depending on game, may not be possible
        ArrayList<Observation>[] npcPositions = stateObs.getNPCPositions(stateObs.getAvatarPosition());
        ArrayList<Observation>[] resourcePositions = stateObs.getResourcesPositions(stateObs.getAvatarPosition());
        ArrayList<Observation>[] immovablePositions = stateObs.getImmovablePositions(stateObs.getAvatarPosition());
        Vector2d originalPos = previousObs.getAvatarPosition();
        Vector2d currentPos = stateObs.getAvatarPosition();
        boolean isnpc = false;
        boolean isresource = false;

        if(!gameOver)
        {
            if ((resourcePositions != null) && (resourcePositions[0].size()>0))
            {
                closestResource = resourcePositions[0].get(0).sqDist/stateObs.getBlockSize();
                score += 1/(closestResource/20);
                isresource = true;
            } 
            else if (npcPositions != null)
            {
                closestNPC = npcPositions[0].get(0).sqDist/stateObs.getBlockSize();
                score += Math.ceil(1/(closestNPC/50));
                isnpc = true;
            }

            // if((immovablePositions != null) && (immovablePositions[0].size()>0))
            // {
            //     closestImmovable = npcPositions[0].get(0).sqDist/stateObs.getBlockSize();

            //     if(isresource)
            //     {
            //         if (closestImmovable < closestResource)
            //         {
            //             score -= (1/(closestResource/20))/2;
            //         }
            //     }

            //     if(isnpc)
            //     {
            //         if (closestImmovable < closestNPC)
            //         {
            //             score -= Math.ceil(1/(closestNPC/50))/2;
            //         }
            //     }
            // }

        }

        // if(gameOver && (stateObs.getGameTick()<1400))
        //     return -1000000;

        if (currentPos == originalPos)
            return 0;

        if(gameOver && win == Types.WINNER.PLAYER_LOSES)
            return 0;
        // if(gameOver)
        // {
        //     return -1000000;
        // }

        return score;
    }
}
