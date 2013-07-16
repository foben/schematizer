package net.foben.schematizer.app;

import java.io.File;
import java.io.IOException;

import net.foben.schematizer.parse.QuadFileParser;
import net.foben.schematizer.parse.TypeCountHandler;

public class TypeCounter {

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
			QuadFileParser parser = new QuadFileParser(new TypeCountHandler(args[i]+"_stats"),	args[i]);
			parser.startParsing();
			System.out.println();
		}
	}

}
