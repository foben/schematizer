package net.foben.schematizer.app;

import java.io.File;
import java.io.IOException;

import net.foben.schematizer.parse.LOVHandler;
import net.foben.schematizer.parse.QuadFileParser;
import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.LookupPLDReducer;

public class LOVAnalyzer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for(int j = 0; j < args.length; j++){
			System.out.println(args[j]);
			if(!new File(args[j]).exists()){
				throw new IllegalArgumentException("File " + args[j] + " doesn't exist!");
			}
		}
		IPLDReducer reducer = new LookupPLDReducer();
		for(int i = 0; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			//QuadFileParser parser = new QuadFileParser(new TypeCountHandler(args[i]+"_stats"),	args[i]);
			QuadFileParser parser = new QuadFileParser(new LOVHandler(args[i]+"_lovs", reducer),	args[i]);
			parser.startParsing();
			System.out.println();
		}
	}

}