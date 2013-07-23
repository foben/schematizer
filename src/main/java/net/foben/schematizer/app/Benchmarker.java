package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.foben.schematizer.parse.BenchmarkingHandler;
import net.foben.schematizer.parse.GraphExtractHandler;
import net.foben.schematizer.parse.QuadFileParser;

public class Benchmarker {

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
		boolean opt = true;
		HashMap<String, String> foo = null;
		
		if(opt) {
			System.out.println("\"optimizing\"");
			BufferedReader br = new BufferedReader(new FileReader("src/main/resources/graphs.nt"));
			foo = new HashMap<String, String>();
			String line;
			while ((line = br.readLine()) != null){
			    foo.put(line, "foo");
			}
			br.close();
			
		}
		
		for(int i = 0; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(new BenchmarkingHandler(),	args[i]);
			parser.startParsing();
			System.out.println();
		}
		if(opt)System.out.println(foo.keySet().size());
	}

}
