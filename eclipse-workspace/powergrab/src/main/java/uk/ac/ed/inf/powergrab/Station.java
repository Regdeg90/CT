package uk.ac.ed.inf.powergrab;

public class Station {
	private final String id;
	private double charge;
	private double coin;
	private final Position pos;
	
	
	public Station (String i, double ch, double co, Position p ) {
		this.id = i;
		this.charge = ch;
		this.coin = co;
		this.pos = p;
	}


	public String getId() {
		return id;
	}



	public double getCharge() {
		return charge;
	}


	public void setCharge(double charge) {
		this.charge = charge;
	}


	public double getCoin() {
		return coin;
	}


	public void setCoin(double coin) {
		this.coin = coin;
	}


	public Position getPos() {
		return pos;
	}

	
	
}
