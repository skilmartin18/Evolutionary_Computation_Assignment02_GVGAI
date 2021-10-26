package evo_exercises.Assignment3.Ex4_levelgen;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import core.game.GameDescription;
import core.game.GameDescription.SpriteData;
import core.generator.AbstractLevelGenerator;
import tools.ElapsedCpuTimer;
import tools.GameAnalyzer;

import evo_exercises.Assignment3.Ex4_levelgen.individual;
import handle_files.handle_files;

// THIS PROGRAM IS HARDCODED TO WORK ONLY FOR COOKMEPASTA


public class LevelGenerator extends AbstractLevelGenerator{

	public LevelGenerator(GameDescription game, ElapsedCpuTimer elapsedTimer){
		
	}
	/**
	 * Generate a level string randomly contains only one avatar, 80% free space, and 20% of random sprites
	 * @param game			Abstract game description object. This object contains all needed information about the current game.
	 * @param elapsedTimer	Timer to define the maximum amount of time for the level generation.
	 */
	@Override
	public String generateLevel(GameDescription game, ElapsedCpuTimer elapsedTimer) {
		
        individual _ind = new individual();
        String result = convert_genotype_to_map(_ind);
		return result;
	}

    // converts genotype which is a 1d char array to a walled map as
    // a string
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
	
	
}

