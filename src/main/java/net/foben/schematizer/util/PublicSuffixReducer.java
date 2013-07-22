package net.foben.schematizer.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PublicSuffixReducer implements IPLDReducer {
	
	public PublicSuffixReducer() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader("if(stcount%100000000 == 0) writeOut()"));
		String line;
		while((line = in.readLine()) != null){
			System.out.println(line);
		}
		in.close();
	}
	
	@Override
	public String getPLD(String uri) {
		// src/main/resources/publicsuffix.txt
		return null;
	}

}
