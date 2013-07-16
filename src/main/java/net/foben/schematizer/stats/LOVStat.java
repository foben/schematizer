package net.foben.schematizer.stats;

import java.util.HashMap;
import java.util.Map;

public class LOVStat {
	private String PLDString;
	private Map<String, Integer> lovmap;
	
	public LOVStat(String pldString){
		this.PLDString = pldString;
		lovmap = new HashMap<String, Integer>();
	}
	
	public void incrVoc(String type){
		if(lovmap.containsKey(type)){
			lovmap.put(type, lovmap.get(type) + 1);
		}
		else{
			lovmap.put(type, 1);
		}
	}
	
	public String toString(){
		String result = "";
		for(String type : lovmap.keySet()){
			result += PLDString + " " + type + " " + lovmap.get(type) + "\n";
		}
		return result;
	}

	
}
