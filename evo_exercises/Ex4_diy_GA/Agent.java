package evo_exercises.Ex4_diy_GA;

import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import tools.ElapsedCpuTimer;
import tools.Utils;
import ontology.*;
import ontology.Types.ACTIONS;
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

    // var decs 
    public double epsilon = 1e-6;
    public Random m_rnd;
    public int population_size = 5;
    public int genotype_size = 5;
    public Random rand = new Random();
    // constructor
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) 
    {

    }

    public ArrayList<Types.ACTIONS> create_individual(StateObservation stateObs)
    {
        ArrayList<Types.ACTIONS> actions = stateObs.getAvailableActions();
        int available_actions = actions.size();
        ArrayList<Types.ACTIONS> individual = new ArrayList<Types.ACTIONS>();

        for(int i = 0; i < genotype_size; i++)
        {
            individual.add( actions.get(rand.nextInt(available_actions)) );
        }

        return individual;
    }

    public ArrayList<ArrayList<Types.ACTIONS>> create_population(StateObservation stateObs)
    {
        ArrayList<ArrayList<Types.ACTIONS>> population = new ArrayList<ArrayList<Types.ACTIONS>>();
        for (int i = 0; i < population_size; i++)
        {
            population.add(create_individual(stateObs));
        }

        return population;
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

        //
        
        
        
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

