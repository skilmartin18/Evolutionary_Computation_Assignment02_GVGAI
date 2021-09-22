package tracks.singlePlayer.diy;

import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class multiStepLookAgent extends AbstractPlayer {

    public double epsilon = 1e-6;
    public Random m_rnd;

    public multiStepLookAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        m_rnd = new Random();


    }

    /**
     *
     * Very simple one step lookahead agent.
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS[] act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        Types.ACTIONS[] bestActions = null;
        //Types.ACTIONS bestAction2 = null;

        double maxQ = Double.NEGATIVE_INFINITY;
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);

        // Loop through current possible actions
        for (Types.ACTIONS firstAction : stateObs.getAvailableActions()) {

            // Copy state of first action
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(firstAction);

            SimpleStateHeuristic heuristic2 =  new SimpleStateHeuristic(stCopy);

            // For each first action, loop through the possible second actions
            for (Types.ACTIONS secondAction : stCopy.getAvailableActions()) {
                
                StateObservation stCopy2 = stCopy.copy();
                stCopy2.advance(secondAction);
                double Q = heuristic2.evaluateState(stCopy2);
                Q = Utils.noise(Q, this.epsilon, this.m_rnd.nextDouble());

                //System.out.println("Action:" + action + " score:" + Q);
                if (Q > maxQ) {
                    maxQ = Q;
                    bestActions[0] = firstAction;
                    bestActions[1] = secondAction;
                }
            }
        }

        //System.out.println("======== "  + maxQ + " " + bestAction + "============");
        return bestActions;

    }


}
