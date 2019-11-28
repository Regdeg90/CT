package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author s1704097
 *
 */
public class StatefullDrone extends Drone{
	
	public List<Direction> previousmoves = new ArrayList<Direction>();;
	public List<String> unreachable = new ArrayList<String>();
	int repeatedmoves = 0;
	
	//Inherits constructor from Drone
	public StatefullDrone(Position p, double ch, double co, int seed) {
		super(p, ch, co, seed);
		
	}
	
	//Makes a move based on the drone's preferences
	@Override
	public String nextMove(HashMap<String, Station> s) {
		
		//Finds the station with the largest coins on the map 
		//and the which direction gets the drone closest to the best station
		Station beststation = findlargeststation(s);
		Direction bestdirection = findbestdirection(s, beststation);
		
		//Updates rand count if the direction has been used 2 moves ago
		if (previousmoves.size() >=3) {
			boolean b = previousmoves.subList(previousmoves.size()-2, previousmoves.size()-1).contains(bestdirection);
			if (b) {
				repeatedmoves++;
			}
		}
		
		//Takes a random direction
		if (repeatedmoves>=10 || beststation == null) {
			
			//Removes all directions that are bad
			List<Direction> dirs = new ArrayList<Direction>(getDirections());
			for (Direction d : getDirections()) {
				Position posmove = this.getPos().nextPosition(d);
				if (!goodmove(posmove, s)) {
					dirs.remove(d);
				}
				
			}
			
			//Sets station to unreachable if it's stuck
			if (beststation != null && repeatedmoves >= 10) {
				repeatedmoves = 0;
				unreachable.add(beststation.getId());
			}
			
			//Resets repeated moves
			if (repeatedmoves>=10) {
				repeatedmoves = 0;
			}
			
			return returnrandommove(dirs);
		}
		
		else {
			previousmoves.add(bestdirection);
			return returnbestmove(bestdirection);
		}
		
	}

	//Finds the direction that gets the drone closest to the best station
	private Direction findbestdirection(HashMap<String, Station> s, Station beststation) {
		Direction bestdirection = null;
		double closestdistance = Double.POSITIVE_INFINITY;
		
		for (Direction d : getDirections()) {
			
			Position posmove = this.getPos().nextPosition(d);
			
			if (beststation != null && goodmove(posmove, s) && repeatedmoves<10) {
				
				double distance = eculidistance(posmove, beststation.getPos());
				if (distance < closestdistance) {
					closestdistance = distance;
					bestdirection = d;
				}
			}
		}
		return bestdirection;
	}

	//Will find the station with that largest coins
	private Station findlargeststation(HashMap<String, Station> s) {
		//Gets best station from current stations that are reachable
		Station beststation = beststation(s);
		
		//If there are no more positive stations it will try to get
		//to the previously unreachable ones
		if (beststation == null && !unreachable.isEmpty()) {
			unreachable = new ArrayList<String>();
			beststation = this.findlargeststation(s);
		}
		
		return beststation;
	}

	//Find that largest station that is no unreachable
	private Station beststation(HashMap<String, Station> s) {
		Station beststation = null;
		double bestval = Double.POSITIVE_INFINITY;
		for (String str : s.keySet()) {
			Station station = s.get(str);
			if (station.getCoin() > 0  && !unreachable.contains(station.getId())) {
				double distance = eculidistance(this.getPos(), station.getPos());
				if (distance < bestval) {
					beststation = station;
					bestval = distance;

				}
			}
			
		}
		return beststation;
	}		
}
