package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.RestrictingPLDReducer;

public class TypeMapper {
	static Logger _log = LoggerFactory.getLogger(TypeMapper.class);
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("foo"));
		BufferedWriter out = new BufferedWriter(new FileWriter("foo_"));
		String line;
		IPLDReducer reducer = new RestrictingPLDReducer("src/main/resources/TopLevelDomains");
		HashMap<String, Set<String>> mappings = new HashMap<String, Set<String>>();
		int count = 0;
		String oldline = "";
		in.readLine();
		while((line = in.readLine()) != null){
			count++;
			line = line.replace("\"", "");
			String[] ln = line.split("\t");
			String dataset = reducer.getPLD(ln[1]);
			String type = ln[0];
			line = type + " " + dataset;
			if(!line.equals(oldline)){
				out.write(type + " " + dataset);
				out.newLine();
			}
			oldline = line;
			System.out.println(count);
			if(count%1000000 == 0) _log.info((count/1000000) + " million lines parsed");
		}
		in.close();
		out.close();
		System.out.println();
	}

}
