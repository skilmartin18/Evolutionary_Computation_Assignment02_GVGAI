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

    // EX2: 
    Ex2_run_controller.java contains strings with the path of the different game controllers.
    To test a desired controller, change the 4th parameter in runGames. 
    L and M correspond to the number of levels, and number of tests to be done, respectively. 
    L is set to 5, and M to 10 for Ex2. 
    When run, the mean and standard deviation of the scores is output to text files that appear
    in the "results" directory.


    // EX3:


    // EX4: 


// Results
Results are returned in a text file that is made in the results sub-directory
in the format <Tsplibname>_<GA name>_<test#>_<popsize#>_<generations#>.
Will display the Generation number (writes every popsize/10 generations), the distances of 
the population memeber, will show the distance of the best performer (rounded up as int) and
finally the time elapsed at that point. InvrOvr will also write the amount of inversions at the time of
reporting.

// Troubleshooting:
1-  If having a error from reading the TSPlib files can go to line 11 of TSPproblem and follow Instructions
    there, although shouldn't be an issue unless running an older version wthout access to pathlib. 
2-  Tests of the same name in the results folder will be overwritten, multiple of the same test i.e 
    30x(eil51,10 5000) should all be done in a single BenchmarkParameters file to avoid this.

