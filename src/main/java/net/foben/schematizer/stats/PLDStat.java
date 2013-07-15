package net.foben.schematizer.stats;

import java.util.HashMap;
import java.util.Map;

public class PLDStat {
	private String PLDString;
	private int instanceCount;
	private Map<String, Integer> typemap;
	
	public PLDStat(String pldString){
		this.PLDString = pldString;
		this.instanceCount = 0;
		typemap = new HashMap<String, Integer>();
	}
	
	public void incr(){
		instanceCount++;
	}
	
	public void incrType(String type){
		if(typemap.containsKey(type)){
			typemap.put(type, typemap.get(type) + 1);
		}
		else{
			typemap.put(type, 1);
		}
	}
	
	public String toString(){
		String result = "";
		result += PLDString + " TOTAL " + instanceCount + "\n";
		for(String type : typemap.keySet()){
			result += PLDString + " " + type + " " + typemap.get(type) + "\n";
		}
		
		return result;
	}

	
}
