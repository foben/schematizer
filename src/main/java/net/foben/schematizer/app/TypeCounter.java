package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.foben.schematizer.offline.QuadFileParser;
import net.foben.schematizer.stats.PLDStat;
import net.foben.schematizer.util.CustomHandler;
import net.foben.schematizer.util.RestrictingPLDReducer;
import net.foben.schematizer.util.TypeCountHandler;

public class TypeCounter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/datasets"));
		String dataset;
		Map<String, PLDStat> map = new HashMap<String, PLDStat>();
		while((dataset = in.readLine()) != null){
			map.put(dataset, new PLDStat(dataset));
		}
		in.close();
		
		for(int i = 0; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(new TypeCountHandler(map,args[i]+"_stats"),
					args[i]);
			parser.startParsing();
			System.out.println();
		}
	}

}
