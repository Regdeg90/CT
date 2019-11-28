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
import java.util.Random;
public abstract class Drone {
	private Position pos;
	private double charge;
	private double coins;
	private Random random;
	
	private final List<Direction> directions = new ArrayList<Direction> (Arrays.asList(Direction.E, Direction.ENE, Direction.ESE, Direction.N, Direction.NE, Direction.NNE, Direction.NNW,
			Direction.NW, Direction.S, Direction.SE, Direction.SSE, Direction.SSW, Direction.SW, Direction.W, Direction.WNW, Direction.WSW));
	
	public Drone (Position p, double ch, double co, int seed) {
		this.pos = p;
		this.charge = ch;
		this.coins = co;
		this.setRandom(new Random(seed));
	}
	
	//Updates the map if the drone is in contact with any stations
	public HashMap<String, Station> updateStats(HashMap<String, Station> s) {
		HashMap<String,Station> ret = s;
		//Finds if a station is close enough
		Station closestStaion = null;
		double closest = Double.POSITIVE_INFINITY;
		for (String str : s.keySet()) {
			
			Station station = s.get(str);
			
			double distance = eculidistance(this.pos, station.getPos());
			if (str.equals("f67c-7025-31e0-aaba-ecab-6696")) {
				System.out.println(this.pos.latitude+", "+this.pos.longitude);
				System.out.println(distance);
				
			}
			if (distance <= 0.00025 && distance < closest) {
				closest = distance;
				closestStaion = station;
			}
		}
		//Updates values accordingly if station exists
		if ((closestStaion!=null)) {
				System.out.println("Closest Station: "+closestStaion.getId());
			
			this.charge += closestStaion.getCharge();
			this.coins += closestStaion.getCoin();
			double x = 0.0;
			ret.replace(closestStaion.getId(), new Station(closestStaion.getId(), x, x, closestStaion.getPos()));

		}
		System.out.println();
		return ret;
	}
	
	//Will check that the move is valid and not in a negative radius
	public boolean goodmove(Position p, HashMap<String, Station> s) {
		Station closeststation = null;
		double closestdistance = Double.POSITIVE_INFINITY;
		if (!p.inPlayArea()) {
			return false;
		}
		
		//Sets the closest station
		for (String str : s.keySet()) {
			Station station = s.get(str);
			double distance = eculidistance(station.getPos(), p);
			
			if (distance < closestdistance) {
				closeststation = station;
				closestdistance = distance;
			}
		}
		
		//Checks the closest station is bad
		if (closestdistance <= 0.00025 && closeststation.getCoin() < 0) {
			return false;
		}
		
		return true;
	}
	
	//Performs euclidean distance between two points
	public double eculidistance(Position p1, Position p2) {
		return Math.sqrt(Math.pow((p1.latitude-p2.latitude), 2) + Math.pow((p1.longitude-p2.longitude), 2));
	}
	
	//Performs a move in a random direction and records a string
	public String returnrandommove(List<Direction> dirs) {
		String ret = "";
		int randno = this.getRandom().nextInt(dirs.size());
		ret += this.getPos().latitude + "," + this.getPos().longitude + "," + dirs.get(randno).name;
		this.setPos(this.getPos().nextPosition(dirs.get(randno)));
		ret += "," + this.getPos().latitude + "," + this.getPos().longitude;
		return ret;
		
	}
	
	//Performs the best move and records a string
	public String returnbestmove(Direction d) {
		String ret = "";
		ret += this.getPos().latitude + "," + this.getPos().longitude + "," + d.name;
		this.setPos(this.getPos().nextPosition(d));
		ret += "," + this.getPos().latitude + "," + this.getPos().longitude;
		return ret;
		
	}
	
	//Basis of next Move to be overwritten
	public String nextMove(HashMap<String, Station> s) {
		return null;
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
					if (distance <= 0.00025 && distance < closestdist) {
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
	
	//Will return the direction with the highest coins from nearby stations, can be negative
	public Direction findbestbaddirection(HashMap<String, Station> s, Direction bestdirection,
			HashMap<Direction, Station> near, List<Direction> dirs) {
		double bestval = Double.NEGATIVE_INFINITY;
		for (Direction d : getDirections()) {
			Position posmove = this.getPos().nextPosition(d);
			if (!goodmove(posmove, s)) {
				dirs.remove(d);
			}
			if (near.containsKey(d)) {
				Station posvist = near.get(d);

				if (posvist.getCoin() > bestval) {

					bestval = posvist.getCoin();
					bestdirection = d;
				}

			}
			
		}
		
//		System.out.println(bestval);
		return bestdirection;
	}
	
	public Position getPos() {return pos;}

	public void setPos(Position pos) {this.pos = pos;}

	public double getCharge() {return charge;}

	public void setCharge(double charge) {this.charge = charge;}

	public double getCoins() {return coins;}

	public void setCoins(double coins) {this.coins = coins;}

	public Random getRandom() {return random;}

	public void setRandom(Random random) {this.random = random;}

	public List<Direction> getDirections() {return directions;}
}
