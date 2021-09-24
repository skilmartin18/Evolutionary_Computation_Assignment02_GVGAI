package evo_exercises.Ex4_diy_GA;

import tracks.singlePlayer.tools.Heuristics.WinScoreHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import tools.ElapsedCpuTimer;
import tools.Utils;
import ontology.*;
import ontology.Types.ACTIONS;
import java.util.ArrayList;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    // var decs 
    public int population_size = 5;
    public int genotype_size = 5;
    public Random rand;
    public individual seed_individual;
    public ElapsedCpuTimer timer;
    public long remaining;
    // constructor
    public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) 
    {
        // init random number generator
        rand = new Random();

        // create initial seed individual (at first there is no previous individual so just NIL)

        individual seed_individual = new individual(stateObs,genotype_size);
        for (int i = 0; i < genotype_size; i++)
        {
            seed_individual.genotype.set(i,ACTIONS.ACTION_NIL);
        }
    }


    // creates population from stateOBS, is list of action lists, chucks in individual from previous runs
    public ArrayList<individual> create_population(StateObservation stateObs)
    {
        ArrayList<individual> population = new ArrayList<individual>();

        // add individuals to population up to popsize-1 members
        for (int i = 0; i < population_size-1; i++)
        {
            individual ind = new individual(stateObs, genotype_size);
            population.add(ind);
        }
        // last member is best individual from previous run
        population.add(seed_individual);

        return population;
    }

    // apply all actions from a genotype into a stateobs and return score
    public void calculate_fitness(StateObservation stateObs, individual _individual, WinScoreHeuristic heuristic)
    {
        StateObservation stateObsCopy = stateObs.copy();

        // apply moves
        for( int i = 0; i < genotype_size; i++)
        {
            stateObsCopy.advance(_individual.genotype.get(i));
        }

        // get score
        double score = heuristic.evaluateState(stateObsCopy);

        _individual.fitness = score;

    }

    // iterates through all individuals 
    public void calculate_population_fitness(StateObservation stateObs, ArrayList<individual> population, WinScoreHeuristic heuristic)
    {
        for ( int i = 0; i < population_size; i++)
        {
            calculate_fitness(stateObs, population.get(i), heuristic);
        }
    }

    // random index mutation
    public individual random_mutate(individual individual){

        // find number of available moves
        int num_moves = individual.available_actions;

        // random class and int generator to find which random move to choose
        Random rand = new Random();
        int rand_int1 = rand.nextInt(num_moves);

        // from random index, it searches the list of avaiable moves to specific individual and chooses one
        Types.ACTIONS rand_move = individual.actions.get(rand_int1);

        // random int to find where in genotype list to insert new move
        int rand_int2 = rand.nextInt(genotype_size);
        individual.genotype.set(rand_int2, rand_move);

        return individual;
    }

    // returns an arrary list of 2 children after parent crossover
    public ArrayList<individual> one_point_crossover(individual ind1, individual ind2){
        
        // initialising an arraylist of children to return
        ArrayList<individual> children = new ArrayList<individual>();

        // initialising a temp list of actions
        Types.ACTIONS temp;
        
        // random int to find crossover point
        Random rand = new Random();
        int rand_int = rand.nextInt(genotype_size);

        // iterates through random index to end of list and creates sublist
        for (int i = rand_int; i < genotype_size; i++){
            temp = ind1.genotype.get(i);
            

        }

        // creating new children and giving it clone lists of chosen parents
        individual child1 = new individual(ind1.genotype);
        individual child2 = new individual(ind2.genotype);

        // adding children
        children.add(child1);
        children.add(child2);

        return children;
    }

    // fitness proportionate selection WITHOUT replacement. Returns 2 individuals to be parents
    public ArrayList<ArrayList<Types.ACTIONS>> fitness_proportionate(ArrayList<ArrayList<Types.ACTIONS>> population){
        ArrayList<ArrayList<Types.ACTIONS>> parents = new ArrayList<ArrayList<Types.ACTIONS>>();

        return parents;
    }

    // elitism
    public ArrayList<individual> elitism(ArrayList<individual> population, int percentage){
        ArrayList<individual> elites = new ArrayList<individual>();

        return elites;
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
        elitism -JEFE

        remember best individual from last run:
        chuck into population -SEB

        */

        // do admin work:
        this.timer = elapsedTimer;
        long avg_time = 0;
        long time_sum = 0;
        int gen_count = 0;
        // create population
        ArrayList<individual> population = new ArrayList<individual>();
        population = create_population(stateObs);

        // evolve while we have time remaining
        remaining = timer.remainingTimeMillis();
        while(remaining > avg_time && remaining > 10)
        {
            gen_count++;

            //crossover 

            //mutation

            //select



            // check remaining time
            time_sum += timer.elapsedMillis();
            avg_time = time_sum/gen_count;
            remaining = timer.remainingTimeMillis();

        }

        return ACTIONS.ACTION_NIL;

    }


}

