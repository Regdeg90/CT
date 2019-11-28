package uk.ac.ed.inf.powergrab;

/**
 * Public class direction that has a double variable in radians
 * @author s1704097
 *
 */
public class Direction {
	
	public double latitude;
	public double longitude;
	public String name;

	static public Direction N = new Direction(0.0, "N");
	static public Direction NNE = new Direction(22.5, "NNE");
	static public Direction NE = new Direction(45, "NE");
	static public Direction ENE = new Direction(67.5, "ENE");
	static public Direction E = new Direction(90, "E");
	static public Direction ESE = new Direction(112.5, "ESE");
	static public Direction SE = new Direction(135, "SE");
	static public Direction SSE = new Direction(157.5, "SSE");
	static public Direction S = new Direction(180, "S");
	static public Direction SSW = new Direction(202.5, "SSW");
	static public Direction SW = new Direction(225, "SW");
	static public Direction WSW = new Direction(247.5, "WSW");
	static public Direction W = new Direction(270, "W");
	static public Direction WNW = new Direction(292.5, "WNW");
	static public Direction NW = new Direction(315, "NW");
	static public Direction NNW = new Direction(337.5, "NNW");
	
	/**
	 * Constructor for the Direction class taking in a bearing in degrees
	 * @param d is the bearing in degrees
	 */
	public Direction (double d, String s) {
		this.longitude = 0.0003*Math.sin((Math.PI / 180) * (d));
		this.latitude = 0.0003*Math.cos((Math.PI / 180) * (d));
		this.name = s;
	}
	


}
