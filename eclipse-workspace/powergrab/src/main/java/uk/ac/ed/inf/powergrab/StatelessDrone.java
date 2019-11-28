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
		HashMap<Direction, Station> near = this.inrange(s);
		List<Direction> possibledirections = new ArrayList<Direction>(getDirections());
		
		//Finds the best direction
		Direction bestdirection = null;
		double bestval = Double.NEGATIVE_INFINITY;
		for (Direction d : getDirections()) {
			//Find the largest coins every directional moves
			if (near.containsKey(d)) {
				Station posvist = near.get(d);
				if (posvist.getCoin() > bestval) {

					bestval = posvist.getCoin();
					bestdirection = d;
				}

			}
			
			if (!goodmove(this.getPos().nextPosition(d), s)) {
				possibledirections.remove(d);
			}
		}
		//If there is a station with positive coins or there is no neutral directions
		//Otherwise finds a random move from neutral directions
		if (bestval > 0 ||( possibledirections.isEmpty() && bestdirection != null)) {
			return returnbestmove(bestdirection);
			
		} else {
			return returnrandommove(possibledirections);
		}
		
	}
		
	//Finds which stations are in range after a directional move
	public HashMap<Direction,Station> inrange(HashMap<String, Station> s){
		
		HashMap<Direction,Station> ret = new HashMap<Direction, Station>();
		
		for (Direction d : getDirections()) {
			
			Position potential = this.getPos().nextPosition(d);
			Station closeststation = null;
			double closestdist = Double.POSITIVE_INFINITY;
			if (potential.inPlayArea()) {
				for (String str : s.keySet()) {
					
					Station station = s.get(str);
					
					//Finds the closest station to the new position
					double distance = eculidistance(potential, station.getPos());
					if (distance < 0.00025 && distance < closestdist) {
						closestdist = distance;
						closeststation = station;
					}
					
				}
				
			}
			if (!(closeststation==null)) {
				ret.put(d, closeststation);
			}
			
		}
		
		
		
		return ret;
	}
	

}
