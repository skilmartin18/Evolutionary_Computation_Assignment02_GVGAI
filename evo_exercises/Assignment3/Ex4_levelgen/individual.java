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


    /*
        FITNESS CALCULATIONS
                              */


    ////// CALC DISQUAL //////
    // will return a boolean whether this current individual is completely infeasible,
    // due to either not containing all relevant tiles- i.e avatar and choices
    // or due to the best player not being able to complete the level.
    public boolean calc_disqual(AbstractPlayer automatedAgent, StateObservation stateObs)
    {
        // by default we will disqualify the level
        boolean disqual = true;
        
        
        ///// LEVEL IS COMPLETEABLE //////
        // Play the game using the best agent, similar to the sampleGA
        // method, but ive made it much simpler- no idea why theirs was so complex

        // create new agent to play game
        StepController stepAgent = new StepController(automatedAgent, 40);
        ElapsedCpuTimer elapsedTimer = new ElapsedCpuTimer();
        elapsedTimer.setMaxTimeMillis(5000);

        /// run a few times incase it doesnt win 100% of the time
        /// running twice doubles computational overhead, so until 
        /// we find infeasible levels passing through, we will run once
        int game_num = 1;

        for ( int i = 0; i < game_num; i++)
        {
            stepAgent.playGame(stateObs.copy(), elapsedTimer);
        
            // gets end state of game
            StateObservation bestState = stepAgent.getFinalState();
            
            // if the player wins then dont disqualify
            if( (bestState.getGameWinner() == Types.WINNER.PLAYER_WINS) )
            {
                System.out.println("i can win this level");
                disqual = false;
            }

        }
        

        return disqual;
    }

    ////// WALL BASED FITNESS //////
    // Testing a fitness method which promotes long chains of walls,
    // hopefully this can lead to complex levels with long walls that need
    // to be traversed, or even rooms
    public int calc_wall_fitness()
    {
        int score = 0;
        int x,y, adjacent_walls;
        // iterate through the level to look at all tiles-
        // iteration in terms of x and y
        for ( y = 0; y < playable_height; y++)
        {
            for ( x = 0; x < playable_width; x++ )

                // have we encountered a wall?
                // addressing the genotype with the x+y*width method- need to check
                if ( genotype[x+y*playable_width] == 'w')
                {
                    adjacent_walls = 0;

                    // now we are looking at a wall, to promote wall growth
                    // extra fitness given to walls with adjacent walls- however dont want 'blobs'
                    // thus for no adjacent walls zero fitness, from there fitness shall be inversely
                    // proportional to adjacent wall count (we want only one adjacent wall)
                    
                    /// CHECK FOR ADJACENT WALLS
                    // need to have checks for edge cases

                    // above
                    if (y != 0) // not the top row
                    {
                        if( genotype[x+(y-1)*playable_height] == 'w')
                        {
                            adjacent_walls++;
                        }
                    }

                    // below 
                    if (y != (playable_height-1)) // not the bottom row
                    {
                        if( genotype[x+(y+1)*playable_height] == 'w')
                        {
                            adjacent_walls++;
                        }
                    }

                    // left
                    if (x != 0) // not left most
                    {
                        if( genotype[x-1+(y)*playable_height] == 'w')
                        {
                            adjacent_walls++;
                        }
                    }

                    // right
                    if (x != (playable_width-1)) // not rightmost
                    {
                        if( genotype[x+1+(y)*playable_height] == 'w')
                        {
                            adjacent_walls++;
                        }

                    }


                    /// CONVERT AMOUNT OF ADJACENT WALLS INTO A SCORE
                    /// scores can be changed at a later date to confer more or less fitness
                    switch (adjacent_walls) {
                        case 0: 
                            score += 0;                           
                            break;
                        case 1: 
                            score += 7;                           
                            break;
                        case 2:           
                            score += 3;                 
                            break;
                        case 3:     
                            score += 2;                       
                            break;
                        case 4:     
                            score += 0;                       
                            break;
                        default:
                            break;
                    }

                }
        }

        return score;
    }

    ////// COVERAGE BASED FITNESS //////
    // this is a potential objective that competes with having lots of walls 
    // coverage is only based on the prescence of walls- not enemies keys or doors
    public int calc_coverage_fitness()
    {
        int score = 0;
        int wall_count = 0;
        int playspace = playable_height*playable_width;
        /// CALCULATE WALL COVERAGE
        // just iterate through, count number of walls, and divide by 
        // total play space
        for (int i = 0; i < genotype.length; i++)
        {
            if(genotype[i]=='w')
            {
                wall_count++;
            }
        }

        /// CONVERT THE AMOUNT OF COVERAGE INTO A SCORE
        int non_walls_count = playspace-wall_count;
        float wall_coverage = wall_count/playspace;
        float floor_coverage = non_walls_count/playspace;
        

        score = non_walls_count;
        return score;
    }

    ///// CALC FITNESS //////
    /// somehow come up with an equation to combine the different fitness elements calculated as well as 
    /// requireing the level to be feasible
    public void calc_fitness(AbstractPlayer automatedAgent, StateObservation stateObs)
    {
        // testing running of automated agent
        if(calc_disqual(automatedAgent, stateObs))
        {
            System.out.println("i cant play the level no");
        }

    }

    public int get_fitness()
    {
        return fitness;
    }



}
