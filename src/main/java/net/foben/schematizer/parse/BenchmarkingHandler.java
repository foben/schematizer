package net.foben.schematizer.parse;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;

public class BenchmarkingHandler extends AbstractHandler {
	
	long timeten;
	Map<Integer, Double> avgs;
	
	public BenchmarkingHandler(){
		super();
		timeten = System.nanoTime();
		avgs = new HashMap<Integer, Double>();
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		
		if(stcount%10000000 == 0){
			double avgten = ((System.nanoTime()-timeten) / 10d) / 1000000000d;
			_log.info("   Average last 10 million: " + avgten);
			avgs.put(stcount/10000000, avgten);
			timeten = System.nanoTime();
		}

	}
	
	@Override
	protected void parseEnd() {
		_log.info("Averages");
		for(Integer key : avgs.keySet()){
			_log.info(" " + key + " : " + avgs.get(key));
		}
	}
}
