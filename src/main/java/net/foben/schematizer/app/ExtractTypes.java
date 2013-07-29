package net.foben.schematizer.app;

import net.foben.schematizer.parse.ExtractTypesHandler;
import net.foben.schematizer.parse.MultipleQuadFileParser;

public class ExtractTypes {

	public static void main(String[] args) {
		for(String str : args){
			System.out.println(str);
		}
		ExtractTypesHandler handler = new ExtractTypesHandler();
		MultipleQuadFileParser parser = new MultipleQuadFileParser(handler, args);
		parser.startParsing();

	}

}
