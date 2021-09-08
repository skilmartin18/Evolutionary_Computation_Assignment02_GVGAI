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
		String sampleRandomController = "tracks.singlePlayer.simple.sampleRandom.Agent";
		String sampleOneStepController = "tracks.singlePlayer.simple.sampleonesteplookahead.Agent";
        String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";
	

		//Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

		//Game settings
		boolean visuals = false;
		int seed = new Random().nextInt();

		// Game and level to play
		int gameIdx = 11; 
		int levelIdx = 0; // level names from 0 to 4 (game_lvlN.txt).
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];
		String level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);

		//String recordActionsFile = "actions_" + games[gameIdx] + "_lvl"
						// + levelIdx + "_" + seed + ".txt";
						// where to record the actions
						// executed. null if not to save.

		// 4. This plays a single game, in N levels, M times :
//		String level2 = new String(game).replace(gameName, gameName + "_lvl" + 1);
//		int M = 10;
//		for(int i=0; i<games.length; i++){
//			game = games[i][0];
//			gameName = games[i][1];
//			level1 = game.replace(gameName, gameName + "_lvl" + levelIdx);
//			ArcadeMachine.runGames(game, new String[]{level1}, M, sampleMCTSController, null);
//		}

		//5. This plays games_played games, in the first L levels, M times each.
		// set games_played- indices are games indexes from all_games_sp.csv
		int[] games_played = {0,11,13,18}; 
		int L = 1, M = 2;

		String[] levels = new String[L];

		for(int i = 0; i < games_played.length; ++i)
		{
			// get game and name
			game = games[ games_played[i] ][ 0 ];
			gameName = games[ games_played[i] ][1];
			// create list of levels to play
			for(int j = 0; j < L; ++j){
				levels[j] = game.replace(gameName, gameName + "_lvl" + j);
			}
			ArcadeMachine.runGames(game, levels, M, sampleRandomController , null);
		}


    }
}

