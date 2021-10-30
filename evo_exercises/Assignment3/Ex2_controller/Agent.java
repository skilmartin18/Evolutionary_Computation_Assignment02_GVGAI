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
import handle_files.handle_files;

import tools.StatSummary;

/**
 * Created with IntelliJ IDEA.
 * User: ssamot
 * Date: 14/11/13
 * Time: 21:45
 * This is a Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Agent extends AbstractPlayer {

    // var decs 
    public int advance_count = 0 ;
    public int population_size = 100;
    public int genotype_size = 500;
    public Random rand;
    public individual seed_individual;
    public ElapsedCpuTimer timer;
    public long remaining;
    public int num_moves;
    public boolean once = false;
    public int test_counter = -1;
    ArrayList<individual> population;
    StateObservation stateObs;

    // keeps track of milestones and if gameState is finished
    public boolean two_hundred_thou = false;
    public boolean one_million = false;
    public boolean five_million = false;
    public boolean finished = false;

    // this will keep track of the move that ends the game in advance(). That way, we don't print unnecessary moves
    public int move_cutoff = 0; 

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
    }

    // finds a cutoff point until the last playable move (before the gamestate is finised)
    // after this point, moves in sequence are redundant
    public int find_cutoff(StateObservation stateObs, individual _individual)
    {
        StateObservation stateObsCopy = stateObs.copy();
        finished = false;
        int cutoff = 0;

        // apply moves
        for( int i = 0; (i < genotype_size && !finished); i++)
        {
            stateObsCopy.advance(_individual.genotype.get(i));

            // if game is over, then finished becomes true, which will become NOT(true) within the loop condition
            finished = stateObsCopy.isGameOver();

            /* if individual_counter == 0 (we are calculating the fitness of the 1st ind in a population), and the game is finished
            it will keep track of the index of the move that ended the game. This way, when we save the moves of the best individual
            to a string, it won't print everything single move in its genotype, only the relevant ones*/

            if (finished)
            {
                cutoff = i;
            }
        }

        return cutoff;
    }


    // apply all actions from a genotype into a stateobs and return score
    public void calculate_fitness(StateObservation stateObs, individual _individual, int individual_counter)
    {
        StateObservation stateObsCopy = stateObs.copy();
        boolean stop = false;
        finished = false;

        // apply moves
        for( int i = 0; (i < genotype_size && !finished); i++)
        {
            stateObsCopy.advance(_individual.genotype.get(i));
            advance_count++ ;

            // if game is over, then finished becomes true, which will become NOT(true) within the loop condition
            finished = stateObsCopy.isGameOver();

            /* if individual_counter == 0 (we are calculating the fitness of the 1st ind in a population), and the game is finished
            it will keep track of the index of the move that ended the game. This way, when we save the moves of the best individual
            to a string, it won't print everything single move in its genotype, only the relevant ones*/

            if (individual_counter == 0 && finished)
            {
                move_cutoff = i;
            }

            // once advance counter is greater than 5 mil, variables need to be set
            if ( advance_count > 5000000 ){
                stop = true; 
            }

            // checking if the counter for advance has reached certain values
            if (advance_count == 200000){
                two_hundred_thou = true;
            }
            if (advance_count == 1000000){
                one_million = true;
            }
            if (advance_count == 5000000){
                five_million = true;
            }
        }

        /* 
            Progress Update
        */

        // if past 5 mil don't need progress bar otherwise output updated bar
        if ( stop == false ){
            float counter = advance_count;
            float percentage = (counter/5000000)*100;
    
            System.out.print( "\rRunning Test " + test_counter + "... " + advance_count + "/" + 5000000 + " " + "(" );
            System.out.printf( "%.1f",percentage );
            System.out.print( "%" + ")" );
        }else if( stop == true && once == false ){
            float percentage = 100;

            System.out.print( "\rFinished running Test " + test_counter + " " + 5000000 + "/" + 5000000 + " " + "(" );
            System.out.printf( "%.1f",percentage );
            System.out.print( "%" + ")\n" );
            once = true;
        }
    
        // get score
        double score = stateObsCopy.getGameScore();

        _individual.fitness = score;

    }


    // iterates through all individuals 
    public void calculate_population_fitness(StateObservation stateObs, ArrayList<individual> population)
    {
        for ( int i = 0; i < population.size(); i++)
        {
            calculate_fitness(stateObs, population.get(i), i);
        }
    }

    // random index mutation
    public individual random_mutate(individual individual, double prob, int trials){

        // find number of available moves
        num_moves = individual.available_actions;

        for ( int i = 0; i < trials; i++ ){

            // random class and int generator to find which random move to choose
            int rand_int1 = rand.nextInt(num_moves);

            // from random index, it searches the list of avaiable moves to specific individual and chooses one
            Types.ACTIONS rand_move = individual.actions.get(rand_int1);

            // random int to find where in genotype list to insert new move
            int rand_int2 = rand.nextInt(genotype_size);

            // generating random number from random generator
            double prob2 = rand.nextDouble();

            // probability of mutating taken into account
            if ( prob2 <= prob ){
                individual.genotype.set(rand_int2, rand_move);
            }
        }

        return individual;
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
    public ArrayList<individual> n_point_crossover(individual ind1, individual ind2, int num){
    
        // initialising an arraylist of children to return
        ArrayList<individual> children = new ArrayList<individual>();

        // creating children clones
        individual child1 = new individual(ind1.genotype,stateObs);
        individual child2 = new individual(ind2.genotype,stateObs);

        // initialising a variable to store Types.ACTIONS
        Types.ACTIONS temp;
        
        // rand and rand_int utilised futher on
        rand = new Random();

        // generating the actual crossover points
        ArrayList<Integer> crossover_points = new ArrayList<Integer>();
        boolean acceptable = false;
        int acceptable_action_amount = 15;

        // determining the crossover points
        for (int j = 0; j < num; j++){
            int crossover_point = rand.nextInt(genotype_size);

            // first value to be added has no constraints
            if ( crossover_points.size() == 0 ){
                crossover_points.add(crossover_point);

            // every other crossover point must be 4 moves apart (allows sequences of actions to be kept in order)
            }else{

                // while loop runs until an acceptable value is obtained for a crossover point
                boolean exit = false;
                while ( exit == false ){

                    // if acceptable remains false, then crossover point will be stored in list as it is acceptable
                    for(int k = 0; k < crossover_points.size(); k ++){

                        // testing if the newly generated crossover point is at least 4 spaces away from existing points
                        int diff = Math.abs(crossover_point - crossover_points.get(k));
                        if ( diff < acceptable_action_amount ){
                            acceptable = true;
                        }
                    }

                    // if crossover point is acceptable exit while loop and store it in the array
                    if ( acceptable == false ){
                        exit = true;
                        crossover_points.add(crossover_point);

                    // if crossover point is not acceptable
                    }else{
                        crossover_point = rand.nextInt(genotype_size);
                        acceptable = false;
                    }
                }
            }
        }

        // iterates through crossover points to end of list and swaps values
        int count = 0;
        Collections.sort(crossover_points);
    
        for ( int i = 0; i < genotype_size; i++ ){

            // checks for crossover points
            if ( count <= crossover_points.size() - 1 ){
                if ( i == crossover_points.get(count) ){
                    count++;
                }
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

        individual parent1 = new individual(candidates.get(best_individual_index).genotype,candidates.get(best_individual_index).fitness);
        individual parent2 = new individual(candidates.get(second_individual_index).genotype,candidates.get(best_individual_index).fitness);

        parents.add(parent1);
        parents.add(parent2);

        return parents;
    }

    // More Elitism 
    // Returns a variable number of elites, depending on input
    public ArrayList<individual> get_elites(ArrayList<individual> population, int numElites){
        
        // initialising return list of elites
        ArrayList<individual> elites = new ArrayList<individual>();

        // Make copy of population 
        ArrayList<individual> populationCopy = new ArrayList<individual>(population);  

        // Sort population by fitness, and then reverse to get from highest -> lowest fitness
        Collections.sort(populationCopy, Comparator.comparingDouble(individual :: get_fitness));
        Collections.reverse(populationCopy);

        // finding elites based on number specified in function call
        for (int i=0; i<numElites; i++)
        {
            elites.add(populationCopy.get(i));
        }

        return elites;
    }

    public String fromACTIONS(ACTIONS move){
        String error = "";

        if (move == ACTIONS.ACTION_NIL) return "ACTION_NIL";
        else if (move == ACTIONS.ACTION_UP) return "ACTION_UP";
        else if (move == ACTIONS.ACTION_DOWN) return "ACTION_DOWN";
        else if (move == ACTIONS.ACTION_LEFT) return "ACTION_LEFT";
        else if (move == ACTIONS.ACTION_RIGHT) return "ACTION_RIGHT";
        else if (move == ACTIONS.ACTION_USE) return "ACTION_USE";
        else if (move == ACTIONS.ACTION_ESCAPE) return "ACTION_ESCAPE";

        else return error;
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

        //setting number of elites
        int numElites = 10;

        // initialising arrays to keep track of scores
        StatSummary scores200k = new StatSummary();
        StatSummary scores1mill = new StatSummary();
        StatSummary scores5mill = new StatSummary();

        // string to keep track of final text to print to fil
        String final_text = "";

        // strings that keep track of mean and std dev
        String mean200k = "";
	    String sd200k = "";
        String mean1mill= "";
	    String sd1mill = "";
        String mean5mill = "";
	    String sd5mill = "";

        for (int k = 0; k < 10; k++)
        {
            // create population
            ArrayList<individual> new_population = new ArrayList<individual>();
            // var decs
            // Types.ACTIONS action = ACTIONS.ACTION_NIL;
            double best_score = 0;
            String best_score_text = "";
            ArrayList<Types.ACTIONS> best_moves;
            String best_moves_text = "";

            // keeps track of previous scores just in case
            String previous_best_score = "";
            String previous_best_moves = "";
            double previous_best_score_double = 0;

            // text that is printed at the end of act
            String text = "";
            String index = "";
            index = k+"";

            int gen_count = 0;

            // resetting advance count and once variable
            advance_count = 0;
            once = false;
            test_counter++;

            // moving onto next level (may not actually be needed depending on how testing is done)
            if ( test_counter > 9 ){
                test_counter = 0;
            }

            // evolve while we have time remaining
            while ( advance_count < 200001 )
            {   
                int cutoff_two = 0;

                previous_best_moves = best_moves_text;
                previous_best_score = best_score_text;
                previous_best_score_double = best_score;

                best_moves_text = "";

                gen_count++;

                // crossover 
                for(int i = 0; i < (population_size-numElites)/2; i++)
                {   
                    // select parents
                    ArrayList<individual> temp = tournament_selection(population, 15);
                    ArrayList<individual> temp2 = n_point_crossover(temp.get(0), temp.get(1), 10);
                    new_population.add(temp2.get(0));
                    new_population.add(temp2.get(1));
                }

                // mutation
                for ( int i = 0; i < new_population.size(); i++ )
                {
                    // mutation is done once (can change to multiple times if need be)
                    new_population.set(i,random_mutate(new_population.get(i),0.5,40));
                }

                // select elites (should return n_Elites of population, this is set at the start of act())
                ArrayList<individual> temp3 = get_elites(population, numElites);

                // clearing old population
                population.clear();

                // fill up pop
                for ( int i = 0; i < numElites; i++ )
                {
                    population.add(temp3.get(i));
                }

                for ( int i = numElites; i < population_size; i++ )
                {
                    population.add(new_population.get(i-numElites));
                }

                // calculate fitness
                calculate_population_fitness(stateObs, new_population);

                new_population.clear();

                // gets score from best individual and converts to string
                best_score = population.get(0).fitness;

                best_score_text = best_score+"";

                // converting ACTIONS to strings (comment out if you just want to print scores for results)
                best_moves = population.get(0).genotype;
                cutoff_two = find_cutoff(stateObs, population.get(0));

                for (int i = 0; i < cutoff_two; i++)
                {
                    best_moves_text = best_moves_text + fromACTIONS(best_moves.get(i)) + ", ";
                }

                best_moves_text += fromACTIONS(best_moves.get(cutoff_two));

                // prints score and genotype of best individual at milestones
                // not necessary as Assignment only asks for final mean and std dev of scores at milestones. Can comment out if needed
                if (two_hundred_thou){
                    scores200k.add(previous_best_score_double);

                    text = "Test " + index + ":\n" + "At 200,000 advance calls:\nBest Ind Score: " + previous_best_score; // + "\nBest Ind Genotype: " + previous_best_moves;

                    two_hundred_thou = false;
                }

                if (one_million) {
                    scores1mill.add(previous_best_score_double);

                    text = text + "\n\nAt 1,000,000 advance calls:\nBest Ind Score: " + previous_best_score; // + "\nBest Ind Genotype: " + previous_best_moves;

                    one_million = false;
                }

                if (five_million){
                    scores5mill.add(previous_best_score_double);

                    text = text + "\n\nAt 5,000,000 advance calls:\nBest Ind Score: " + previous_best_score; // + "\nBest Ind Genotype: " + previous_best_moves;

                    five_million = false;
                }
            }

            final_text = final_text + "\n\n\n" + text + "\nGENERATION: "+ gen_count+"";

            // resetting population for next test (the individuals here have a randomised list of moves)
            population.clear();
            create_population(stateObs);
        }

        // calculating mean and std dev for each milestone
        mean200k += scores200k.mean();
        sd200k += scores200k.sd();

        mean1mill += scores1mill.mean();
        sd1mill += scores1mill.sd();

        mean5mill += scores5mill.mean();
        sd5mill += scores5mill.sd();

        // handle printing of "text" to assignment03/exercise02 results folder
        final_text = final_text + "\n\n\nFinal Scores:\n200k Mean: " + mean200k + " SD: " + sd200k + "\n1 Mill Mean: " 
        + mean1mill + " SD: " + sd1mill + "\n5 Mill Mean: " + mean5mill + " SD: " + sd5mill;

        handle_files.write_to_file("results/assignment03/exercise02/NewPopTest", final_text);

        /* it doesn't matter what act() returns, as it is guaranteed to time-out anyway
        (which is fine as we only care about calls to advance) */
        return ACTIONS.ACTION_NIL;
    }
}

