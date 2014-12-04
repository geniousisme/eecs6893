/*
 * 
 */
package data_streamer;

import java.util.Dictionary;
import java.util.concurrent.atomic.AtomicReference;

public class Exchange {
	
	private AtomicReference<Dictionary<String, Dictionary<String,Dictionary<String, String>>>> current;

	
	/**
	 * Instantiates a new exchange.
	 */
	public Exchange(){
		current = new AtomicReference<Dictionary<String, Dictionary<String,Dictionary<String, String>>>>();
	}
	
	public Dictionary<String, Dictionary<String,Dictionary<String, String>>> getCurrent(){
		return current.get();
	}
	
	public void setCurrent(Dictionary<String, Dictionary<String, Dictionary<String, String>>> current2){
		current.set(current2);
	}
	
	
	//TODO add method to tack on costs	
	public void addTransactionCosts(){
		
	}
	
	
	
}
