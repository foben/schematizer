package net.foben.schematizer.stats;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LOVStat {
	private String PLDString;
	private Map<String, Integer> lovmap;
	private Logger _log;
	
	public LOVStat(String pldString){
		_log = LoggerFactory.getLogger(LOVStat.class);
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

	public void write(Writer w) throws IOException {
		int lines = 0;
		for(String lov : lovmap.keySet()){
			lines++;
			w.write(PLDString + " " + lov + " " + lovmap.get(lov) + "\n");
			if(lines%5000 == 0){
				_log.info("Flushing 5000 lines");
				w.flush();
			}
		}
		
	}

	
}
