package evo_exercises.Ex4_diy_GA;

import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import tools.ElapsedCpuTimer;
import tools.Utils;
import ontology.*;
import ontology.Types.ACTIONS;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    // var decs 
    public double epsilon = 1e-6;
    public Random m_rnd;

    // constructor
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

    }

    /**
     *
     * Very simple diy GA
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {

        /*

        Make a GA: 

        genotype: list of possible actions-> variable genotype length-> 5:  {NIL,LEFT,RIGHT,NIL,UP} -SEB
        fitness: SimpleStateHeuristic evaluation score- SEB
        population size variable:-> population -SEB

        variation operators:
        mutation-> randomly replace action with different available action- JEFE
        crossover-> ordered crossover- JEFE

        selection:
        fitness based -JEFE
        elitism -SEB/JEFE

        remember best individual from last run:
        chuck into population -SEB

        */








        return ACTIONS.ACTION_NIL;

    }


}

