package net.foben.schematizer.distances;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.foben.schematizer.cassandra.CassandraDAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

public class ComputeDistances {

	static List<ResDescriptor> candidates;
	static Logger _log = LoggerFactory.getLogger(ComputeDistances.class);
	static String del = ";";
	
	public static void main(String[] args) throws IOException {
		int top = Integer.parseInt(args[0]);
		ISimmilarityMeasure sim = new NormalizedLevenstheinSim();
		String filename = "src/main/resources/stats/sorted_types";
		candidates = getCandidates(top, filename);
		ResDescriptor[] candArray = candidates.toArray(new ResDescriptor[0]);
		
		CassandraDAO cass = new CassandraDAO("levnorm");
		
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			HashMap<String, Float> map = new HashMap<String, Float>();
			ResDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				ResDescriptor column = candArray[coli];
				double simil = sim.getSim(row, column);
				map.put(column.getType(), (float)simil);
				//System.out.println(String.format("%s - %s   : %s", row, column, simil));
			}
			cass.addData(row.getType(), map);
			_log.info((rowi*100d/(candArray.length)) + " %    " + rowi +"  " + candArray.length);
		}
		cass.shutdown();
	}
	
//	private static void createCSV(Table<ResDescriptor, ResDescriptor, Double> table, String file) throws IOException{
//		BufferedWriter out = new BufferedWriter(new FileWriter(file));
//		out.write(del);
//		for(Object o : table.columnKeySet()){
//			out.write(o + del);
//		}
//		out.newLine();
//		for(Object row : table.rowKeySet()){
//			out.write(row.toString());
//			for(Object col : table.columnKeySet()){
//				out.write(del + table.get(row, col));
//			}
//			out.newLine();
//		}
//		out.close();
//	}
	
	private static LinkedList<ResDescriptor> getCandidates(int top, String filename) throws IOException {
		_log.info("Reading candidates from " + filename);
		TreeMultiset<ResDescriptor> s = TreeMultiset.create();
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		
		while((line = in.readLine()) != null){
			ResDescriptor t = null;
			try{
				String[] fields = line.split(" ");
				t = new ResDescriptor(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
			} catch (Exception e){
				e.printStackTrace();
			}
			if(t != null){
				s.add(t);
			}
		}
		in.close();
		
		Iterator<ResDescriptor> iter = s.descendingMultiset().iterator();
		LinkedList<ResDescriptor> candidates = new LinkedList<ResDescriptor>();
		int count = 0;
		while(iter.hasNext() && ++count <= top){
			candidates.add(iter.next());
		}
		_log.info("Created candidate list");
		return candidates;
	}
}
