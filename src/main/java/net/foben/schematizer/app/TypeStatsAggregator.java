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

import static net.foben.schematizer.Environment.*;

public class TypeStatsAggregator {
	private static String d = " ";
	
	private Table<String, String, TypeStat> stats;
	private Logger _log;
	private String[] args;
	
	public TypeStatsAggregator(String[] args){
		_log = LoggerFactory.getLogger(TypeStatsAggregator.class);
		this.args = args;
	}
	
	public static void main(String[] args){
		TypeStatsAggregator.printToFile2(new TypeStatsAggregator(args).parseTypeStats());
	}
	
	public Table<String, String, TypeStat> parseTypeStats(){
		printArgs(args);
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
					if(count <= 0){
						System.out.println();
					}
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
		return stats;
		//printToFile2(stats);
	}
	
	public static void printToFile2(Table<String, String, TypeStat> stats){
		//stats.
		
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("out.csv"));
			out.write("foo ");
			for(String row : stats.rowKeySet()){
				out.write(row + d);
			}
			out.newLine();
			
			for(String col : stats.columnKeySet()){
				out.write(col + d);
				for(String row : stats.rowKeySet()){
					TypeStat stat = stats.get(row, col);
					if (stat == null) 	out.write("0" + d);
					else				out.write(stat.getCount() + d);
				}
				out.newLine();
			}
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
