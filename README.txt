# Evolutionary_Computation_Assignment02
// Git Repo for the second assignment of Evolutionary Computation. 
// University of Adelaide 2021 S2

// Group Members:

Sebastian Kilmartin a1774638 a1774638@student.adelaide.edu.au
Michael Lazaros a1773775 a1773775@student.adelaide.edu.au
Fouad Nehme Badanai a1774241 a1774241@student.adelaide.edu.au
Luke Bruno a1774453 a1774453@student.adelaide.edu.au
Jedidiah Kurtzer a1774259@student.adelaide.edu.au

// Code Instructions: 
The code is run through the files located in the evo_exercises directory. Here, there are 
subdirectories corresponding to exercises 2,3 and 4. All code is run in vscode, by pressing the 
automated run button. This button simultaneously compiles and runs the java file. 

    EX2: 
    Ex2_run_controller.java contains strings with the path of the different game controllers.
    To test a desired controller, change the 4th parameter in runGames. 
    L and M correspond to the number of levels, and number of tests to be done, respectively. 
    L is set to 5, and M to 10 for Ex2. 
    When run, the mean and standard deviation of the scores is output to text files that appear
    in the "results" directory.


    EX3:
    Ex3_optimise_GA.java is run in a similar way to Ex2. 
    Modify the main function, depending on whether it is desired to optimise GA parameters, 
    or to test the obtained parameters. Both the optimiser and testing functions are defined 
    within the same java file. 
    In the optimise function, initial sigma values are defined as per the file.
    However, new initial values can be input to alter the outcome of the optimisation.
    After some trial and error and iteration, the current sigma values were deemed sufficient.


    EX4: 
    The ex4 directory contains an individual class, that defines the genotype, and
    also contains the Agent.java file that defines how the controller evolved individuals. 
    Ex4 is not run from within these files. Instead, the path to ex4 is defined in Ex2's java file, 
    as "diyGA." Tests on this diy controller are run in the same fashion as Ex2. 


// Results:
Results are returned in text files, in the results directory. Relevant subdirectories exists to 
categorsie the results. The means and standard deviation results are produced using the 
StatSummary class provided with the GVGAI code database. The StatSummary provides a simple
way of tracking the scores from multiple game runs and calculating desired results. 

