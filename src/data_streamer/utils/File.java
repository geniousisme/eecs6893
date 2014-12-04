/*
 * 
 */
package data_streamer.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Used to parse files and write to files
 * @author theocean154
 *
 */
public class File {

	
	/* read values from a csv with header
	 * and return a list of dictionaries
	 */
	public static ArrayList<Dictionary<String,String>> import_csv(String filename) throws IOException{
		ArrayList<Dictionary<String,String>> list = new ArrayList<Dictionary<String,String>>();
		CSVReader reader = new CSVReader(new FileReader(filename));
	    String [] nextLine;
	    nextLine = reader.readNext();
	    String[] keys = nextLine.clone();
	    while ((nextLine = reader.readNext()) != null) {
    		Dictionary<String,String> to_add = new Hashtable<String, String>();
	    	for(int i =0;i<nextLine.length;i++){
	    		to_add.put(keys[i], nextLine[i]);
	    	}
	    	list.add(to_add);
	    }
	    reader.close();
		
		return list; 
		
	}
	
	
	/* take value from a list of dictionaries and
	 * output a csv file
	 */
	public static void export_csv(String filename, ArrayList<Map<String,String>> list) throws IOException{
		 CSVWriter writer = new CSVWriter(new FileWriter(filename), '\t');
	     // feed in your array (or convert your data to an array)
	     String[] entries = "first#second#third".split("#");
	     writer.writeNext(entries);
		 writer.close();
		
	}
	
	
	
	
	
	
}
