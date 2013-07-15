package net.foben.schematizer.offline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.semanticweb.yars.nx.parser.NxParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger _log = LoggerFactory.getLogger(Main.class);
		BufferedReader in = null;
		long start = System.nanoTime();
		try {
			in = new BufferedReader(new FileReader("src/main/resources/quadtest_huge.nq"));
			NxParser nx = new NxParser(in);
			int i = 0;
			while(nx.hasNext()){
				i++;
				//Node[] quad = nx.next();
				//System.out.println(quad);
				if(i%1000000 == 0) _log.info(i/1000000 + " mio quads parsed");
			}
			_log.info("*****Done*****");
			long dur = System.nanoTime() - start;
			_log.info(i + " quads parsed in " + (dur)/1000000000l + " secs");
			_log.info(dur / i + " nanosecs per quad"); 
			
			
		} catch (FileNotFoundException e) {
			_log.error("Specified file not found!");
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				_log.error("Error freeing resource, IOException");
			}
		}
		
	}

}
