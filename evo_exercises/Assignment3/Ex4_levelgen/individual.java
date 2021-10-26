package evo_exercises.Assignment3.Ex4_levelgen;



import java.util.*;
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
}
