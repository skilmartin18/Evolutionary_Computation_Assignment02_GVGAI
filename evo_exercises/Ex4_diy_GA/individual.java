package evo_exercises.Ex4_diy_GA;

import tracks.singlePlayer.tools.Heuristics.SimpleStateHeuristic;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import tools.ElapsedCpuTimer;
import tools.Utils;
import ontology.*;
import ontology.Types.ACTIONS;
import java.util.ArrayList;
import java.util.Random;
import core.game.StateObservation;

public class individual {

    ArrayList<Types.ACTIONS> genotype;
    ArrayList<Types.ACTIONS> actions;
    int available_actions;

    Random rand = new Random();
    double fitness = 0;

    public individual(StateObservation StateObs, int genotype_size)
    {
        // get available actions (maybe move this out to increase performance)
        actions = StateObs.getAvailableActions(true);
        available_actions = actions.size();

        genotype = create_individual(StateObs,genotype_size);
    }

    public individual(ArrayList<Types.ACTIONS> _genotype, StateObservation StateObs)
    {
        // get available actions (maybe move this out to increase performance)
        actions = StateObs.getAvailableActions();
        available_actions = actions.size();

        genotype = new ArrayList<Types.ACTIONS>(_genotype);
    }
    
    public individual(int genotype_size, StateObservation StateObs)
    {
        // get available actions (maybe move this out to increase performance)
        actions = StateObs.getAvailableActions();
        available_actions = actions.size();
        
        for(int i = 0; i < genotype_size; i++)
        {
            genotype.add(ACTIONS.ACTION_NIL);
        }
    }

    // create individual based on stateObs, creates individual of size set at beginning
    public ArrayList<Types.ACTIONS> create_individual(StateObservation stateObs, int genotype_size)
    {
        ArrayList<Types.ACTIONS> individual = new ArrayList<Types.ACTIONS>();

        // add actions from the list of available actions to individual
        for(int i = 0; i < genotype_size; i++)
        {
            individual.add( actions.get(rand.nextInt(available_actions)) );
        }

        return individual;
    }
}
