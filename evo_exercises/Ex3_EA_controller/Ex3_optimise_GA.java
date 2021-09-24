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
        optimise_GA2();
        return;
    }

    //
    // Test the best genotypes
    //
    public static void Ex3_test_GA()
    {
        // Set up the 4 best genotypes 
        double aliens_genotype[] = new double[]{ 0.9, 7, 5, 0.1, 0.142};
        double boulderdash_genotype[] = new double[]{ 0.9, 7, 5, 0.1, 0.142};
        double butterflies_genotype[] = new double[]{ 0.9, 7, 5, 0.1, 0.142};
        double chase_genotype[] = new double[]{ 0.9, 7, 5, 0.1, 0.142};

        // Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

        // set level params **** MAKE SURE GENOTYPE USED IS FOR THE RIGHT GAMEINDEX
        int gameIdx = 0; 
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

            // Run each game level M times
            for (int i=0; i<M; i++) {

                // Run game with given genotype
                double temp[] = ArcadeMachine.runOneGameGA(game, level, false, sampleGAController, null, seed, 0, aliens_genotype);
                 
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
        }
    }

    public static void optimise_GA2()
    {
        /*
            ARCADE MACHINE STUFF
                                    */

        // Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

        // set level params
        int gameIdx = 0; 
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
        ArrayList<double[]> sigmasList = new ArrayList<double[]>();
        int num_gen = 50;
        int population_size = 6;
        double scores[] = new double[2];
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        double tauPrime = 1 / Math.sqrt(2*4) ; 
        double tau = 1 / ( Math.sqrt(2 * Math.sqrt(4) ) );

        /*
            CREATION OF INITIAL PARENT POPULATION AND STANDARD DEVIATION LISTS
                                */

        double parent_scores[] = new double[population_size];
        double child_scores[] = new double [population_size];

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

            // inside for loop runs an individual parent genotype twice for same level and records score
            for ( int j = 0; j < 2; j++ ){
                String level1 = game.replace(gameName, gameName+"_lvl1");
                double temp[] = ArcadeMachine.runOneGameGA(game, level1, false, sampleGAController, null, seed, 0, parent_pop.get(i));
            
                scores[j] = temp[1];
                parent_score += scores[j];
            }

            parent_scores[i] = parent_score;
            parent_score = 0;
        }
    
        /*
        RUN EA (single pop) 
        based on slide 18 of slide set 3
                                        */

        for (int gen = 0; gen < num_gen; gen++) 
        {
            // prints out generation
            System.out.println("GENERATION " + (gen+1) + ":\n");

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
                System.out.print(parent_scores[i]/2);
                System.out.print(" ");
            }
            System.out.print(parent_scores[population_size-1]/2 + "\n");
        

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
                        if ( child_genotype[i] < 0 ){
                            child_genotype[i] = 0;
                        }

                    // simulation depth
                    }else if ( i == 1 ){
                        child_genotype[i] = Math.floor(child_genotype[i] + sigmas[i]*Ni);
                        if ( child_genotype[i] < 1 ){
                            child_genotype[i] = 1;
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
                for ( int j = 0; j < 2; j++ ){
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
                System.out.print(child_scores[i]/2);
                System.out.print(" ");
            }
            System.out.print(child_scores[population_size-1]/2 + "\n");        

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

    public static void optimise_GA()
    {
          /*
            ARCADE MACHINE STUFF
                                    */

        // Load available games
		String spGamesCollection =  "examples/all_games_sp.csv";
		String[][] games = Utils.readGames(spGamesCollection);

        // set level params
        int gameIdx = 0; 
		String gameName = games[gameIdx][1];
		String game = games[gameIdx][0];

        // seed
        int seed = new Random().nextInt();

        // controller

        String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";

        /*
            GA STUFF
                        */

        // genotype as {GAMMA,SIM_DEPTH,POP_SIZE,RECPROB,MUT}
        double parent_genotype[] = new double[]{ 0.9, 7, 5, 0.1, 0.142};
        //double parent_genotype[] = new double[]{1.1342409097819355, 4.0, 3.0, 0.3738103633107982, 0.25};
        double child_genotype[] = new double[5];

        /*
            EA STUFF
                        */

        // EA variables
        double sigma = 1;
        double sigmas[] = {0.2, 2, 2, 0.05};
        int num_gen = 10;
        double scores[] = new double[5];
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        int mutation_success = 0;
        int k = 10;
        double p_s = 0;
        double c = 0.8;
        long initial_time = 0;
        long current_time = 0;

        /*
            RUN INITIAL PARENT
                                */
        
        for(int lvl = 0; lvl < 5; lvl++)
        {
            String level1 = game.replace(gameName, gameName+"_lvl"+lvl);

            double temp[] = ArcadeMachine.runOneGameGA(game, level1, false, sampleGAController, null, seed, 0, parent_genotype);

            scores[lvl] = temp[1];
            parent_score += scores[lvl];
        }

        
        /*
            RUN EA (single pop) 
            based on slide 7 of slide set 3
                                            */



        for (int gen = 0; gen < num_gen; gen++) 
        {
            
            // make child
                // next gaussian returns number from N(0,1), to make with our stddev multiply by sigma

                // Index 0, GAMMA
                child_genotype[0] = Math.abs(parent_genotype[0]  + gaussian.nextGaussian()*sigmas[0]);

                // Index 1, SIM DEPTH   
                initial_time = System.currentTimeMillis();
                do {
                    child_genotype[1] = Math.floor(Math.abs(parent_genotype[1]  + gaussian.nextGaussian()*sigmas[1]));

                    //break this loop if taking too long, honestly dont even need the loop, if it fails just hardcode it...
                    current_time = System.currentTimeMillis();
                    if ((current_time-initial_time) > 5000)
                    {
                        child_genotype[1] = 1;
                        break;
                    }
                } while (child_genotype[1] < 1);

                // INDEX 2, POP SIZE
                initial_time = System.currentTimeMillis();
                do {
                    child_genotype[2] = Math.floor( Math.abs(parent_genotype[2]  + gaussian.nextGaussian()*sigmas[2]) );
                    current_time = System.currentTimeMillis();
                    // loop break
                    if ((current_time-initial_time) > 5000)
                    {
                        child_genotype[2] = 3;
                        break;
                    }
                } while (child_genotype[2] < 3);

                // Index 3, RECPROB
                child_genotype[3] = Math.abs(parent_genotype[3]  + gaussian.nextGaussian()*sigmas[3]);
                child_genotype[3] = child_genotype[3]%1;
               
                // mut is 1/sim_depth
                child_genotype[4] = 1/child_genotype[1];

            // evaluate child across all levels of a single game
            System.out.print("Curent parent genotype: ");
            for (int i=0; i < 5; i++) {
                System.out.print(parent_genotype[i]);
                System.out.print(" ");
            }
            

            for(int lvl = 0; lvl < 5; lvl++)
            {
                String level = game.replace(gameName, gameName+"_lvl"+lvl);

                double temp[] = ArcadeMachine.runOneGameGA(game, level, false, sampleGAController, null, seed, 0, child_genotype);

                scores[lvl] = temp[1];
                current_score += scores[lvl];
            }

            // greedy select

            if ( current_score > parent_score)
            {
                System.out.println("Parent being replaced...");
                parent_genotype = child_genotype.clone();
                parent_score = current_score;
                mutation_success++;
            } else 
            System.out.println("Parent not replaced...");


            current_score = 0;

            // 1 in 5 rule
            if ( (gen%k == 0) && (gen!=0) )
            {
                p_s = mutation_success/k;

                if ( p_s > 0.20 )
                {
                    sigmas[0] = sigmas[0]/c;
                    sigmas[1] = sigmas[1]/c;
                    sigmas[2] = sigmas[2]/c;
                    sigmas[3] = sigmas[3]/c;
                }
                if ( p_s < 0.20)
                {
                    sigmas[0] = sigmas[0]*c;
                    sigmas[1] = sigmas[1]*c;
                    sigmas[2] = sigmas[2]*c;
                    sigmas[3] = sigmas[3]*c;
                }

            }

        }

    }
    
}
    
    
    