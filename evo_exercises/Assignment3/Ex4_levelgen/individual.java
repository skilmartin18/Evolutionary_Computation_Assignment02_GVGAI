package evo_exercises.Assignment3.Ex4_levelgen;



import java.util.*;
import handle_files.handle_files;

public class individual {

    // random var decs
    // premade map length is 14w*11h
    int playable_width = 18-2;
    int playable_height = 12-2;
    double optional_chance = 0.8;
    public Random rand = new Random();

    // genotype as 1d array for easy crossover, access via 
    // genotype[x+y*width]
    char[] genotype = new char[(playable_width)*(playable_height)];
    
   

    // placeables, choices must be unique and present
    // optional are optional, chosen for an individual via the
    // 'optional chance' var
    char[] choices = {'b','p','o','t','A'};
    char[] optional = {'k','l'};
    char [] tiles = {'w','.'};

    // random wall placing param, to start with good individuals
    // will place sections of wall rather than single walls 
    int maximum_wall_length = 6;
    int average_wall_number = 14;
    double wall_prob = 0.5;
    double horizontal_wall_chance = 0.5;


    public individual()
    {
        //create random genotype

        
        // initialise genotype all floors
        for(int i = 0; i < genotype.length; i++)
        {
            genotype[i] = '.';
        }

        // place unique objects i.e choices 
        for( char choice:choices)
        {
            System.out.println(choice);
            int x_rand = rand.nextInt(playable_width-1);
            int y_rand = rand.nextInt(playable_height-1);

            while(genotype[x_rand+y_rand*playable_width] != '.' )
            {
                x_rand = rand.nextInt(playable_width-1);
                y_rand = rand.nextInt(playable_height-1);
            }

            genotype[x_rand+y_rand*playable_width] = choice;  
            System.out.println(choice);
        }

       
        // place optionals (if rand>optional_chance)

        if(rand.nextDouble()>optional_chance)
        {
            
            // place optionals
            for( char option:optional)
            {
                int x_rand = rand.nextInt(playable_width-1);
                int y_rand = rand.nextInt(playable_height-1);

                while(genotype[x_rand+y_rand*playable_width] != '.' )
                {
                    x_rand = rand.nextInt(playable_width-1);
                    y_rand = rand.nextInt(playable_height-1);
                }

                genotype[x_rand+y_rand*playable_width] = option;  
            }

            // implement "locked rooms" here maybe if time permitting

        }

        // add random single walls 

        for ( int i = 0; i < average_wall_number*(1/wall_prob); i++)
        {
            // place wall?
            if ( rand.nextDouble() > wall_prob )
            {
                     // wall start
                    int x_rand = rand.nextInt(playable_width-1);
                    int y_rand = rand.nextInt(playable_height-1);

                    //place the wall
                    if(genotype[x_rand+y_rand*playable_width]=='.')
                    {
                        genotype[x_rand+y_rand*playable_width] = 'w';
                    }

            }
        }

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
