package evo_exercises.Ex3_EA_controller;
import java.util.Random;
import tools.Utils;
import tracks.ArcadeMachine;

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
        int gameIdx = 11; 
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
        double child_genotype[] = new double[5];

        /*
            EA STUFF
                        */

        // EA variables
        double sigma = 1;
        int num_gen = 10;
        double scores[] = new double[5];
        Random gaussian = new Random();
        double parent_score = 0;
        double current_score = 0;
        int mutation_success = 0;
        int k = 10;
        double p_s = 0;
        double c = 0.8;

        /*
            RUN INITIAL PARENT
                                */
        
        for(int lvl = 0; lvl < 5; lvl++)
        {
            String level1 = game.replace(gameName, gameName+"_lvl"+lvl);

            double temp[] = ArcadeMachine.runOneGame(game, level1, false, sampleGAController, null, seed, 0);

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
            for ( int i = 0; i < 5; i++)
            {
                // next gaussian returns number from N(0,1), to make with our stddev multiply by sigma
                child_genotype[i] = parent_genotype[i] + gaussian.nextGaussian()*sigma;
            }


            // evaluate child across all levels of a single game
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
                parent_genotype = child_genotype;
                parent_score = current_score;
                mutation_success++;
            }


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

        System.out.println(parent_genotype);
    }
}