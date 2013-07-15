package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.util.RestrictingPLDReducer;

public class DatasetReducer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/graphs.nt"));
		String graph;
		Set<String> set = new HashSet<String>();
		RestrictingPLDReducer reducer = new RestrictingPLDReducer("src/main/resources/TopLevelDomains");
		int grcount = 0;
		while((graph = in.readLine()) != null){
			grcount++;
			String pld = reducer.getPLD(graph);
			set.add(pld);
			if (grcount%1000000 == 0)System.out.print('.');
		}
		System.out.println("\nMapsize: " + set.size());
		in.close();
		BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/datasets"));
		for(String ds : set){
			out.write(ds);
			out.newLine();
		}
		out.close();
		reducer = null;
	}

}
