package uk.ac.ed.inf.powergrab;

import java.util.*;
public class StatelessDrone extends Drone {
	
	//Inherits constructor from Drone
	public StatelessDrone(Position p, double ch, double co, int seed) {
		super(p, ch, co, seed);
	}	
	
	//Makes a move based on the drone's preferences
	@Override
	public String nextMove(HashMap<String, Station> s) {
		
		//Finds all directional moves that result in stations
		HashMap<Direction, Station> near = inrange(s);
		List<Direction> possibledirections = new ArrayList<Direction>(getDirections());
		
		
		//Finds the best direction from all direction that are nearby
		Direction bestdirection = null;
		bestdirection = findbestbaddirection(s, bestdirection, near, possibledirections);

		//If there is no best direction takes a random moe
		if(bestdirection == null) {
			return returnrandommove(possibledirections);
		} else {
			
			//Takes the best move if there is a move with positive coins or if there are no postive directions
			if (near.get(bestdirection).getCoin() > 0 || possibledirections.isEmpty()) {
				return returnbestmove(bestdirection);
			} else {
				return returnrandommove(possibledirections);
			}
		}

		
	}
		

	

}
