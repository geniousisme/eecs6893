/*
 * 
 */
package data_streamer.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

/**
 * used to log data for use by python analysis system
 * @author theocean154
 *
 */
public class Output {

	String path;
	String name;
	PrintWriter f;
	
	public Output(String p, String n) throws IOException{
		path = p;
		name = n;
		Date date= new java.util.Date();
		f = new PrintWriter(path + name + (new Timestamp(date.getTime())).toString() + ".log", "UTF-8");
	}
	
	
	
	public void log(String s){
		f.println(s);
	}
	
	public void end(){
		f.close();
	}
	
}
