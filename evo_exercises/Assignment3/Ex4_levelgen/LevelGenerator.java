package evo_exercises.Assignment3.Ex4_levelgen;

import java.util.Map.Entry;
import java.lang.reflect.Constructor;
import java.security.DrbgParameters.NextBytes;
import java.util.*;

import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;

import core.game.Event;
import core.game.GameDescription.TerminationData;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import tracks.levelGeneration.constraints.CombinedConstraints;
import ontology.Types;
import ontology.Types.WINNER;
import tools.LevelMapping;
import tools.StepController;
import tools.Vector2d;

import evo_exercises.Assignment3.Ex4_levelgen.individual;
import handle_files.handle_files;
import ontology.effects.binary.WallStop;
//import serialization.Vector2d;

// THIS PROGRAM IS HARDCODED TO WORK ONLY FOR SPECIFIC GAMES


public class LevelGenerator extends AbstractLevelGenerator{

    Random rand;
    GameDescription game;
    AbstractPlayer automatedAgent;
    HashMap<Character, ArrayList<String>> lmap;
    int pop_size = 4;
    int numGens = 16;
    /*   
        MAIN REQUIRED GENERATION FUNCTIONS
                                             */

    ////// REQUIRED CONSTRUCTOR //////
	public LevelGenerator(GameDescription _game, ElapsedCpuTimer elapsedTimer)
    {
		rand = new Random();
        game = _game;

        // lets generate the level mapping in here
        // trying to use inbuild function here
        // ok? this creates an empty level map, fuck you GVGAI
        lmap = game.getLevelMapping();
	}


	////// LEVEL GEN //////
	@Override
	public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer) 
    {
        // var decs
		ArrayList<individual> population = new ArrayList<individual>();
        ArrayList<individual> final_population = new ArrayList<individual>();
        String result = " ";

        // initialise population
        for(int i = 0; i < pop_size; i++)
        {
            population.add(new individual());
            // calculate_individual_fitness(population.get(i));
        }


        final_population = bi_Objective_GA(population, numGens);
        
        // calculate hypervolume
        double hypervolume = hypervolume_population(final_population);

        // print genotypes
        
        for ( int i = 0; i < final_population.size(); i++)
        {
            String wallfitness, coveragefitness, rank;
            wallfitness = final_population.get(i).wallFitness+"";
            coveragefitness = final_population.get(i).coverageFitness+"";
            rank = final_population.get(i).rank+"";

            handle_files.write_to_file("results/assignment03/ex4/test1", "WAL_FIT: "+wallfitness);
            handle_files.write_to_file("results/assignment03/ex4/test1", "COVER_FIT: "+coveragefitness);
            handle_files.write_to_file("results/assignment03/ex4/test1", "RANK: "+rank);
            handle_files.write_to_file("results/assignment03/ex4/test1", convert_genotype_to_map(final_population.get(i)));
            handle_files.write_to_file("results/assignment03/ex4/test1", "\n\n\n\n\n");
        }
        
        result = convert_genotype_to_map(final_population.get(0));
		return result;
	}


    /*   
        EA SUPPORT FUNCTIONS
                              */

    ////// FITNESS //////

    public void calculate_individual_fitness(individual ind)
    {
        // this should initialise the best agent,
        // best agent from sample code was incapable of playing zelda
        // so have gone with the NovelTS controller to test level possibility
        try
        {
            Class agentClass = Class.forName("NovelTS.Agent");
            Constructor agentConst = agentClass.getConstructor(new Class[]{StateObservation.class, ElapsedCpuTimer.class});
            automatedAgent = (AbstractPlayer)agentConst.newInstance(getStateObservation(ind).copy(), null);
            }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        // pass this agent on to the individuals fitness calculation algo
        ind.calc_fitness(automatedAgent, getStateObservation(ind).copy());

    }

     
    ////// MUTATIONS AND CROSSOVER //////


    /// CREATE TILE ///
    // tile chance, either places a wall or random optional
    // currently able to overwrite required tiles, i.e doors, locks and avatars
    double wall_chance = 0.95;

    public void create_tile(individual ind)
    {
        if(rand.nextDouble()<wall_chance)
        {
            //place wall, this can overwrite required tiles
            ind.genotype[rand.nextInt(ind.genotype.length-1)] = 'w';
        }
        else
        {
            //place random optional, this can overwrite required tiles
            ind.genotype[rand.nextInt(ind.genotype.length-1)] = ind.optional[rand.nextInt(ind.optional.length)];
        }   
    }

    /// DESTROY TILE ///
    // replace a random tile with a floor tile i.e "destroy" it
    public void destroy_tile(individual ind)
    {
        ind.genotype[rand.nextInt(ind.genotype.length)] = '.';
    }

    // Randomly perform mutations
    public void mutate(individual ind)
    {
        double decider = rand.nextDouble();

        if (decider < 0.5)
        {
            // 2 of same mutation
            create_tile(ind); 
            create_tile(ind);

        } else if (decider >= 0.5 && decider < 0.75)
        {
            // 2 different
            create_tile(ind);
            destroy_tile(ind);

        } else 
        {
            // 2 same
            destroy_tile(ind);
            destroy_tile(ind);
        }
    }

    /// N-POINT CROSSOVER ///
    // returns an arrary list of 2 children individual objects after n-point parent crossover
    // takes in individual genotypes, not individuals
    public ArrayList<individual> n_point_crossover(char[] ind1, char[] ind2, int num_cross)
    {
    
        // initialising an arraylist of children to return
        ArrayList<individual> children = new ArrayList<individual>();

        // creating children clones
        char[] child1 = ind1.clone();
        char[] child2 = ind2.clone();

        // generating the actual crossover points
        ArrayList<Integer> crossover_points = new ArrayList<Integer>();

        // determining the crossover points
        for (int j = 0; j < num_cross; j++){
            int crossover_point = rand.nextInt(ind1.length);

            if(!crossover_points.contains(crossover_point))
            {
                crossover_points.add(crossover_point);
            }
        }

        // iterates through crossover points to end of list and swaps values
        int count = 0;
        Collections.sort(crossover_points);
        for ( int i = 0; i < ind1.length; i++ ){

            // checks for crossover points
            if ( i == crossover_points.get(count) ){
                count++;

                if ( count >= crossover_points.size())
                {
                    count --;
                }

            }

            // performs crossover if necessary
            if ( count % 2 == 1 ){
                char temp = child1[i];
                child1[i] = child2[i];
                child2[i] = temp;  
            }
        }

        // adding children
        children.add(new individual(child1));
        children.add(new individual(child2));
      

        return children;
    }

    /// 1D->2D CONVERSION ///
    // converts genotype which is a 1d char array to a walled map as
    // a string- this can be passed to getStateObservation().
    static public String convert_genotype_to_map(individual _ind)
    {
        String result = "";
        for(int w = 0; w < (_ind.playable_width+2); w++)
        {
            result += "w";
        }

        result += '\n';

        for(int y = 0; y < _ind.playable_height; y++)
        {
            result += "w";
            for(int x = 0; x < _ind.playable_width; x++)
            {
                result += _ind.genotype[x+y*_ind.playable_width];
            }

            result += "w";
            result += '\n';
            
        }

        for(int w = 0; w < (_ind.playable_width+2); w++)
        {
            result += "w";
        }

        return result;
    }

    // CALCULATING HYPERVOLUME FOR A POPULATION
    public double hypervolume_population(ArrayList<individual> population)
    {
        // initialising variables to sort copied version of population based on x axis variable
        ArrayList<individual> copied_pop = new ArrayList<individual>(population);
        Collections.sort(copied_pop, Comparator.comparingDouble(individual :: get_coverage_fitness));

        // hypervolume variable which is returned
        double hypervolume = 0;

        // calculating hypervolume for given population
        for ( int i = 0; i < pop_size; i++ )
        {
            // first index does not have previous data point
            if ( i == 0 )
            {
                hypervolume = hypervolume + ( copied_pop.get(i).coverageFitness*copied_pop.get(i).wallFitness );

            // every other data point has a previous data point
            }else
            {
                hypervolume = hypervolume + ( (copied_pop.get(i).coverageFitness - copied_pop.get(i-1).coverageFitness)*copied_pop.get(i).wallFitness );
            }
        }

        return hypervolume;
    }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /* 
        HELPER FUNCTIONS FOR GA
                                 */


    // Normalises fitness values to make sure there is not a bias towards one objective
    public void normaliseFitnesses(ArrayList<individual> population)
    {
        // Find max fitness values in population
        int maxWallFitness = 1; 
        int maxCoverageFitness = 1;
        for (int i=0; i<population.size(); i++)
        {
            if (population.get(i).wallFitness > maxWallFitness)
            {
                maxWallFitness = population.get(i).wallFitness;
            }
            if (population.get(i).coverageFitness > maxCoverageFitness)
            {
                maxCoverageFitness = population.get(i).coverageFitness;
            }
        } 

        // Modify normalised fitness values
        for (int i=0; i<population.size(); i++)
        {
            population.get(i).normalisedWallFitness = population.get(i).wallFitness / maxWallFitness;
            population.get(i).normalisedCoverageFitness = population.get(i).coverageFitness / maxCoverageFitness;

        }
    }


    // Calculates the crowding distances for all individuals in a rank
    public void calcCrowdingDistance(ArrayList<individual> rank)
    {
        // // Deep copy the rank
        // ArrayList<individual> rankCopy = new ArrayList<individual>();
        // System.arraycopy(rank, 0, rankCopy, 0, rank.size()); 

        // Order the rank based on any of the two normalised fitness values
        Collections.sort(rank, Comparator.comparingDouble(individual :: get_normalisedWallFitness));

        // Loop through the rank (the front)
        for (int i=0; i<rank.size(); i++)
        {

            // If we are at first or last individual, assign +inf. crowding distance
            if ( i==0 || i==rank.size()-1 )
            {
                rank.get(i).crowdingDistance = Double.POSITIVE_INFINITY; 
            }
            // All other cases...
            else
            {
                // Get references to current, left, and right individuals
                individual current = rank.get(i);
                individual left = rank.get(i-1);
                individual right = rank.get(i+1);

                // Get vector positions of the three individuals, based upon 2 fitness vals
                Vector2d currentPos = new tools.Vector2d(current.normalisedWallFitness, current.normalisedCoverageFitness); 
                Vector2d leftPos = new tools.Vector2d(left.normalisedWallFitness, left.normalisedCoverageFitness); 
                Vector2d rightPos = new tools.Vector2d(right.normalisedWallFitness, right.normalisedCoverageFitness); 

                // Calc distance from current to left and right vecs
                double leftDistance = currentPos.dist(leftPos); 
                double rightDistance = currentPos.dist(rightPos); 

                // Crowding distance is the total of these two values
                rank.get(i).crowdingDistance = leftDistance + rightDistance; 
            }
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
            else 
            // Else, if indA fitness is higher (better) than or equal to indB on both fronts, indA is not dominated 
            if ( (indB.normalisedWallFitness > indA.normalisedWallFitness) || (indB.normalisedCoverageFitness > indA.normalisedCoverageFitness) )
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
        for (int i=0;i<population.size(); i++)
        {
            System.out.println(population.get(i).toString() + "wall and coverage fitness: " + population.get(i).wallFitness + ", " + population.get(i).coverageFitness);
        }


        // Counting
        float percentage = (count_fitness/numGens*pop_size)*100;
        System.out.print( "\r" + count_fitness + "/" + numGens*pop_size + " " + "(" );
        System.out.printf( "%.1f",percentage );
        System.out.print( "%" + ")" );

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
                    System.out.println("NON DOMINATED INDIVIDUAL REACHED");
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
                System.out.println("FOR LOOP RUNNING"); 
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

        for (int i=0;i<population.size(); i++)
        {
            System.out.println(population.get(i).toString() + "rank and crowding: " + population.get(i).rank + ", " + population.get(i).crowdingDistance);
        }


    }
    

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*   
        GA TO FIND BEST GAME LEVELS
                                        */

    /// Biobjective GA works as follows for each generation:

    // 1. Get initial population of M randomly generated levels
    // 2. Select parents for crossover and mutation 
    // 3. Create offspring until number equals same as parent population 
    // 4. Combine parents and offspring to make new population of size 2*M
    // 5. Sort total population into ranks using dominance heirachies - see above function
    // 6. Sort each rank by crowding distance - see above function
    // 7. Based on rank and crowding, take top M amount of individuals, discard the bottom M
    // 8. Repeat for n number of generations

    public ArrayList<individual> bi_Objective_GA(ArrayList<individual> population, int numGens)
    {
        int selectionSize = population.size(); 
        
        // // Create return array
        // ArrayList<individual> paretoFront = new ArrayList<individual>();

        // Create deep copy of population
        ArrayList<individual> pop = new ArrayList<individual>(population);
        
        // For each generation
        for (int i=0; i<numGens; i++)
        {
            // // Malakia
            // float percentage = (i+1/numGens)*100;
            // System.out.print( "\r" + (i+1) + "/" + numGens + " " + "(" );
            // System.out.printf( "%.1f",percentage );
            // System.out.print( "%" + ")" );
            
            // Create offspring array              
            ArrayList<individual> offspring = new ArrayList<>(); 

            // Create new individuals until offspring size = population size
            while (offspring.size() < pop.size())
            {
                // // Get index of random parents
                int fatherIndex = rand.nextInt(pop.size());
                int motherIndex = rand.nextInt(pop.size());

                // // Ensure not same individual twice selected
                // while (motherIndex == fatherIndex)
                // {
                //     motherIndex = rand.nextInt(pop.size());
                // }

                // // Do crossover, **Choose number of crossover points
                // ArrayList<individual> children = n_point_crossover(pop.get(motherIndex).genotype, pop.get(fatherIndex).genotype, 3); 

                // crossover is fucked- trying mutate only
                ArrayList<individual> children = new ArrayList<individual>();
                individual child1 = new individual(population.get(motherIndex).genotype);
                individual child2 = new individual(population.get(fatherIndex).genotype);
                children.add(child1);
                children.add(child2);

                // Mutate the children
                mutate(children.get(0));
                mutate(children.get(1));   

                // Add the two children to offspring
                offspring.addAll(children); 
            }

            // Once offspring size matches parent pop size, combine into one pop
            pop.addAll(offspring);

            // ***Calculate coverage and wall fitness for each individual in pop BEFORE dominance ranking
            //
            //
            //
            for(individual ind:pop)
            {
                calculate_individual_fitness(ind);
            }

            // Calculate dominance ranks and crowding distance for all individuals
            bi_objective_fitness(pop); 

            /// SORT THE POPULATION ///
            // We need the population sorted from lowest rank to highest (rank 1 being best rank)
            // Then WITHIN each rank, the individuals need to be sorted from highest crowding distance to lowest

            // Get number of ranks to begin with
            int numRanks = 0; 
            for (int j=0; j<pop.size(); j++)
            {
                if (pop.get(j).rank > numRanks)
                {
                    numRanks = pop.get(j).rank; 
                }
            }

            System.out.println("Numranks is: " + numRanks);
            
            // Create ordered population list
            ArrayList<individual> orderedPop = new ArrayList<individual>();

            System.out.println("Ordering pop now...");
            
            // For each rank... 
            for (int j=0; j<numRanks; j++)
            {
                // Create a temporary list
                ArrayList<individual> tempRank = new ArrayList<individual>();

                // Then loop through the whole population
                for (int k=0; k<pop.size(); k++)
                {
                    // And build up a sublist of individuals from the current rank
                    if (pop.get(k).rank == j+1)
                    {
                        tempRank.add(pop.get(k)); 
                    }
                }

                System.out.println("temprank has size: " + tempRank.size());

                // Now order the temporary list in descending order of crowding distance
                Collections.sort(tempRank, Comparator.comparingDouble(individual :: get_crowdingDistance));
                Collections.reverse(tempRank);

                // Then add the sorted rank to a new population list
                orderedPop.addAll(tempRank); 
              
            }

            System.out.println("OrderedPOp has size: " + orderedPop.size()); 

            pop.clear();
            // Now that we have our full population ordered, add the top individuals back into pop
            for (int j=0; j<selectionSize; j++)
            {
                pop.add(orderedPop.get(j)); 
            }

            for (int k=0;k<pop.size(); k++)
            {
                System.out.println(pop.get(k).toString() + "rank and crowding: " + pop.get(k).rank + ", " + pop.get(k).crowdingDistance);
            }
            
        }

        // Some code for printing final population genotypes to file 

        // Return final population
        return pop;
    }



	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	


    /* 
        BORROWED FUNCTIONS FOR RUNNING AN AGENT
                                                */

    /// DO: Fix state observation- we have level as string- just need level mapping

    /**
	 * current level described by the chromosome
	 */
	ArrayList<String>[][] level;
    StateObservation stateObs;

    /**
	 * get game state observation for the current level
	 * @return	StateObservation for the current level
	 */
	private StateObservation getStateObservation(individual ind){

		// get the level map as a string
		String levelString = convert_genotype_to_map(ind);

        // start a game with these parameters- this is different from how
        // the given sampleGAGenerator does it, but matches the function signature so might work
		stateObs = game.testLevel(levelString, lmap);
		return stateObs;
	}



////// SELECTION METHODS //////


    /// MODIFIED TRUNCATION SELECT ///

    // Removes all population members below a certain fitness
    // Or, I think it'd be better if it selects the top proportion of individuals
    // since we don't know what fitness values we're gonna get

    public ArrayList<individual> modified_truncation_selection(ArrayList<individual> population, double fitnessThreshold)
    {
        // Initialise an array list to return
        ArrayList<individual> selectedIndividuals = new ArrayList<individual>(); 

        // Copy the population array
        // This is a shallow copy, so it might need to be changed idk???
        ArrayList<individual> competingIndividuals = new ArrayList<individual>(population);  

        // Sort population by fitness, and then reverse to get from highest -> lowest fitness
        Collections.sort(competingIndividuals, Comparator.comparingInt(individual :: get_fitness));
        Collections.reverse(competingIndividuals);
    
        // Fill the new population with only the individuals above the threshold value
        // Essentially, removing individuals below that value
        int i = 0; 
        while (competingIndividuals.get(i).wallFitness > fitnessThreshold )
        {
            selectedIndividuals.add(competingIndividuals.get(i)); 
            i++; 
        }

        return selectedIndividuals; 
    }


    /// TOURNAMENT AND ELITISM SELECT ///
    // Borrowed from Ex2, and then modified to fit Ex4

    // Tournament selection WITHOUT replacement. Returns 2 individuals to be parents. k = tournament size
    public ArrayList<individual> tournament_selection(ArrayList<individual> population, int k){
       
        // initialising arraylist of 2 parents to return
        ArrayList<individual> parents = new ArrayList<individual>();

        // initialising arraylist of candidate indices and candidates
        List<Integer> indices = new ArrayList<Integer>();
        ArrayList<individual> candidates = new ArrayList<individual>();
        
        // fills up a list of indices which is then shuffled, then the first k indices are taken (this prevents duplicates)
        for (int i = 0; i < population.size(); i++){
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
            if ((candidates.get(i)).wallFitness >= best_fitness){
                second_best_fitness = best_fitness;
                best_fitness = (candidates.get(i)).wallFitness;
                best_individual_index = i;
                
            } else if ((candidates.get(i)).wallFitness > second_best_fitness){
                second_best_fitness = (candidates.get(i)).wallFitness;
                second_individual_index = i;
            }
        }

        // adding best and second best individuals to return list
        parents.add(candidates.get(best_individual_index));
        parents.add(candidates.get(second_individual_index));

        return parents;
    }

    // Elitism 
    // Returns a variable number of elites, depending on input
    public ArrayList<individual> get_elites(ArrayList<individual> population, int numElites){
        
        // initialising return list of elites
        ArrayList<individual> elites = new ArrayList<individual>();

        // Make copy of population 
        ArrayList<individual> populationCopy = new ArrayList<individual>(population);  

        // Sort population by fitness, and then reverse to get from highest -> lowest fitness
        Collections.sort(populationCopy, Comparator.comparingInt(individual :: get_fitness));
        Collections.reverse(populationCopy);

        // finding elites based on number specified in function call
        for (int i=0; i<numElites; i++)
        {
            elites.add(populationCopy.get(i));
        }

        return elites;
    }

}
   
