package controllers.simulatedAnnealing;

import java.util.Random;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import ontology.Types.WINNER;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Agent extends AbstractPlayer{
	public static final double HUGE_VALUE = 1000;
	public static final int SEQ_LENGTH = 5;
	public static final double MAX_PROP = 0.6;
	
	private Random random;
	
	public int[][] tiles;
	public int totalMoves;
	
	public Agent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer){
		random = new Random();
		int d1 = stateObs.getObservationGrid().length;
		int d2 = stateObs.getObservationGrid()[0].length;
		
		tiles = new int[d1][d2];
		totalMoves = 0;
	}
	
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		float avgTime = 10;
		float worstTime = 10;
		float totalTime = 0;
		int numberOfIterations = 0;
		double maxValue = elapsedTimer.remainingTimeMillis();
		
		State currentState = new State(stateObs);
		currentState.randomInitialize(random);
		while(elapsedTimer.remainingTimeMillis() > 2 * avgTime 
				&& elapsedTimer.remainingTimeMillis() > worstTime){
			ElapsedCpuTimer methodTime = new ElapsedCpuTimer();
			
			State nextState = currentState.getRandomNeighbour(random);
			
			double delta = nextState.getFitness(this) - currentState.getFitness(this);
			if(delta > 0){
				currentState = nextState;
			}
			else if(random.nextDouble() < MAX_PROP * ((SEQ_LENGTH - Math.abs(delta)) / SEQ_LENGTH) * (elapsedTimer.remainingTimeMillis() / maxValue)){
				currentState = nextState;
			}

			numberOfIterations += 1;
			totalTime += methodTime.elapsedMillis();
			avgTime = totalTime / numberOfIterations;
		}
		
		return currentState.getFirstAction();
	}

	private class State{
		private Types.ACTIONS[] sequence;
		private StateObservation stateObs;
		
		public State(StateObservation stateObs){
			this.stateObs = stateObs;
			sequence = new Types.ACTIONS[Agent.SEQ_LENGTH];
		}
		
		public State(State state){
			this(state.stateObs);
			for(int i=0; i<state.sequence.length; i++){
				sequence[i] = state.sequence[i];
			}
		}
		
		public void randomInitialize(Random random){
			for(int i=0; i<sequence.length; i++){
				sequence[i] = stateObs.getAvailableActions().
						get(random.nextInt(stateObs.getAvailableActions().size()));
			}
		}
		
		public Types.ACTIONS getFirstAction(){
			return sequence[0];
		}
		
		public State getRandomNeighbour(Random random){
			State neightbour = new State(this);
			
			int randomIndex = random.nextInt(neightbour.sequence.length);
			neightbour.sequence[randomIndex] = neightbour.stateObs.getAvailableActions().
					get(random.nextInt(neightbour.stateObs.getAvailableActions().size()));
			
			return neightbour;
		}
		
		public double getFitness(Agent agent){
			StateObservation tempState = stateObs.copy();
			for(int i=0;i<sequence.length;i++){
				tempState.advance(sequence[i]);
			}
			
			if(tempState.isGameOver()){
				if(tempState.getGameWinner() == WINNER.PLAYER_WINS){
					return HUGE_VALUE;
				}
				else{
					return -HUGE_VALUE;
				}
			}
			
			Vector2d point = tempState.getAvatarPosition().mul(1.0/tempState.getBlockSize());
			agent.totalMoves += 1;
			agent.tiles[(int)point.x][(int)point.y] += 1;
			
			double value = (agent.totalMoves - agent.tiles[(int)point.x][(int)point.y]) / (agent.totalMoves * 1.0);
			return tempState.getGameScore() * value;
		}
	}
	
}
