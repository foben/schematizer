package net.foben.schematizer.app;

import java.io.File;
import java.io.IOException;

import net.foben.schematizer.parse.CountParseablesHandler;
import net.foben.schematizer.parse.QuadFileParser;

public class CountParseables {

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
		for(int i = 0; i < args.length; i++){
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(new CountParseablesHandler(), args[i]);
			parser.startParsing(true);
			System.out.println();
		}

	}

}
