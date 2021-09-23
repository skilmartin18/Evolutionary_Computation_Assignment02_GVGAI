package tracks.singlePlayer.diy.multiStepLookAhead;

import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    public double epsilon = 1e-6;
    public Random m_rnd;

    // Added in timer
    private ElapsedCpuTimer timer;
    private long remaining;

    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

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
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        boolean _break = false;
        this.timer = elapsedTimer;
        Types.ACTIONS bestFirstAction[] = {ACTIONS.ACTION_NIL, ACTIONS.ACTION_NIL, ACTIONS.ACTION_NIL};
        Types.ACTIONS bestSecondAction[] = {ACTIONS.ACTION_NIL, ACTIONS.ACTION_NIL, ACTIONS.ACTION_NIL};
        double maxQ = Double.NEGATIVE_INFINITY;
        double Q = 0;
        SimpleStateHeuristic heuristic =  new SimpleStateHeuristic(stateObs);

        // Reversing first actions array
        ArrayList<Types.ACTIONS> firstAvailableActions = stateObs.getAvailableActions();
        ArrayList<Types.ACTIONS> reversedFirstAvailableActions = new ArrayList<Types.ACTIONS>();

        for (int j = firstAvailableActions.size()-1; j > -1; j--){
            reversedFirstAvailableActions.add(firstAvailableActions.get(j));
        }

        // Loop through current possible actions
        for (Types.ACTIONS firstAction : reversedFirstAvailableActions ){
            
            if ( _break ){
                break;
            }

            // Copy state of first action
            StateObservation stCopy = stateObs.copy();
            stCopy.advance(firstAction);
            double Q1 = heuristic.evaluateState(stCopy);
            Q1 = Utils.noise(Q1, this.epsilon, this.m_rnd.nextDouble());
    
            // For each first action, loop through the possible second actions
            for (Types.ACTIONS secondAction : stCopy.getAvailableActions()) {

                // If time remaining is less than ...ms, then exit both for loops
                remaining = timer.remainingTimeMillis();
                if ( remaining < 15 ){
                    _break = true;
                    break;
                }
                
                StateObservation stCopy2 = stCopy.copy();
                stCopy2.advance(secondAction);
                double Q2 = heuristic.evaluateState(stCopy2);
                Q2 = Utils.noise(Q2, this.epsilon, this.m_rnd.nextDouble());

                Q = Q1 + Q2;

                // Recording three best first and second actions to take
                if (Q > maxQ) {

                    maxQ = Q;
                    bestFirstAction[2] = bestFirstAction[1];
                    bestFirstAction[1] = bestFirstAction[0];
                    bestFirstAction[0] = firstAction;

                    bestSecondAction[2] = bestSecondAction[1];
                    bestSecondAction[1] = bestSecondAction[0];
                    bestSecondAction[0] = secondAction;
                }

            }
        }


        //Setting bestAction just incase not enough time
        Types.ACTIONS bestAction = bestFirstAction[0];
        maxQ = Double.NEGATIVE_INFINITY;

        // If time permits look futher ahead
        if ( !_break ){

            _break = false;

            // Running through the three best actions recorded for first and second actions
            for ( int i = 0; i < 3; i++){

                if ( _break ){
                    break;
                }

                // Copy state of first action and advance
                StateObservation stCopy3 = stateObs.copy();
                stCopy3.advance(bestFirstAction[i]);
                stCopy3.advance(bestSecondAction[i]);

                // For loop runs through third actions to take in conjunction with previous two actions
                for ( Types.ACTIONS thirdAction : stCopy3.getAvailableActions() ){
                    
                    // If time remaining is less than 15ms, then exit both for loops
                    remaining = timer.remainingTimeMillis();
                    if ( remaining < 15 ){
                        _break = true;
                        break;
                    }
                    
                    StateObservation stCopy4 = stCopy3.copy();
                    stCopy4.advance(thirdAction);
                    Q = heuristic.evaluateState(stCopy4);
                    Q = Utils.noise(Q, this.epsilon, this.m_rnd.nextDouble());

                    if (Q > maxQ) {
                        maxQ = Q;
                        bestAction = bestFirstAction[i];
                    }
                }
            }
        }

        return bestAction;

    }

}
