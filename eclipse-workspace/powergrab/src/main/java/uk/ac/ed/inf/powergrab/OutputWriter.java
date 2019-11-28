package uk.ac.ed.inf.powergrab;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.mapbox.geojson.Feature;

public class OutputWriter {
	private PrintWriter geoprint;
	private PrintWriter txtprint;
	
	//Creates the two needed files
	public OutputWriter(String dronetype, String day, String month, String year) throws IOException {
    	FileWriter writegeo = new FileWriter(dronetype + "-" + day + "-" + month + "-" + year + ".geojson");
    	geoprint = new PrintWriter(writegeo);
    	
    	FileWriter writetxt = new FileWriter(dronetype + "-" + day + "-" + month + "-" + year + ".txt");
    	txtprint = new PrintWriter(writetxt);
    	
	}
	
	//Outputs for geojson display
    public void geooutput(ArrayList<Position> p, List<Feature> features) throws IOException {

    	geoprint.print("{\"type\": \"FeatureCollection\",\n \"date-generated\": "
        		+ "\"Tue Jan 01 2019\", \n \"features\": [\n "
        		+ "{\"type\": \"Feature\", \n \"properties\": { }, \n "
        		+ "\"geometry\": { \n"
        		+ "\"type\": \"LineString\",\n"
        		+ "\"coordinates\": [ \n");
    	
        for (int i =0; i < p.size()-1; i++) {
        	geoprint.println("["+ p.get(i).longitude + "," + p.get(i).latitude + "],");
        }
        geoprint.println("["+ p.get(p.size()-1).longitude + "," + p.get(p.size()-1).latitude + "]");
        geoprint.println("]}},\n");
        for (int i =0; i < features.size()-1; i++) {
        	geoprint.println(features.get(i).toJson() + ",");
        	
        	
        }
        geoprint.println(features.get(features.size()-1).toJson());
        
        geoprint.println("]}");
        
    	
        geoprint.close();
    }
    
    //Output for txt file
    public void txtoutput(String s) {
    	txtprint.println(s);
    }

	public PrintWriter getGeoprint() {
		return geoprint;
	}

	public void setGeoprint(PrintWriter geoprint) {
		this.geoprint = geoprint;
	}

	public PrintWriter getTxtprint() {
		return txtprint;
	}

	public void setTxtprint(PrintWriter txtprint) {
		this.txtprint = txtprint;
	}
    
    
    
}
