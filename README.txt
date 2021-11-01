# Evolutionary_Computation_Assignment03_GVGAI
// Git Repo for the third assignment of Evolutionary Computation. 
// University of Adelaide 2021 S2

// Group Members

Sebastian Kilmartin a1774638 a1774638@student.adelaide.edu.au
Michael Lazaros a1773775 a1773775@student.adelaide.edu.au
Fouad Nehme Badanai a1774241 a1774241@student.adelaide.edu.au
Luke Bruno a1774453 a1774453@student.adelaide.edu.au
Jedidiah Kurtzer a1774259@student.adelaide.edu.au

// Code Instructions: 
NOTE: For running tests and troubleshooting, only consider the code within the evo_exercises/Assignment03 directory

For Exercise 2:
Use the run controller Ex2_run_controller.java within the evo_exercises/Assignment3/Ex2_controller directory. It has been set up to automatically run 5 levels each of Bomber, Boulderchase, Chase and Garbagecollector. It does so by calling runOneGames, which calls a predetermined agent (Agent assignment03ex02 from evo_exercises/Assignment03/Ex2_controller/Agent.java), which then calls the act() method. act() has been edited to automatically run 10 tests (0-9) with a predetermined set of parameters (see the example below), and will automatically print results in order to the results/assignment03/exercise02 directory. act() then makes calls to advance() to simulate a controller playing the game. Once 10 tests are complete, act() will return ACTION_NIL, but then force runOneGames to time out (as 5 million calls will always exceed the time limit for any game). This is expected behaviour and will allow the run controller to load up the next level. Please see the example below for more detail on setting up specific games, levels, testing parameters and more.

For Exercise 3:
Similar set-up to Exercise 2. Use run controller Ex3_optimise_GA.java within the evo_exercises/Assignment3/ex3_optimise_GA directory. It has been set up identically to Exercise 2, except it uses the Agent assignment03ex03 from evo_exercises/Assignment03/ex3_optimise_GA/Agent.java. Regarding parameters, Exercise 3 does not include numElites and tournament size k from Exercise 2 (as Elitism and tournament selection are not used). Everything else is the same as explained above.

For Exercise 4:

// Example (for Exercise 2):
In evo_exercises/Assignment3/Ex2_controller/Ex2_run_controller.java

int[] games_played = {8,10,18,45}; 	// ID for Bomber, Boulderchase, Chase and Garbagecollector respectively
int L = 5				// plays 5 levels within each game
ArcadeMachine.runOneGame(game, levels[k], visuals, assignment03ex02, recordActionsFile, seed, 0);	// will use the Agent from Exercise 2

This will automatically run 10 tests on each level, for 5 levels, for 4 games.

Agent names (EAs are run within the act() method of an Agent):
assignment03ex02 = Agent used in Exercise 2
assignment03ex03 = Agent used in Exercise 3
You could run the Exercise 3 Agent within the Exercise 2 run controller by simply changing the Agent name inside runOneGame's 4th parameter.

In evo_exercises/Assignment3/Ex2_controller/Agent.java
You can edit the following parameters: population_size, genotype_size, crossover_spacing, crossover_points, numElites, tournament size k, random_mutate probability and number of genes.

e.g. population_size = 100, genotype_size = 300, crossover_spacing = 7, crossover_points = 7, numElites = 10, k = 15, probability = 0.5, genes to mutate = 40

*note that population and genotype size must be even

To change the name of the file where results will be printed, go to: handle_files.write_to_file("results/assignment03/exercise03/placeholder", final_text) and change "placeholder" to be desired file name.

// Example end

// Results
For Exercise 2:
Will print the Test number e.g. Test 0:, Test 1:... etc (from tests 0-9). By default, it will print the best individual's score and genotype (not including redundant moves) at each milestone (200k, 1 mill, 5 mill) for each test. Will also print the total number of generations iterated after 5 million calls to advance. After 10 tests have been run and printed, it will print the mean and standard deviation of scores at each milestone. Result files will be saved to results/assignment03/exercise02.

For Exercise 3:
Will print the Test number e.g. Test 0:, Test 1:... etc (from tests 0-9). By default, it will print population's hypervolume at each milestone. Will also print the total number of generations iterated after 5 million calls to advance. After 10 tests have been run and printed, it will print the mean and standard deviation of hypervolumes at each milestone. Result files will be saved to results/assignment03/exercise03.

**PLEASE NOTE FOR EXERCISE 2 AND 3:**
Due to the way the act() method within an Agent was implemented, it is impossible to know which game or level you are in inside of act(). Because of this, the result files may be difficult to read, especially after automating 4 games for 5 levels, there will be 20 sets of tests within a single text file. We were able to keep track of which tests corresponded to a certain game and level as the run controllers ran games and levels in order (from level 0-4, in the order of games within the int[] array). Sorry for any inconvenience.

For Exercise 4:


// Troubleshooting:
1-  Tests of the same name in the results folder will write to the bottom of the existing test instead of overwriting (e.g. if a test file named "Test.txt" already        exists and you're printing new results to "Test.txt", the new results will print at the bottom of the first "Test.txt" file).
2-  n-point-crossover in Exercise 2 and 3 may trigger an infinite loop if the crossover spacing and number of crossover points are unreasonably high with respect to the genotype size. A good rule-of-thumb is to multiply crossover spacing and crossover points, and make sure that value is ~100 less than the genotype size. e.g. for a genotype size of 300, make crossover spacing = 15 and crossover points = 13... 15x13 = 195 which is ~100 less than 300.
