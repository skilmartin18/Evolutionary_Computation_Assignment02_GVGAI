package evo_exercises.Ex3_EA_controller;
import java.util.Random;
import tools.Utils;
import tracks.ArcadeMachine;
import java.lang.*;

public class Ex3_optimise_GA 
{
    public static void main(String[] args) 
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
        double parent_genotype[] = new double[]{0.9, 7, 5, 0.1, 0.142};
        //double parent_genotype[] = new double[]{1.1342409097819355, 4.0, 3.0, 0.3738103633107982, 0.25};
        double child_genotype[] = new double[5];

        /*
            EA STUFF
                        */

        // EA variables
        double sigma = 1;
        int num_gen = 50;
        double scores[] = new double[5];
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        int mutation_success = 0;
        int k = 5;
        double p_s = 0;
        double c = 0.8;

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
            System.out.print("GENERATION ");
            System.out.print(gen+1);
            System.out.print(": \n");
            // make child
                // next gaussian returns number from N(0,1), to make with our stddev multiply by sigma

                // Index 0, GAMMA
                child_genotype[0] = Math.abs(parent_genotype[0]  + gaussian.nextGaussian()*sigma);

                // Index 1, SIM DEPTH   
                do {
                    child_genotype[1] = Math.floor(Math.abs(parent_genotype[1]  + gaussian.nextGaussian()*sigma));
                } while (child_genotype[1] < 1);

                // INDEX 2, POP SIZE
                do {
                    child_genotype[2] = Math.floor( Math.abs(parent_genotype[2]  + gaussian.nextGaussian()*sigma) );
                } while (child_genotype[2] < 3);

                // Index 3, RECPROB
                child_genotype[3] = Math.abs(parent_genotype[3]  + gaussian.nextGaussian()*sigma);
                child_genotype[3] = child_genotype[3]%1;
               
                // mut is 1/sim_depth
                child_genotype[4] = 1/child_genotype[1];

            // evaluate child across all levels of a single game
            System.out.print("Curent individual genotype: ");
            for (int i=0; i < 5; i++) {
                System.out.print(child_genotype[i]);
                System.out.print(" ");
            }
            System.out.print("\n");
            

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
                parent_genotype = child_genotype;
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
                    sigma = sigma/c;
                }
                if ( p_s < 0.20)
                {
                    sigma = sigma*c;
                }

            }

        }

        System.out.print("Final parent genotype: ");
        for (int i=0; i < 5; i++) {
            System.out.print(parent_genotype[i]);
            System.out.print(" ");
        }
        System.out.print("\n");

        System.out.print("Final child genotype: ");
        for (int i=0; i < 5; i++) {
            System.out.print(child_genotype[i]);
            System.out.print(" ");
        }
        System.out.print("\n");
    }
}