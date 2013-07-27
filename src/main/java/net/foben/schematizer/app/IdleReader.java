package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.foben.schematizer.parse.CustomQuadsParser;
import net.foben.schematizer.parse.GraphExtractHandler;
import net.foben.schematizer.parse.IdleHandler;
import net.foben.schematizer.parse.QuadFileParser;

public class IdleReader {

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
			QuadFileParser parser = new QuadFileParser(new IdleHandler(), args[i]);
			parser.startParsing(true);
			System.out.println();
		}

	}

}
