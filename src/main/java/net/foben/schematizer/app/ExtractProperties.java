package net.foben.schematizer.app;

import net.foben.schematizer.parse.ExtractPropertiesHandler;
import net.foben.schematizer.parse.MultipleQuadFileParser;

public class ExtractProperties {

	public static void main(String[] args) {
		for(String str : args){
			System.out.println(str);
		}
		ExtractPropertiesHandler handler = new ExtractPropertiesHandler();
		MultipleQuadFileParser parser = new MultipleQuadFileParser(handler, args);
		parser.startParsing();

	}

}
