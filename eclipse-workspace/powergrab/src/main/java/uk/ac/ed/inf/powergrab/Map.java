package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Map {
	private List<Feature> features;
	private HashMap<String, Station> stations;
	private ArrayList<Position> droneflightpath;
	
	//Connects to geojson and extracts all the stations
	public Map(String day, String month, String year) throws MalformedURLException, IOException, ProtocolException{
		
		features = new ArrayList<Feature>();
		stations = new HashMap<String, Station>();
		droneflightpath = new ArrayList<Position>();
		
		setupconnection(day, month, year);
        getstations(features, stations);
	}
	
	//Connects to geojson
	private void setupconnection(String day, String month, String year)
			throws MalformedURLException, IOException, ProtocolException {
		String URLstring = "http://homepages.inf.ed.ac.uk/stg/powergrab/" + year + "/" + month + "/"+ day +"/powergrabmap.geojson";

        URL mapUrl = new URL(URLstring);
        
        HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
        
        conn.setReadTimeout(10000); // milliseconds
        conn.setConnectTimeout(15000); // milliseconds
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        
        Scanner scan = new Scanner(conn.getInputStream());
        String mapSource = scan.useDelimiter("\\A").next();
        
        FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        this.features = fc.features();
        scan.close();
		
	}
	
	//Gets all the stations from the map feature
	private static void getstations(List<Feature> features, HashMap<String, Station> stations) {
		for (Feature f : features) {
        	List<Double> p = ((Point) f.geometry()).coordinates();
        	Station s = new Station(f.getProperty("id").getAsString(), f.getProperty("power").getAsDouble(), f.getProperty("coins").getAsDouble(),
					new Position(p.get(1), p.get(0)));
        	stations.put(f.getProperty("id").getAsString(), s);
//        	System.out.println(s.getCharge());
        }
	}
	
	
	public HashMap<String, Station> getStations() {
		return stations;
	}
	
	public void setStations(HashMap<String, Station> stations) {
		this.stations = stations;
	}
	
	public List<Feature> getFeatures() {
		return features;
	}
	public void setFeatures(List<Feature> features) {
		this.features = features;
	}

	public ArrayList<Position> getDroneflightpath() {
		return droneflightpath;
	}

	public void setDroneflightpath(ArrayList<Position> droneflightpath) {
		this.droneflightpath = droneflightpath;
	}



}
