package net.foben.schematizer.app;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.parse.GraphExtractHandler;
import net.foben.schematizer.parse.NonNodeStoringNQuadsParser;
import net.foben.schematizer.parse.QuadFileParser;

public class GraphExtractor {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for(String str : args){
			System.out.println(str);
		}
		Set<String> graphs = new HashSet<String>(1000);
		for(int i = 0; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(new GraphExtractHandler(graphs),	args[i], new NonNodeStoringNQuadsParser());
			parser.startParsing();
			System.out.println();
			
		}

	}

}
