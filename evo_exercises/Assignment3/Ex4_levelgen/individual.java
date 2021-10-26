package evo_exercises.Assignment3.Ex4_levelgen;



import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import core.game.Event;
import core.game.GameDescription.SpriteData;
import core.game.GameDescription.TerminationData;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import evo_exercises.Ex4_diy_GA.Agent;
import tracks.levelGeneration.constraints.CombinedConstraints;
import ontology.Types;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import tools.LevelMapping;
import tools.StepController;
import java.util.*;

import core.player.AbstractPlayer;
import handle_files.handle_files;

public class individual {

    /*   
         VAR DECS
                     */

    // premade map length is 14w*11h
    int playable_width = 18-2;
    int playable_height = 12-2;

    // placing optionals i.e optional locks or enemies
    double optional_chance = 0.5;
    double average_optionals = 3;

    // random generator
    public Random rand = new Random();

    // genotype as 1d array for easy crossover, access via 
    // genotype[x+y*width]
    char[] genotype = new char[(playable_width)*(playable_height)];

    // fitness stuff
    int fitness = 0;
    
   
    /*   
         LEVEL MAPPINGS
                          */

    // placeables, choices must be unique and present to play the game
    // optional are optional, chosen for an individual via the
    // 'optional chance' var- different sections for different games

    // cookmepasta
    // char[] choices = {'b','p','o','t','A'};
    // char[] optional = {'k','l'};

    //zelda
    char[] choices = {'g','+','A'};
    char[] optional = {'1','2','3'};
    


    /*   
        WALL GEN
                    */

    // random wall placing param, to start with good individuals
    // can place sections of wall rather than single walls 
    // however that is making this too similar to a level generator 
    // in and of itself, the GA would be useless
    int maximum_wall_length = 6; // unused currently
    int average_wall_number = 12;
    double wall_prob = 0.5;
    double horizontal_wall_chance = 0.5; // unused currently


    /*   
         CONSTRUCTOR FROM EXISTING GENOTYPE
                                             */
    
    public individual(char[] _genotype)
    {
        genotype = _genotype.clone();
    }

    /*   
         CONSTRUCTOR NO INPUTS
                                 */

    public individual()
    {
        // create random genotype
        // initialise genotype as all floors
        for(int i = 0; i < genotype.length; i++)
        {
            genotype[i] = '.';
        }


        ////// PLACE UNIQUE OBJECTS //////

        // place unique objects i.e choices 
        for( char choice:choices)
        {
            int x_rand = rand.nextInt(playable_width-1);
            int y_rand = rand.nextInt(playable_height-1);

            // dont place unique objects on themselves
            while(genotype[x_rand+y_rand*playable_width] != '.' )
            {
                // (zero referenced array so x/y start at 0)
                x_rand = rand.nextInt(playable_width-1); 
                y_rand = rand.nextInt(playable_height-1);
            }

            // place the element
            genotype[x_rand+y_rand*playable_width] = choice;     
        }


        ////// PLACE OPTIONALS //////

        // try to place average_optional*1/optional_chance optionals, should average at 
        // average_optional, with optionally double that, or none.
        for(int i = 0; i < average_optionals*(1/optional_chance); i++)
        {
            // place random optional from list (if rand>optional_chance)
            if(rand.nextDouble()>optional_chance)
            {
                    int x_rand = rand.nextInt(playable_width-1);
                    int y_rand = rand.nextInt(playable_height-1);

                    while(genotype[x_rand+y_rand*playable_width] != '.' )
                    {
                        x_rand = rand.nextInt(playable_width-1);
                        y_rand = rand.nextInt(playable_height-1);
                    }

                    genotype[x_rand+y_rand*playable_width] = optional[rand.nextInt(optional.length)];  
                
            }
        }


        ////// WALL GEN //////

        // add random single walls 
        for ( int i = 0; i < average_wall_number*(1/wall_prob); i++)
        {
            // place wall?
            if ( rand.nextDouble() > wall_prob )
            {
                    // wall start
                    int x_rand = rand.nextInt(playable_width-1);
                    int y_rand = rand.nextInt(playable_height-1);

                    //place the wall (if something else there, dont place)
                    if(genotype[x_rand+y_rand*playable_width]=='.')
                    {
                        genotype[x_rand+y_rand*playable_width] = 'w';
                    }
            }
        }

        ////// LEGACY LONG WALL CODE //////


        // // add walls in contiguous chunks
        // for ( int i = 0; i < average_wall_number*(1/wall_prob); i++)
        // {
        //     // place wall?
        //     if ( rand.nextDouble() > wall_prob )
        //     {
        //         // how long is wall?
        //         int wall_len = rand.nextInt(maximum_wall_length)+1;

        //         // horizontal?
        //         if ( rand.nextDouble() > horizontal_wall_chance)
        //         {
        //             // wall start
        //             int x_rand = rand.nextInt(playable_width-1);
        //             int y_rand = rand.nextInt(playable_height-1);

        //             for(int j = 0; j < wall_len; j++)
        //             {
        //                 // dont place outside bounds
        //                 if(x_rand == playable_width)
        //                 {
        //                     break;
        //                 }

        //                 // place the wall
        //                 if(genotype[x_rand+y_rand*playable_width]=='.')
        //                 {
        //                     genotype[x_rand+y_rand*playable_width] = 'w';
        //                 }

        //                 // move next
        //                 x_rand++;

        //             }
        //         }
        //         else //vertical
        //         {
        //             // wall start
        //             int x_rand = rand.nextInt(playable_width-1);
        //             int y_rand = rand.nextInt(playable_height-1);

        //             for(int j = 0; j < wall_len; j++)
        //             {
        //                 // dont place outside bounds
        //                 if(y_rand == playable_height)
        //                 {
        //                     break;
        //                 }

        //                 // place the wall
        //                 if(genotype[x_rand+y_rand*playable_width]=='.')
        //                 {
        //                     genotype[x_rand+y_rand*playable_width] = 'w';
        //                 }

        //                 // move next
        //                 y_rand++;
        //             }
        //         }
        //     }
        // }
    }

    public boolean calc_disqual(AbstractPlayer automatedAgent, StateObservation stateObs)
    {
        boolean disqual = false;
        
        /* /// EVERYTHING IS PRESENT /// (legacy)
        // this should be checked by the next part anyway
        //int choice_count = 0;
        // // disqualification factor 1-> are all choices present
        // for(int i = 0; i < genotype.length; i++)
        // {
        //     if( (genotype[i] == 'g') || (genotype[i] == '+') || (genotype[i] == 'A') )
        //     {
        //         choice_count++;
        //     }
        // }

        // if ( choice_count < 3)
        // {
        //     disqual = true;
        // }
        */
        
        ///// LEVEL IS COMPLETEABLE //////
        /// Play the game using the best agent, copied from SampleGA

        StepController stepAgent = new StepController(automatedAgent, 40);
        ElapsedCpuTimer elapsedTimer = new ElapsedCpuTimer();
        elapsedTimer.setMaxTimeMillis(40);
        stepAgent.playGame(stateObs.copy(), elapsedTimer);
        
        // gets end state of game
        StateObservation bestState = stepAgent.getFinalState();
        // ArrayList<Types.ACTIONS> bestSol = stepAgent.getSolution();

        // if the player doesnt win i.e loses or cannot win
        if( (bestState.getGameWinner() == Types.WINNER.PLAYER_LOSES) || (bestState.getGameWinner() == Types.WINNER.NO_WINNER) )
        {
            disqual = true;
        }

        return disqual;
    }

    public void calc_fitness(AbstractPlayer automatedAgent, StateObservation stateObs)
    {
        if(!calc_disqual(automatedAgent, stateObs))
        {
            System.out.println("i can play the level yay");
        }

    }



}
