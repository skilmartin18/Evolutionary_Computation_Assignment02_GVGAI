package tracks.levelGeneration;

import java.util.Random;

public class TestLevelGeneration {


    public static void main(String[] args) {

		// Available Level Generators
		String randomLevelGenerator = "tracks.levelGeneration.randomLevelGenerator.LevelGenerator";
		String geneticGenerator = "tracks.levelGeneration.geneticLevelGenerator.LevelGenerator";
		String constructiveLevelGenerator = "tracks.levelGeneration.constructiveLevelGenerator.LevelGenerator";
		String diyLevelGenerator = "evo_exercises.Assignment3.Ex4_levelgen.LevelGenerator";
		String gamesPath = "examples/gridphysics/";
		String physicsGamesPath = "examples/contphysics/";
		String generateLevelPath = gamesPath;

		String games[] = new String[] { "zelda"}; 

		String recordActionsFile = null;// "actions_" + games[gameIdx] + "_lvl"
										// + levelIdx + "_" + seed + ".txt";
										// where to record the actions
										// executed. null if not to save.

		// Other settings
		int seed = new Random().nextInt();
		int gameIdx = 0;
		String recordLevelFile = generateLevelPath + games[gameIdx] + "_glvl.txt";
		String game = generateLevelPath + games[gameIdx] + ".txt";


		// 1. This starts a game, in a generated level created by a specific level generator
		if(LevelGenMachine.generateOneLevel(game, diyLevelGenerator, recordLevelFile)){
		    LevelGenMachine.playOneGeneratedLevel(game, recordActionsFile, recordLevelFile, seed);
		}


		// 2. This generates numberOfLevels levels.
		// String levelGenerator = "tracks.levelGeneration." + args[0] + ".LevelGenerator";
		// int numberOfLevels = 5;
		// tracks.levelGeneration.randomLevelGenerator.LevelGenerator.includeBorders = true;

		// String[] folderName = levelGenerator.split("\\.");
		// generateLevelPath = "examples/generatedLevels/" + folderName[1] + "/";

		// game = gamesPath + args[1] + ".txt";
		// for (int i = 0; i < numberOfLevels; i++) {
		// 	recordLevelFile = generateLevelPath + args[1] + "_lvl" + i + ".txt";
		// 	LevelGenMachine.generateOneLevel(game, levelGenerator, recordLevelFile);
		//}


    }
}
