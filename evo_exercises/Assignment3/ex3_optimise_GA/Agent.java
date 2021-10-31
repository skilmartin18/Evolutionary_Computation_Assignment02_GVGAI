package evo_exercises.Assignment3.ex3_optimise_GA;

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
import tools.Vector2d;

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
    public int population_size = 80;
    public int genotype_size = 200;
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
    //(May not be needed)
    public int find_cutoff(StateObservation stateObs, individual _individual)
    {
        StateObservation stateObsCopy = stateObs.copy();
        boolean finished = false;
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
    public void calculate_fitness(StateObservation stateObs, individual _individual)
    {
        StateObservation stateObsCopy = stateObs.copy();
        boolean stop = false;
        boolean finished = false;
        int sequence_length = 0;

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

            if (finished || i == genotype_size-1)
            {
                sequence_length = i+1;
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
        _individual.sequence_fitness = sequence_length;
    }

    // calculates initial Agent individual fitnesses (without advance counter)
    public void calculate_initial_fitness(StateObservation stateObs, individual _individual)
    {   
        StateObservation stateObsCopy = stateObs.copy();
        boolean finished = false;
        int sequence_length = 0;

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

            if (finished || i == genotype_size-1)
            {
                sequence_length = i+1;
            }
        }

        // get score
        double score = stateObsCopy.getGameScore();

        _individual.fitness = score;
        _individual.sequence_fitness = sequence_length;
    }

    // iterates through all individuals 
    public void calculate_population_fitness(StateObservation stateObs, ArrayList<individual> population)
    {
        for ( int i = 0; i < population.size(); i++)
        {
            calculate_fitness(stateObs, population.get(i));
        }
    }

    // iterates through all initial individuals (the ones created by Agent constructor
    public void calculate_initial_population_fitness(StateObservation stateObs, ArrayList<individual> population)
    {
        for ( int i = 0; i < population.size(); i++)
        {
            calculate_initial_fitness(stateObs, population.get(i));
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
        int acceptable_action_amount = 7;

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

    /* 


        CODE FOR NSGA
    

    */
        // Normalises fitness values to make sure there is not a bias towards one objective
    public void normaliseFitnesses(ArrayList<individual> population)
    {
        // Find max fitness values in population
        double max_fitness = 0.0001; 
        double max_sequence_fitness = 0;
        for (int i=0; i<population.size(); i++)
        {
            if (population.get(i).fitness > max_fitness)
            {
                max_fitness = population.get(i).fitness;
            }
            if (population.get(i).sequence_fitness > max_sequence_fitness)
            {
                max_sequence_fitness = population.get(i).sequence_fitness;
            }
        }

        // Modify normalised fitness values
        for (int i=0; i<population.size(); i++)
        {
            population.get(i).normalised_fitness = population.get(i).fitness / max_fitness;
            population.get(i).normalised_sequence_fitness = (max_sequence_fitness - population.get(i).sequence_fitness)/(max_sequence_fitness);
        }
    }

    // Calculates the crowding distances for all individuals in a rank
    public void calcCrowdingDistance(ArrayList<individual> rank)
    {
        // Deep copy the rank
        // ArrayList<individual> rankCopy = new ArrayList<individual>();
        // System.arraycopy(rank, 0, rankCopy, 0, rank.size()); 

        // Order the rank based on sequence fitness
        Collections.sort(rank, Comparator.comparingDouble(individual :: get_normalised_sequence_fitness));

        // Loop through the rank (the front)
        for (int i=0; i<rank.size(); i++)
        {
            // If we are at first or last individual, assign +inf. crowding distance
            if ( i==0 || i==rank.size()-1 )
            {
                rank.get(i).sequence_distance = Double.POSITIVE_INFINITY; 
            }
            // All other cases...
            else
            {
                // Get references to left and right individuals
                individual sequence_left = rank.get(i-1);
                individual sequence_right = rank.get(i+1);

                // Get current individual sequence distance
                rank.get(i).sequence_distance = (sequence_right.normalised_sequence_fitness-sequence_left.normalised_sequence_fitness)/
                (rank.get(rank.size()-1).normalised_sequence_fitness-rank.get(0).normalised_sequence_fitness);

                // Get vector positions of the three individuals, based upon 2 fitness vals
                // Vector2d currentPos = new tools.Vector2d(current.normalised_fitness, current.normalised_sequence_fitness); 
                // Vector2d leftPos = new tools.Vector2d(left.normalised_fitness, left.normalised_sequence_fitness); 
                // Vector2d rightPos = new tools.Vector2d(right.normalised_fitness, right.normalised_sequence_fitness); 

                // Calc distance from current to left and right vecs
                // double leftDistance = currentPos.dist(leftPos); 
                // double rightDistance = currentPos.dist(rightPos); 

                // Crowding distance is the total of these two values
                //rank.get(i).crowdingDistance = (leftDistance + rightDistance); 
            }
        }

        Collections.sort(rank, Comparator.comparingDouble(individual :: get_normalised_fitness));
        // Loop through the rank (the front)
        for (int j=0; j<rank.size(); j++)
        {
                // If we are at first or last individual, assign +inf. crowding distance
            if ( j==0 || j==rank.size()-1 )
            {
                rank.get(j).score_distance= Double.POSITIVE_INFINITY; 
            }
            else
            {
                // Get references to left and right individuals
                individual score_left = rank.get(j-1);
                individual score_right = rank.get(j+1);

                // Get current individual sequence distance
                rank.get(j).score_distance = (score_right.normalised_fitness-score_left.normalised_fitness)/
                (rank.get(rank.size()-1).normalised_fitness-rank.get(0).normalised_fitness);
            }

            // Calculate crowding distance
            rank.get(j).crowdingDistance = rank.get(j).sequence_distance + rank.get(j).score_distance ;
        } 
    }

    // Check if an individual is not dominated by any other solutions in population
    public boolean notDominated(individual indA, ArrayList<individual> toBeRanked)
    {
        for (individual indB : toBeRanked)
        {
            // If we're comparing same individual, do nothing
            if (indA == indB)
            {

            }

            // Else, if indA fitness is higher (better) than or equal to indB on both fronts, indA is not dominated 
            if ( (indB.normalised_fitness >= indA.normalised_fitness) && ( indB.normalised_sequence_fitness >= indA.normalised_sequence_fitness) )
            {
                return false; 
            }
        }

        // Otherwise, indA is dominated
        return true;
    }

    static int count_fitness = 0;
    // Function that runs all 3 functions above, calculating ranks and crowding distances
    public void bi_objective_fitness(ArrayList<individual> population)
    {
        // for (int i=0;i<population.size(); i++)
        // {
        //     System.out.println(population.get(i).toString() + " score and sequence fitness: " + population.get(i).fitness + ", " + population.get(i).sequence_fitness);
        // }

        // Begin by clearing existing rank and crowding values in population
        // This is because new offspring have been added so the ranks are not longer valid
        for (individual ind : population)
        {
            ind.rank = -1;
            ind.crowdingDistance = -1;  
        }
    
        // Normalise the fitness values
        normaliseFitnesses(population);
        
        // Shallow copy of population
        ArrayList<individual> remainingToBeRanked = new ArrayList<individual>(population); 

        // System.out.println("remainingToBeRanked size: " + remainingToBeRanked.size());
        
        // Create an arraylist of arraylists
        ArrayList<ArrayList<individual>> allRanks = new ArrayList<ArrayList<individual>>(); 
        
        // Now sort the population into fronts:
        int currentRank = 1; 

        // While remaining to be ranked has individuals still remaining...
        while(!remainingToBeRanked.isEmpty())
        {
            ArrayList<individual> indsInCurrentRank = new ArrayList<individual>();
            ArrayList<Integer> addedIndices = new ArrayList<Integer>();

            for (int i=0; i<remainingToBeRanked.size(); i++)
            {
                individual ind = remainingToBeRanked.get(i);
                if ( notDominated(ind, remainingToBeRanked ))
                {
                    // System.out.println("NON DOMINATED INDIVIDUAL REACHED");
                    ind.rank = currentRank; 
                    indsInCurrentRank.add(ind);
                    addedIndices.add(i);
                }
            }

            if (indsInCurrentRank.size() == 0)
            {
                for(individual ind:remainingToBeRanked)
                {
                    ind.rank = currentRank;
                }

                allRanks.add(remainingToBeRanked);
                break;

            } else 
            {
                // Add rank to the list that holds ranks
                allRanks.add(indsInCurrentRank);
            }

            // Remove all ranked individuals from the "toBeRanked" list
            Collections.sort(addedIndices);
            int removed = 0; 
        
            for (int index : addedIndices)
            {
                // System.out.println("FOR LOOP RUNNING"); 
                remainingToBeRanked.remove(index-removed);
                removed++;
            }

            // Increment to next rank
            currentRank++; 
        }
        
        // Loop through all ranks, and calculate crowding distances
        for (ArrayList<individual> rank : allRanks) 
        {
            calcCrowdingDistance(rank);
        }

        // for (int i=0;i<population.size(); i++)
        // {
        //     System.out.println(population.get(i).toString()+  " rank and crowding: " + population.get(i).rank + ", " + population.get(i).crowdingDistance);
        //     System.out.println(population.get(i).toString() + " score and sequence fitness: " + population.get(i).fitness + ", " + population.get(i).sequence_fitness);
        //     System.out.println(population.get(i).toString() + " normalised score and sequence fitness: " + population.get(i).normalised_fitness + ", " + population.get(i).normalised_sequence_fitness);
        // }
    }

    // calculates hypervolume for a population
    public double hypervolume_population(ArrayList<individual> population)
    {
        // initialising variables to sort copied version of population based on x axis variable
        ArrayList<individual> copied_pop = new ArrayList<individual>(population);
        Collections.sort(copied_pop, Comparator.comparingDouble(individual :: get_fitness));

        // hypervolume variable which is returned
        double hypervolume = 0;
        double absolute_sequence_fitness = 0;
        double gen_size = genotype_size;

        // calculating hypervolume for given population
        for ( int i = 0; i < population_size; i++ )
        {
            // calculating absolute normalised sequence fitness (based on set genotype size rather than a varying max sequence length of a population)
            absolute_sequence_fitness = (gen_size - copied_pop.get(i).sequence_fitness)/gen_size;

            // first index does not have previous data point
            if ( i == 0 )
            {
                hypervolume = hypervolume + ( copied_pop.get(i).normalised_fitness*absolute_sequence_fitness);

            // every other data point has a previous data point
            }else
            {
                hypervolume = hypervolume + ( (copied_pop.get(i).normalised_fitness - copied_pop.get(i-1).normalised_fitness)*absolute_sequence_fitness);
            }
        }

        return hypervolume;
    }

    /*


        CODE FOR NSGA ENDS


    */


    /**
     *
     * Very simple diy GA
     *
     * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
     * @return An action for the current state
     */
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        // calculating fitness for first seed population
        calculate_initial_population_fitness(stateObs, population);

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

            // hypervolume
            double hypervolume = 0;
            double previous_best_hypervol = 0;

            // text that is printed at the end of act
            String text = "";
            String index = "";
            index = k+"";

            int gen_count = 0;

            // resetting advance count and once variable
            advance_count = 0;
            once = false;
            test_counter++;
            int selectionSize = population_size; 

            // moving onto next level (may not actually be needed depending on how testing is done)
            if ( test_counter > 9 ){
                test_counter = 0;
            }

            // evolve while we have time remaining
            while ( advance_count < 5000001 )
            {   
                int cutoff_two = 0;

                previous_best_moves = best_moves_text;
                previous_best_score = best_score_text;
                previous_best_score_double = best_score;
                previous_best_hypervol = hypervolume;

                best_moves_text = "";

                gen_count++;

                // crossover 
                for(int i = 0; i < (population_size/2); i++)
                {   
                    // select parents
                    // Get index of random parents
                    int fatherIndex = rand.nextInt(population_size);
                    int motherIndex = rand.nextInt(population_size);

                    ArrayList<individual> temp2 = n_point_crossover(population.get(fatherIndex), population.get(motherIndex), 7);
                    new_population.add(temp2.get(0));
                    new_population.add(temp2.get(1));
                }

                // mutation
                for ( int i = 0; i < new_population.size(); i++ )
                {
                    // mutation is done once (can change to multiple times if need be)
                    new_population.set(i,random_mutate(new_population.get(i),0.5,20));
                }

                // add children to parent population, will result in a population of double size
                population.addAll(new_population);

                // calculate fitness
                calculate_population_fitness(stateObs, new_population);

                new_population.clear();

                /*

                    CODE FOR NSGA GOES HERE

                */
                
                // Calculate dominance ranks and crowding distance for all individuals
                bi_objective_fitness(population);
                // System.out.println("pop size after bi: " + population.size());
                // for (int i=0; i<population.size(); i++)
                // {
                //     System.out.println(population.get(i).toString()+ " rank and crowding: " + population.get(i).rank + ", " + population.get(i).crowdingDistance);
                //     System.out.println(population.get(i).toString() + " score and sequence fitness: " + population.get(i).fitness + ", " + population.get(i).sequence_fitness);
                //     System.out.println(population.get(i).toString() + " normalised score and sequence fitness: " + population.get(i).normalised_fitness + ", " + population.get(i).normalised_sequence_fitness);
                // }

                /// SORT THE POPULATION ///
                // We need the population sorted from lowest rank to highest (rank 1 being best rank)
                // Then WITHIN each rank, the individuals need to be sorted from highest crowding distance to lowest

                // Get number of ranks to begin with
                int numRanks = 0; 
                for (int j = 0; j<population.size(); j++)
                {
                    if (population.get(j).rank > numRanks)
                    {
                        numRanks = population.get(j).rank; 
                    }
                }

                // System.out.println("Numranks is: " + numRanks);
                
                // Create ordered population list
                ArrayList<individual> orderedPop = new ArrayList<individual>();

                // System.out.println("Ordering population now...");
                
                // For each rank... 
                for (int j = 0; j<numRanks; j++)
                {
                    // Create a temporary list
                    ArrayList<individual> tempRank = new ArrayList<individual>();

                    // Then loop through the whole population
                    for (int n = 0; n<population.size(); n++)
                    {
                        // And build up a sublist of individuals from the current rank
                        if (population.get(n).rank == j+1)
                        {
                            tempRank.add(population.get(n)); 
                        }
                    }

                    // System.out.println("temprank has size: " + tempRank.size());

                    // Now order the temporary list in descending order of crowding distance
                    Collections.sort(tempRank, Comparator.comparingDouble(individual :: get_crowdingDistance));
                    Collections.reverse(tempRank);

                    // Then add the sorted rank to a new population list
                    orderedPop.addAll(tempRank); 
                }

                population.clear();

                // System.out.println("OrderedPop has size: " + orderedPop.size()); 

                // Now that we have our full population ordered, add the top individuals back into population
                for (int j=0; j<selectionSize; j++)
                {
                    population.add(orderedPop.get(j));
                }

                // for (int i=0; i<population_size; i++)
                // {
                //     System.out.println(population.get(i).toString()+ " rank and crowding: " + population.get(i).rank + ", " + population.get(i).crowdingDistance);
                // }

                /*

                    CODE FOR NSGA ENDS HERE

                */
                
                hypervolume = hypervolume_population(population);

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

                    text = "Test " + index + ":\n" + "At 200,000 advance calls:\nBest Ind Score: " + previous_best_score + "\nBest Ind Genotype: " + previous_best_moves + "\nHypervolume: " + previous_best_hypervol+"";

                    two_hundred_thou = false;
                }

                if (one_million) {
                    scores1mill.add(previous_best_score_double);

                    text = text + "\n\nAt 1,000,000 advance calls:\nBest Ind Score: " + previous_best_score + "\nBest Ind Genotype: " + previous_best_moves + "\nHypervolume: " + previous_best_hypervol+"";

                    one_million = false;
                }

                if (five_million){
                    scores5mill.add(previous_best_score_double);

                    text = text + "\n\nAt 5,000,000 advance calls:\nBest Ind Score: " + previous_best_score + "\nBest Ind Genotype: " + previous_best_moves + "\nHypervolume: " + previous_best_hypervol+"";

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

        handle_files.write_to_file("results/assignment03/exercise03/BomberTest", final_text);

        /* it doesn't matter what act() returns, as it is guaranteed to time-out anyway
        (which is fine as we only care about calls to advance) */
        return ACTIONS.ACTION_NIL;
    }
}

