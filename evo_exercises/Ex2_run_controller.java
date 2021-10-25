package evo_exercises;
import java.util.Random;

import core.logging.Logger;
import tools.Utils;
import tracks.ArcadeMachine;

/**
 * Created with IntelliJ IDEA. User: Diego Date: 04/10/13 Time: 16:29 This is a
 * Java port from Tom Schaul's VGDL - https://github.com/schaul/py-vgdl
 */
public class Ex2_run_controller {

    public static void main(String[] args) {

		// Available tracks:
		String sampleRandomController = "tracks.singlePlayer.simple.simpleRandom.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
        String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";

		// Multi mate
		String multiStepLookAgent = "tracks.singlePlayer.diy.multiStepLookAhead.Agent";
		String sampleRHEAController = "tracks.singlePlayer.advanced.sampleRHEA.Agent";
		String diyGA = "evo_exercises.Ex4_diy_GA.Agent";
		String gamercool = "evo_exercises.Assignment3.Ex2_controller.Agent";
	
		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = false;
		int seed = new Random().nextInt();
		String recordActionsFile = null;

		// This plays games_played games, in the first L levels, M times each.
		// set games_played- indices are games indexes from all_games_sp.csv
		//int[] games_played = {0,11,13,18}; 
		int[] games_played = {8,10,18,45}; 
		int L = 5, M = 1;

		String[] levels = new String[L];

		for(int i = 0; i < games_played.length; ++i)
		{
			
			// get game and name
			String game = games[ games_played[i] ][ 0 ];
			String gameName = games[ games_played[i] ][1];
			System.out.println("\n\nPlaying "+gameName);
			// create list of levels to play
			for(int j = 0; j < L; ++j){
				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
			}

			//
			// ARCADEMACHINE.JAVA, LINE 537 IS WHERE OUTPUT FILE IS SPECIFIED
			//

			for(int k = 0; k < L; ++k){
				// runGames must take levels as an string array- so convert levels[k] into one
				System.out.println("Lvl_"+k+":");
				//ArcadeMachine.runGames(game, new String[]{levels[k]}, M, sampleRandomController, null);
				//ArcadeMachine.runGames(game, new String[]{levels[k]}, M, multiStepLookAgent, null);
				//ArcadeMachine.runGames(game, new String[]{levels[k]}, M, diyGA, null);
				//ArcadeMachine.runGames(game, new String[]{levels[k]}, M, diyGA, null);
				ArcadeMachine.runOneGame(game, levels[k], visuals, gamercool, recordActionsFile, seed, 0);
			}
			
		}



    }
}

