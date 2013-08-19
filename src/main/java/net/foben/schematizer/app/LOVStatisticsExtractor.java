package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import net.foben.schematizer.parse.LOVStatisticExtractHandler;
import net.foben.schematizer.parse.NonNodeStoringNQuadsParser;
import net.foben.schematizer.parse.QuadFileParser;

public class LOVStatisticsExtractor {

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("data-0.nq"));
		HashMap<String, String> foo = new HashMap<String, String>();
		String line;
		while ((line = br.readLine()) != null) {
			foo.put(line, "foo");
		}
		foo = null;
		br.close();
		br = null;

		LOVStatisticExtractHandler handler = new LOVStatisticExtractHandler(
				args.length);

		for (int i = 0; i < args.length; i++) {
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(handler, args[i],
					new NonNodeStoringNQuadsParser());
			parser.startParsing();
			System.out.println();

		}

	}

}
