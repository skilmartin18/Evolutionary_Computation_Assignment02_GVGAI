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

import evo_exercises.Assignment3.Ex4_levelgen.individual;
import handle_files.handle_files;
import ontology.effects.binary.WallStop;

// THIS PROGRAM IS HARDCODED TO WORK ONLY FOR SPECIFIC GAMES


public class LevelGenerator extends AbstractLevelGenerator{

    Random rand;
    GameDescription game;
    AbstractPlayer automatedAgent;
    HashMap<Character, ArrayList<String>> lmap;
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
		
        individual _ind = new individual();
        String result = convert_genotype_to_map(_ind);
        calculate_individual_fitness(_ind);
		return result;
	}


    /*   
        EA SUPPORT FUNCTIONS
                              */

    ////// FITNESS //////

    public void calculate_individual_fitness(individual ind)
    {
        // this should initialise the best agent 
        try
        {
        Class agentClass = Class.forName("YOLOBOT.Agent");
		Constructor agentConst = agentClass.getConstructor(new Class[]{StateObservation.class, ElapsedCpuTimer.class});
		automatedAgent = (AbstractPlayer)agentConst.newInstance(getStateObservation(ind).copy(), null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        ind.calc_fitness(automatedAgent, getStateObservation(ind).copy());

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
        while (competingIndividuals.get(i).fitness > fitnessThreshold )
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
    


    /*   
        GA TO FIND BEST GAME LEVELS
                                        */

    public ArrayList<individual> bi_Objective_GA(ArrayList<individual> population)
    {
        // Create return array
        ArrayList<individual> paretoFront = new ArrayList<individual>();

        // Do some fun stuff...

        // Return best individuals
        return paretoFront;
    }

	

	
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


}

