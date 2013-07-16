package net.foben.schematizer.stats;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyStat {
	private String PLDString;
	private int instanceCount;
	private Map<String, Integer> typemap;
	private Logger _log;
	
	public int getTotal(){
		return instanceCount;
	}
	
	public PropertyStat(String pldString){
		_log = LoggerFactory.getLogger(PropertyStat.class);
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
	
	public void write(Writer w) throws IOException{
		w.write(PLDString + " TOTAL " + instanceCount + "\n");
		int lines = 0;
		for(String type : typemap.keySet()){
			lines++;
			w.write(PLDString + " " + type + " " + typemap.get(type) + "\n");
			if(lines%5000 == 0){
				_log.info("Flushing 5000 lines");
				w.flush();
			}
		}
	}
	
}
