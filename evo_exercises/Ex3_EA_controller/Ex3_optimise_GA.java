package evo_exercises.Ex3_EA_controller;
import java.util.Random;

//import org.graalvm.compiler.core.common.type.ArithmeticOpTable.UnaryOp.Sqrt;

import tools.Utils;
import tracks.ArcadeMachine;
import java.lang.*;

public class Ex3_optimise_GA 
{
    public static void main(String[] args) 
    {
        optimise_GA2();
        return;
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
        double sigmas[] = {0.2, 2, 2, 0.05};
        double minSigmas[] = {0.2, 2, 2, 0.05};
        int num_gen = 50;
        double scores[] = new double[5];
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        double tauPrime = 1 / Math.sqrt(2*4) ; 
        double tau = 1 / ( Math.sqrt(2 * Math.sqrt(4) ) );

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
        based on slide 18 of slide set 3
                                        */

        for (int gen = 0; gen < num_gen; gen++) 
        {
            
            // performing calculations of new sigmas and making child with correct parameters
            double N = gaussian.nextGaussian();
            for(int i = 0; i < 3; i++){

                double Ni = gaussian.nextGaussian();
                sigmas[i] = sigmas[i] * Math.exp( tauPrime*N + tau*Ni );
                
                // if new sigma calculated is below threshold value, the set it to the threshold value
                if ( sigmas[i] < minSigmas[i] ){
                    sigmas[i] = minSigmas[i];
                }

                // new child
                
                /* 
                    Makes sure certain parameters are bounded
                */
                
                // gamma
                if ( i == 0 ){
                    child_genotype[i] = parent_genotype[i] + sigmas[i]*Ni;
                    if ( child_genotype[i] < 0 ){
                        child_genotype[i] = 0;
                    }

                // simulation depth
                }else if ( i == 1 ){
                    child_genotype[i] = Math.floor(parent_genotype[i] + sigmas[i]*Ni);
                    if ( child_genotype[i] < 1 ){
                        child_genotype[i] = 1;
                    }

                // population size
                }else if ( i == 2 ){
                    child_genotype[i] = Math.floor(parent_genotype[i] + sigmas[i]*Ni);
                    if ( child_genotype[i] < 3 ){
                        child_genotype[i] = 3;
                    }else if ( child_genotype[i] > 5 ){
                        child_genotype[i] = 5;
                    }

                // recprob
                }else if ( i == 3 ){
                    child_genotype[i] = parent_genotype[i] + sigmas[i]*Ni;
                    if ( child_genotype[i] < 0 ){
                        child_genotype[i] = 0;
                    }else if ( child_genotype[i] >= 1 ){
                        child_genotype[i] = 0.99;
                    }
                }
            }
            
            // mut is calculated based off simulation depth
            child_genotype[4] = 1/child_genotype[1];

            // evaluate child across all levels of a single game
            System.out.print("Curent parent genotype: ");
            for (int i=0; i < 5; i++) {
                System.out.print(parent_genotype[i]);
                System.out.print(" ");
            }
            
            // calculates score for child
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
            } else 
            System.out.println("Parent not replaced...");

            current_score = 0;

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
    // public static void Ex3_test_GA()
    // {
    //     // GA controller
    //     String sampleGAController = "tracks.singlePlayer.deprecated.sampleGA.Agent";

    //     // Set up the 4 game indexes and levels to be played
        
    //     // Set up the new GA paramaters for each game (calculated by the EA)

    //     // Run the GA with new parameters 
    //         // Run each level in eahc game 10 times
    //         // Get the mean and std deviation of scores


    //     double temp[] = ArcadeMachine.runOneGameGA(game, level1, false, sampleGAController, null, seed, 0, parent_genotype);

    // }
    
    