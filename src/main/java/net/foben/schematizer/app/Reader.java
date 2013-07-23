package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Reader {
	protected static int stcount = 0;
	protected static Logger _log;
	protected static long last, now, timeten;
	protected static Map<Integer, Double> avgs;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		init();
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line;
		while((line = br.readLine()) != null){
			stcount++;
			if(stcount%1000000 == 0){
				_log.info(stcount/1000000 + " million lines parsed in " + measure());
			}
			if(stcount%10000000 == 0){
				double avgten = ((System.nanoTime()-timeten) / 10d) / 1000000000d;
				_log.info("   Average last 10 million: " + avgten);
				avgs.put(stcount/10000000, avgten);
				timeten = System.nanoTime();
			}
		}
		System.out.println(line);
		br.close();
	}
	
	private static void init(){
		_log = LoggerFactory.getLogger(Reader.class);
		avgs = new HashMap<Integer, Double>();
	}
	
	private static String measure() {
		now = System.nanoTime();
		double dur = (now - last) / 1000000000d;
		String result = ("" + dur);
		last = now;
		return result;
	}

}
