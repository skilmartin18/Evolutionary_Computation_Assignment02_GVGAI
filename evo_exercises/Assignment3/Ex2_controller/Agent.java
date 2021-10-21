package evo_exercises.Assignment3.Ex2_controller;

import tracks.singlePlayer.tools.Heuristics.SimplestHeuristic;
import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
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
    public int population_size = 6;
    public int genotype_size = 6;
    public Random rand;
    public individual seed_individual;
    public ElapsedCpuTimer timer;
    public long remaining;
    public int num_moves;
    ArrayList<individual> population;
    StateObservation stateObs;
    // constructor
    public Agent(StateObservation _stateObs, ElapsedCpuTimer elapsedTimer) 
    {
        stateObs = _stateObs;
        // init random number generator
        rand = new Random();

        population = new ArrayList<individual>();
        create_population(_stateObs);

        // create initial seed individual (at first there is no previous individual so just NIL)
        individual seed_individual = new individual(stateObs,genotype_size);
        for (int i = 0; i < genotype_size; i++)
        {
            seed_individual.genotype.set(i,ACTIONS.ACTION_NIL);
        }


    }


    // creates population from stateOBS, is list of action lists, chucks in individual from previous runs
    public void create_population(StateObservation stateObs)
    {
        // add individuals to population up to popsize-1 members
        for (int i = 0; i < population_size; i++)
        {
            individual ind = new individual(stateObs, genotype_size);
            this.population.add(ind);
        }
        // last member is best individual from previous run
        //population.add(seed_individual);
    }

    // apply all actions from a genotype into a stateobs and return score
    public void calculate_fitness(StateObservation stateObs, individual _individual, SimplestHeuristic heuristic)
    {
        StateObservation stateObsCopy = stateObs.copy();

        // apply moves
        for( int i = 0; i < genotype_size; i++)
        {
            stateObsCopy.advance(_individual.genotype.get(i));
        }

        // get score
        double score = heuristic.evaluateState(stateObsCopy, stateObs);

        _individual.fitness = score;

    }

    // find first move of best fitness individual
    public Types.ACTIONS first_move(ArrayList<individual> population){
        Types.ACTIONS move;

        int best_individual_index = 0;
        double best_fitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < population_size; i++){
            if ((population.get(i)).fitness >= best_fitness){
                best_fitness = (population.get(i)).fitness;
                best_individual_index = i;
            }
        }

        move = (population.get(best_individual_index)).genotype.get(0);

        return move;
    }

    // iterates through all individuals 
    public void calculate_population_fitness(StateObservation stateObs, ArrayList<individual> population, SimplestHeuristic heuristic)
    {
        for ( int i = 0; i < population.size(); i++)
        {
            calculate_fitness(stateObs, population.get(i), heuristic);
        }
    }

    // random index mutation
    public individual random_mutate(individual individual){

        // find number of available moves
        num_moves = individual.available_actions;

        // random class and int generator to find which random move to choose
        int rand_int1 = rand.nextInt(num_moves);

        // from random index, it searches the list of avaiable moves to specific individual and chooses one
        Types.ACTIONS rand_move = individual.actions.get(rand_int1);

        // random int to find where in genotype list to insert new move
        int rand_int2 = rand.nextInt(genotype_size);
        individual.genotype.set(rand_int2, rand_move);

        return individual;
    }

    //mutate MORE
    public individual mutate_three_genes(individual individual){

        // find number of available moves
        num_moves = individual.available_actions;

        // random class and int generator to find which random move to choose
        int rand_int1 = rand.nextInt(num_moves);
        int rand_int2 = rand.nextInt(num_moves);
        int rand_int3 = rand.nextInt(num_moves);

        Types.ACTIONS rand_move1 = individual.actions.get(rand_int1);
        Types.ACTIONS rand_move2 = individual.actions.get(rand_int2);
        Types.ACTIONS rand_move3 = individual.actions.get(rand_int3);

        int rand_int4 = rand.nextInt(genotype_size);
        individual.genotype.set(rand_int4, rand_move1);
        int rand_int5 = rand.nextInt(genotype_size);
        individual.genotype.set(rand_int5, rand_move2);
        int rand_int6 = rand.nextInt(genotype_size);
        individual.genotype.set(rand_int6, rand_move3);

        return individual;
    }

    public void remove_pop_first_action()
    {
        for( int i = 0; i < population_size; i++)
        {
            // random class and int generator to find which random move to choose
            int rand_int1 = rand.nextInt(num_moves);
            // from random index, it searches the list of avaiable moves to specific individual and chooses one
            Types.ACTIONS rand_move = population.get(i).actions.get(rand_int1);

            // this should pop the first element
            population.get(i).genotype.remove(0);
            // add a new random element to the end
            population.get(i).genotype.add(rand_move);
        }
    }

    // returns an arrary list of 2 children after parent crossover
    public ArrayList<individual> one_point_crossover(individual ind1, individual ind2){
        
        // initialising an arraylist of children to return
        ArrayList<individual> children = new ArrayList<individual>();

        // creating children clones
        individual child1 = new individual(ind1.genotype,stateObs);
        individual child2 = new individual(ind2.genotype,stateObs);

        // initialising a variable to store Types.ACTIONS
        Types.ACTIONS temp;
        
        // random int to find crossover point
        rand = new Random();
        int rand_int = rand.nextInt(genotype_size);

        // iterates through random index to end of list and swaps values
        for (int i = rand_int; i < genotype_size; i++){
            temp = child1.genotype.get(i);
            child1.genotype.set(i, child2.genotype.get(i));
            child2.genotype.set(i, temp);
        }

        // adding children
        children.add(child1);
        children.add(child2);

        return children;
    }

    // returns an arrary list of 2 children after parent crossover
    public ArrayList<individual> n_point_crossover(individual ind1, individual ind2){
    
        // initialising an arraylist of children to return
        ArrayList<individual> children = new ArrayList<individual>();

        // creating children clones
        individual child1 = new individual(ind1.genotype,stateObs);
        individual child2 = new individual(ind2.genotype,stateObs);

        // initialising a variable to store Types.ACTIONS
        Types.ACTIONS temp;
        
        // random int to find how many crossover points there will be
        rand = new Random();
        int rand_int = rand.nextInt(genotype_size);

        // make sure number of crossover points is acceptable (will likely have to be changed)
        while ( rand_int < 3 && rand_int > 10 ){
            rand_int = rand.nextInt(genotype_size);
        }

        // generating the actual crossover points
        int crossover_points[] = {};
        boolean acceptable = false;

        // determining the crossover points
        for (int j = 0; j < rand_int; j++){
            int crossover_point = rand.nextInt(genotype_size);

            // first value to be added has no constraints
            if ( crossover_points.length == 0 ){
                crossover_points[j] = crossover_point;

            // every crossover point must be 5 moves apart
            }else{

                // while loop runs until an acceptable value is obtained for a crossover point
                boolean exit = false;
                while ( exit == false ){

                    // if acceptable remains false, then crossover point will be stored in array
                    for(int k = 0; k < crossover_points.length; k ++){

                        // testing if the newly generated crossover point is at least 5 moves away from existing points
                        int diff = Math.abs(crossover_point - crossover_points[k]);
                        if ( diff < 5 ){
                            acceptable = true;
                        }
                    }

                    // if crossover point is acceptable exit while loop and store it in the array
                    if ( acceptable == false ){
                        exit = true;
                        crossover_points[j] = crossover_point;

                    // if crossover point is not acceptable, 
                    }else{
                        crossover_point = rand.nextInt(genotype_size);
                        acceptable = false;
                    }
                }
            }
        }

        // iterates through crossover points to end of list and swaps values
        int count = 0;
        Arrays.sort(crossover_points);
        for ( int i = 0; i < genotype_size; i++ ){

            // checks for crossover points
            if ( i == crossover_points[count] ){
                count++;
            }

            // performs crossover if necessary
            if ( count % 2 == 1 ){
                temp = child1.genotype.get(i);
                child1.genotype.set(i, child2.genotype.get(i));
                child2.genotype.set(i, temp);  
            }
        }

        // adding children
        children.add(child1);
        children.add(child2);

        return children;
    }

    // tournament selection WITHOUT replacement. Returns 2 individuals to be parents. k = tournament size
    public ArrayList<individual> tournament_selection(ArrayList<individual> population, int k){
       
        // initialising arraylist of 2 parents to return
        ArrayList<individual> parents = new ArrayList<individual>();

        // initialising arraylist of candidate indices and candidates
        List<Integer> indices = new ArrayList<Integer>();
        ArrayList<individual> candidates = new ArrayList<individual>();
        
        // fills up a list of indices which is then shuffled, then the first k indices are taken (this prevents duplicates)
        for (int i = 0; i < population_size; i++){
            indices.add(i);
        }

        Collections.shuffle(indices);

        // selecting chosen random candidates from population
        for (int i = 0; i < k; i++){
            candidates.add(population.get(indices.get(i)));
        }

        // finding best and second best individuals from candidates
        int best_individual_index = 0;
        int second_individual_index = 0;
        double best_fitness = Double.NEGATIVE_INFINITY;
        double second_best_fitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < k; i++){
            if ((candidates.get(i)).fitness >= best_fitness){
                second_best_fitness = best_fitness;
                best_fitness = (candidates.get(i)).fitness;
                best_individual_index = i;
                
            } else if ((candidates.get(i)).fitness > second_best_fitness){
                second_best_fitness = (candidates.get(i)).fitness;
                second_individual_index = i;
            }
        }

        // adding best and second best individuals to return list
        parents.add(candidates.get(best_individual_index));
        parents.add(candidates.get(second_individual_index));

        return parents;
    }

    // elitism (will only return 2 elites for now). Effectively the same as tournament selection
    public ArrayList<individual> return_two_elites(ArrayList<individual> population){
        
        // initialising return list of elites
        ArrayList<individual> elites = new ArrayList<individual>();

        // finding best and second best individuals from all in population
        int best_individual_index = 0;
        int second_individual_index = 0;
        double best_fitness = Double.NEGATIVE_INFINITY;
        double second_best_fitness = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < population_size; i++){
            if ((population.get(i)).fitness >= best_fitness){
                second_best_fitness = best_fitness;
                best_fitness = (population.get(i)).fitness;
                best_individual_index = i;
                
            } else if ((population.get(i)).fitness > second_best_fitness){
                second_best_fitness = (population.get(i)).fitness;
                second_individual_index = i;
            }
        }

        // adding best and second best individuals to return list
        elites.add(population.get(best_individual_index));
        elites.add(population.get(second_individual_index));

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

        // do admin work:
        this.timer = elapsedTimer;
        SimplestHeuristic heuristic = new SimplestHeuristic(stateObs);
        long avg_time = 0;
        long time_sum = 0;
        int gen_count = 0;
        // create population
        ArrayList<individual> new_population = new ArrayList<individual>();
        // var decs
        Types.ACTIONS action = ACTIONS.ACTION_NIL;
        
        // evolve while we have time remaining
        remaining = timer.remainingTimeMillis();
        while((remaining > avg_time) && (remaining > 20))
        {
            gen_count++;

            //crossover 
            for(int i = 0; i < (population_size-2)/2; i++)
            {
                //select parents
                ArrayList<individual> temp = tournament_selection(population, 3);
                ArrayList<individual> temp2 = one_point_crossover(temp.get(0), temp.get(1));
                new_population.add(temp2.get(0));
                new_population.add(temp2.get(1));
            }

            if(population_size%2 == 1)
            {
                ArrayList<individual> temp = tournament_selection(population, 3);
                ArrayList<individual> temp2 = one_point_crossover(temp.get(0), temp.get(1));
                new_population.add(temp2.get(0));
            }

            // mutation
            for(int i = 0; i < new_population.size(); i++)
            {
                new_population.set(i,mutate_three_genes(new_population.get(i)));
            }

            // select elites
            ArrayList<individual> temp3 = return_two_elites(population);

            
            // calculate fitness
            calculate_population_fitness(stateObs, temp3, heuristic);
            calculate_population_fitness(stateObs, new_population, heuristic);

            // fill up pop
            population.set(0,temp3.get(0));
            population.set(1,temp3.get(1));

            for(int i =2; i<population_size;i++)
            {
                population.set(i,new_population.get(i-2));
            }

            // check remaining time
            time_sum += timer.elapsedMillis();
            avg_time = time_sum/gen_count;
            remaining = timer.remainingTimeMillis();

        }

        action = first_move(population);
        remove_pop_first_action();
        // maybe helps with dying due to un-searched actions
        // StateObservation stcopy = stateObs.copy();
        // stcopy.advance(action);

        // if(stcopy.isGameOver())
        // {
        //     // random class and int generator to find which random move to choose
        //     int rand_int1 = rand.nextInt(num_moves);
        //     // from random index, it searches the list of avaiable moves to specific individual and chooses one
        //     action = stateObs.getAvailableActions().get(rand_int1);

        // }
        // System.out.println(gen_count);
        return action;

    }


}

