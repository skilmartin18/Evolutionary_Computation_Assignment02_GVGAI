package evo_exercises.Ex3_EA_controller;
import java.util.ArrayList;
import java.util.Random;

//import org.graalvm.compiler.core.common.type.ArithmeticOpTable.UnaryOp.Sqrt;

import tools.Utils;
import tracks.ArcadeMachine;
import java.lang.*;
import tools.StatSummary;

// Import file handler for outputs
import handle_files.handle_files;


public class Ex3_optimise_GA 
{
    public static void main(String[] args) 
    {
        // use to run the optimiser to obtain parameter values or actually test those parameter values

        //optimise_GA();
        Ex3_test_GA();
        return;
    }

    //
    // Test the best genotypes
    //
    public static void Ex3_test_GA()
    {
        // Set up the 4 best genotypes 
        double aliens_genotype[] = new double[]{0.7684580278057679, 2.0, 5.0, 0.593744114715972, 0.5}; 
        double boulderdash_genotype[] = new double[]{3.2333020613515053, 1.0, 5.0, 0.99, 1.0}; 
        double chase_genotype[] = new double[]{6.091106410265279, 1.0, 5.0, 0.5777935041605069, 1.0};
        double butterflies_genotype[] = new double[]{0.01, 7.0, 3.0, 0.8863864166517196, 1/7};

        // Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

        // set level params **** MAKE SURE GENOTYPE USED IS FOR THE RIGHT GAMEINDEX
        int gameIdx = 13;  
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];

        // GA controller
        String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";

        // Set seed
        int seed = new Random().nextInt();
        
        // Number of runs for each game
        int M = 10; 

        // Text to write to file
        String text = "";
        
        /*
             Play all 5 levels of the specified game
                                                        */

        for(int lvl = 0; lvl < 5; lvl++)
        {
            // Set up output filename
            String filename = "results/exercise03/" + gameName + "_" + "lvl" + lvl + "_" + M;

            // Set up level to pass to runOneGameGA
            String level = game.replace(gameName, gameName+"_lvl"+lvl);

            // Create stat summary object to compute mean and std dev
            StatSummary scores = new StatSummary(); // set to 1 because we only have one player (the GA)
            text = "";

            
            // Run each game level M times
            for (int i=0; i<M; i++) {

                // Run game with given genotype
                double temp[] = ArcadeMachine.runOneGameGA(game, level, false, sampleGAController, null, seed, 0, butterflies_genotype);
                
                // Put score into stat summary running tally
                scores.add(temp[1]);
                text += "Score on run " + (i+1) + ": " + temp[1] + "\n";
            }

            // After running 10 games, get mean and sd for the level
            double mean = scores.mean(); 
            double sd = scores.sd();

        

            // Output values
            text += "\nMEAN: " + mean;
            text += ", STD DEV: " + sd;
            handle_files.write_to_file(filename, text);
            text = "";
            
        }
            
        
    }


    //
    // Obtains optimised parameter settings
    //
    public static void optimise_GA()
    {
        /*
            ARCADE MACHINE STUFF
                                    */

        // Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

        // set level params
<<<<<<< HEAD
        int gameIdx = 13; 
=======
        int gameIdx = 11;
>>>>>>> 35f71a0972ec0b91c6e4e5aa55fc1807f6eddaa1
		String gameName = games[gameIdx][1];
        System.out.println("Gamename is " + gameName);
		String game = games[gameIdx][0];

        // seed
        int seed = new Random().nextInt();

        // controller

        String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";

        /*
            GA/EA STUFF
                                */

        // genotype as {GAMMA,SIM_DEPTH,POP_SIZE,RECPROB,MUT}
        ArrayList<double[]> parent_pop = new ArrayList<double[]>();
        ArrayList<double[]> child_pop = new ArrayList<double[]>();
        double child_genotype[] = new double[5];
        double sigmas[] = {0.3, 3, 3, 0.3};
        double minSigmas[] = {0.15, 3, 3, 0.09};
<<<<<<< HEAD
        int num_gen = 200;
        double scores[] = new double[2];
=======
        ArrayList<double[]> sigmasList = new ArrayList<double[]>();
        int num_gen = 200;
        int population_size = 6;
        int number_levels = 3;
        double scores[] = new double[number_levels];
>>>>>>> 35f71a0972ec0b91c6e4e5aa55fc1807f6eddaa1
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        double tauPrime = 1 / Math.sqrt(2*4) ; 
        double tau = 1 / ( Math.sqrt(2 * Math.sqrt(4) ) );
        double parent_scores[] = new double[population_size];
        double child_scores[] = new double [population_size];

        /*
            CREATION OF INITIAL PARENT POPULATION AND STANDARD DEVIATION LIST
                                */

        double parent_genotype[] = new double[]{0.9, 7, 5, 0.1, 0.142};
        for ( int j = 0; j < population_size; j++){
            parent_pop.add(parent_genotype);
        }

        for ( int j = 0; j < population_size; j++){
            sigmasList.add(sigmas);
        }

        /*
            RUNNING PARENT POPULATION TO OBTAIN SCORES
                                */

        // outside for loop runs all individual parent genotypes 
        for( int i = 0; i < population_size; i++){

            // inside for loop runs an individual parent genotype three times for same level and records total score
            for ( int j = 0; j < number_levels; j++ ){
                String level1 = game.replace(gameName, gameName+"_lvl1");
                double temp[] = ArcadeMachine.runOneGameGA(game, level1, false, sampleGAController, null, seed, 0, parent_pop.get(i));
            
                scores[j] = temp[1];
                parent_score += scores[j];
            }

            parent_scores[i] = parent_score;
            parent_score = 0;
        }
    
        /*
            RUN EA
            based on slide 18 of slide set 3
                                            */

        for (int gen = 0; gen < num_gen; gen++) 
        {
            // prints out generation
            System.out.println("GENERATION " + (gen+1) + ":\n");
<<<<<<< HEAD
            // performing calculations of new sigmas and making child with correct parameters
            double N = gaussian.nextGaussian();
            for(int i = 0; i < 4; i++){
=======
>>>>>>> 35f71a0972ec0b91c6e4e5aa55fc1807f6eddaa1

            // prints out genotype of each parent
            System.out.println("PARENT GENOTYPES: ");
            for ( int i = 0; i < population_size; i++){

                double parent[] = parent_pop.get(i);
                for ( int j = 0; j < 4; j++){
                    System.out.print( parent[j] + " ");
                }
                System.out.println( parent[4] );
                
            }
            System.out.print("\n");

            // prints out parent mean scores
            System.out.println("PARENT MEAN SCORES: ");
            for ( int i = 0; i < population_size - 1; i++){
                System.out.print(parent_scores[i]/number_levels);
                System.out.print(" ");
            }
            System.out.print(parent_scores[population_size-1]/number_levels + "\n");
        

            // performs recombination of parents to produce a child population of the same size as that of the parents
            for ( int j = 0; j < population_size; j += 2){

                double parent1[] = parent_pop.get(j);
                double parent2[] = parent_pop.get(j+1);

                for ( int i = 0; i < 2; i ++){

                    // initialising required variables
                    double random_num = Math.floor( Math.random() + 0.5 );
                    double new_child[] = new double[5];

                    // runs through each parent to create child genotype
                    for ( int k = 0; k < 5; k++){

                        // average parent values
                        if ( random_num == 0 ){
                            double average = ( parent1[k] + parent2[k] ) / 2;
                            new_child[k] = average;

                        // select one of parent values to go in child
                        }else if ( random_num == 1){
                            double random_num2 = Math.floor( Math.random() + 0.5 );

                            if ( random_num2 == 0 ) new_child[k] = parent1[k];
                            else if ( random_num2 == 1 ) new_child[k] = parent2[k];
                        }

                    }

                    child_pop.add(new_child);
                }
            }

            // mutates all children
            double N = gaussian.nextGaussian();
            for(int j = 0; j < population_size; j++){

                // setting correct child genotype and sigmas list
                child_genotype = child_pop.get(j);
                sigmas = sigmasList.get(j);

                // performing calculations of new sigmas and making child with correct parameters
                for(int i = 0; i < 4; i++){

                    double Ni = gaussian.nextGaussian();
                    sigmas[i] = sigmas[i] * Math.exp( tauPrime*N + tau*Ni );
                    
                    // if new sigma calculated is below threshold value, the set it to the threshold value
                    if ( sigmas[i] < minSigmas[i] ){
                        sigmas[i] = minSigmas[i];
                    }
                    
                    // gamma
                    if ( i == 0 ){
                        child_genotype[i] = child_genotype[i] + sigmas[i]*Ni;
                        if ( child_genotype[i] <= 0 ){
                            child_genotype[i] = 0.01;
                        }

                    // simulation depth
                    }else if ( i == 1 ){
                        child_genotype[i] = Math.floor(child_genotype[i] + sigmas[i]*Ni);
                        if ( child_genotype[i] < 1 ){
                            child_genotype[i] = 1;
                        }else if ( child_genotype[i] > 21 ){
                            child_genotype[i] = 20;
                        }

                    // population size
                    }else if ( i == 2 ){
                        child_genotype[i] = Math.floor(child_genotype[i] + sigmas[i]*Ni);
                        if ( child_genotype[i] < 3 ){
                            child_genotype[i] = 3;
                        }else if ( child_genotype[i] > 5 ){
                            child_genotype[i] = 5;
                        }

                    // recprob
                    }else if ( i == 3 ){
                        child_genotype[i] = child_genotype[i] + sigmas[i]*Ni;
                        if ( child_genotype[i] < 0 ){
                            child_genotype[i] = 0;
                        }else if ( child_genotype[i] >= 1 ){
                            child_genotype[i] = 0.99;
                        }
                    }
                }
                
                // mut is calculated based off simulation depth
                child_genotype[4] = 1/child_genotype[1];


                // replacing current child genotype and sigmas lists with mutated versions
                sigmasList.set(j,sigmas);
                child_pop.set(j,child_genotype);
            }
            
            // outside for loop runs all individual child genotypes 
            current_score = 0 ;
            for( int i = 0; i < population_size; i++){

                // inside for loop runs an individual child genotype twice for same level and records score
                for ( int j = 0; j < number_levels; j++ ){
                    String level1 = game.replace(gameName, gameName+"_lvl1");
                    double temp[] = ArcadeMachine.runOneGameGA(game, level1, false, sampleGAController, null, seed, 0, child_pop.get(i));
                
                    scores[j] = temp[1];
                    current_score += scores[j];
                }

                child_scores[i] = current_score;
                current_score = 0;
            }

            // prints out child mean scores
            System.out.println("CHILD MEAN SCORES: ");
            for ( int i = 0; i < population_size - 1; i++){
                System.out.print(child_scores[i]/number_levels);
                System.out.print(" ");
            }
            System.out.println(child_scores[population_size-1]/number_levels + "\n");        

            // greedy select
            for ( int i = 0; i < population_size; i++){
                current_score = child_scores[i];
                parent_score = parent_scores[i];

                if ( current_score > parent_score)
                {
                    parent_pop.set(i,child_pop.get(i));
                    parent_scores[i] = child_scores[i]; 
                }
            }

            // makes child population empty so new one can be made for each generation
            child_pop.clear();
           
        }
        
    }
    
}
    
    
   