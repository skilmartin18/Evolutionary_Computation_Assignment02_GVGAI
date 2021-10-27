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
    
    /*   
        MAIN REQUIRED GENERATION FUNCTIONS
                                             */

    ////// REQUIRED CONSTRUCTOR //////
	public LevelGenerator(GameDescription _game, ElapsedCpuTimer elapsedTimer)
    {
		rand = new Random();
        game = _game;
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
        Class agentClass = Class.forName("tracks.singlePlayer.tools.repeatOLETS.Agent");
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
    // Luke do this-- removes all population members below a certain fitness
    // population as and arraylist of individuals probably?





    
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
        BORROWED FUNCTIONS FOR RUNNING AN AGENT
                                                */

    /// TODO: Fix state observation- we have level as string- just need level mapping

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

        LevelMapping levelMapping = new LevelMapping(game);
		levelMapping.clearLevelMapping();
		char c = 'a';
		for(int y = 0; y < level.length; y++){
			for(int x = 0; x < level[y].length; x++){
				if(levelMapping.getCharacter(level[y][x]) == null){
					levelMapping.addCharacterMapping(c, level[y][x]);
					c += 1;
				}
			}
		}
		
		String levelString = getLevelString(levelMapping);
		stateObs = game.testLevel(levelString, levelMapping.getCharMapping());
		return stateObs;
	}

	/**
	 * get the current level string
	 * @param levelMapping	level mapping object to help constructing the string
	 * @return				string of letters defined in the level mapping 
	 * 						that represent the level
	 */
	public String getLevelString(LevelMapping levelMapping){
		String levelString = "";
		for(int y = 0; y < level.length; y++){
			for(int x = 0; x < level[y].length; x++){
				levelString += levelMapping.getCharacter(level[y][x]);
			}
			levelString += "\n";
		}
		
		levelString = levelString.substring(0, levelString.length() - 1);
		
		return levelString;
	}

    
	

}

