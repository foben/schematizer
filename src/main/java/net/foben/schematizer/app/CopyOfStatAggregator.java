package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.foben.schematizer.stats.TypeStat;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class CopyOfStatAggregator {
	
	static Table<String, String, TypeStat> stats;
	static Logger _log;
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		_log = LoggerFactory.getLogger(CopyOfStatAggregator.class);
		stats = HashBasedTable.create();
		int total = 0;
		for(int i = 0; i < args.length; i++){
			
			String file = args[i];
			//_log.info("Parsing file " + file);
			try {
				BufferedReader in = new BufferedReader(new FileReader(file));
				String line, dataset, type;
				int count;
				while((line = in.readLine()) != null){
					String[] data = line.split(" ");
					dataset = data[0];
					type = data[1];
					count = Integer.parseInt(data[2]);
					total += count;
				}
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(args[i] + ":  " + total);
		}
		_log.info("complete");
	}
	
	
	
}
