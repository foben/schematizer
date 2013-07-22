package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import net.foben.schematizer.parse.GraphExtractHandler;
import net.foben.schematizer.parse.QuadFileParser;
import net.foben.schematizer.parse.TypeCountHandler;
import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.LookupPLDReducer;

public class GraphExtr {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for(int j = 1; j < args.length; j++){
			System.out.println(args[j]);
			if(!new File(args[j]).exists()){
				throw new IllegalArgumentException("File " + args[j] + " doesn't exist!");
			}
		}
		boolean opt = Boolean.parseBoolean(args[0]);
		System.out.println(opt);
		HashMap<String, String> foo = null;
		if(opt) { BufferedReader br = new BufferedReader(new FileReader("src/main/resources/graphs.nt"));
			foo = new HashMap<String, String>();
			String line;
			while ((line = br.readLine()) != null){
			    foo.put(line, "foo");
			}
	//		foo = null;
			br.close();
		}
		
		for(int i = 1; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			//QuadFileParser parser = new QuadFileParser(new TypeCountHandler(args[i]+"_stats"),	args[i]);
			QuadFileParser parser = new QuadFileParser(new GraphExtractHandler(args[i]+"_stats"),	args[i]);
			parser.startParsing();
			System.out.println();
		}
		if(opt)System.out.println(foo.keySet().size());
	}

}
