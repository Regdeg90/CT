package uk.ac.ed.inf.powergrab;

import java.io.*;



import java.net.*;
import java.util.*;

import com.mapbox.geojson.*;



/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException
    {
        String day = args[0];
        String month = args[1];
        String year = args[2];
        double latitude = new Double(args[3]);
        double longitude = new Double(args[4]);
        int seed = new Integer(args[5]);
        String dronetype = args[6];
        
        Map currentmap = new Map(day,month,year);
        
        //Sets dronetype
        Drone droney;
        if (dronetype.equals("stateless")){
        	droney = new StatelessDrone(new Position(latitude, longitude), 250, 0, seed);
        } else {
        	droney = new StatefullDrone(new Position(latitude, longitude), 250, 0, seed);
        }
        
    	currentmap.getDroneflightpath().add(droney.getPos());
    	
    	//Makes 250 moves of the drone type
    	OutputWriter output = new OutputWriter(dronetype, day, month, year);
    	int count = 0;
    	while (droney.getCharge() > 0 && count < 250) {
    		dronemove(currentmap,output, droney);
    		count++;
    	}
        
        output.getTxtprint().close();
        
        //Outputs to geojson
        output.geooutput(currentmap.getDroneflightpath(), currentmap.getFeatures());
        System.out.println("done");        
    }

    //Calls the next move of a drone and returns the updated stations
	private static void dronemove(Map currentmap, OutputWriter output, Drone droneyb) {
		
		double oldcoins = droneyb.getCoins();
		String s = droneyb.nextMove(currentmap.getStations());
		
		//Updates the station in range of the drone and sets the maps stations
		HashMap<String, Station> stations = droneyb.updateStats(currentmap.getStations());
		currentmap.setStations(stations);
		
		//If drone visits a positive station reset repeated moves
		if (oldcoins != droneyb.getCoins() && droneyb instanceof StatefullDrone) {
			StatefullDrone dron = (StatefullDrone) droneyb;
			dron.repeatedmoves = 0;
		}
		droneyb.setCharge(droneyb.getCharge()-1.25);
		
		s += "," + droneyb.getCoins() + "," + droneyb.getCharge();
		output.txtoutput(s);
		currentmap.getDroneflightpath().add(droneyb.getPos());
		
	}
	
	//validation
}
