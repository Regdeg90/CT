package uk.ac.ed.inf.powergrab;

public class Position {
	public double latitude;
	public double longitude;

	/**
	 * Constructor for the Position class 
	 * @param latitude
	 * @param longitude
	 */
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	
	/**
	 * Calculates the drones next position given a direction
	 * @param direction
	 * @return Position with the new lat and long
	 */
	public Position nextPosition(Direction direction) {

		return new Position(this.latitude + direction.latitude, this.longitude + direction.longitude);
	}
	
	/**
	 * Checks if the drones current position is allowed
	 * @return boolean
	 */
	public boolean inPlayArea() {
		
		if (this.latitude > 55.946232 || this.latitude < 55.942616 || this.longitude < -3.192474 || this.longitude > -3.184318 ) {
			return false;
		} else {
			return true;
		}
		
		
	}

}
