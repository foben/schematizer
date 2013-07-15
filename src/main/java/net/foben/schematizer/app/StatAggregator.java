package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.foben.schematizer.stats.TypeStat;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class StatAggregator {
	
	static Table<String, String, TypeStat> stats;
	static Logger _log;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		_log = LoggerFactory.getLogger(StatAggregator.class);
		stats = HashBasedTable.create();
		for(int i = 0; i < args.length; i++){
			String file = args[i];
			_log.info("Parsing file " + file);
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line, dataset, type;
				int count;
				while((line = in.readLine()) != null){
					String[] data = line.split(" ");
					dataset = data[0];
					type = data[1];
					count = Integer.parseInt(data[2]);
					if(stats.contains(dataset, type)){
						stats.get(dataset, type).incr(count);
					}
					else {
						stats.put(dataset, type, new TypeStat(dataset, type, count));
					}
				}
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		_log.info("complete");
		printToFile(stats);
	}
	
	public static void printToFile(Table<String, String, TypeStat> stats){
		//stats.
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("out.csv"));
			out.write("foo ");
			int limit = 0;
			for(String col : stats.columnKeySet()){
				out.write(col + " ");
				if (limit++ > 500) break;
			}
			out.newLine();
			for(String row : stats.rowKeySet()){
				out.write(row + " ");
				limit = 0;
				for(String col : stats.columnKeySet()){
					TypeStat stat = stats.get(row, col);
					if (stat == null)out.write("0 ");
					else{
						out.write(stat.getCount() + " ");
					}
					if (limit++ > 500) break;
				}
				out.newLine();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
