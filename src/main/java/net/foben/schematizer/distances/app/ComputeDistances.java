package net.foben.schematizer.distances.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;

import net.foben.schematizer.da.DAO;
import net.foben.schematizer.da.cassandra.CassandraDAO;
import net.foben.schematizer.da.mysql.MySQLDAO;
import net.foben.schematizer.distances.DistanceSelector;
import net.foben.schematizer.distances.ISimmilarityMeasure;
import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.model.ResourceDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComputeDistances {

	static Logger _log = LoggerFactory.getLogger(ComputeDistances.class);
	static long last;
	
	public static void main(String[] args) throws IOException {
		int typesprops = -1;
		int top = -1;
		try {
			System.out.println("Types(1) or Props(2) ??");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			typesprops = Integer.parseInt(br.readLine());
			
			System.out.println("How much ???");
			top = Integer.parseInt(br.readLine());
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ISimmilarityMeasure<? extends ResourceDescriptor> sim = DistanceSelector.getMeasure();
		
		Class<?> exp = sim.getExpected();
		ComparableResourceDescriptor[] candArray = null;
		
		Array.newInstance(exp, 5);
		candArray = ModelAccess.getCandidates(exp, top);
		
		String tablename = sim.getMeasureName() + "Top" + top;
		tablename += typesprops == 1 ? "Types" : "Props" ;
		
		DAO dao = new MySQLDAO(tablename);
		//DAO dao = new CassandraDAO(tablename);
		
		long total = candArray.length * (candArray.length + 1) / 2;
		long oneP = Math.max(((long) (total * 0.01)),1);
		long count = 0;
		last = System.nanoTime();
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			ComparableResourceDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				ComparableResourceDescriptor column = candArray[coli];
				double simil = sim.getSim(row, column);
				dao.queue(row, column, simil);
				if(++count%oneP == 0){
					_log.info(((int)(count*10000d/total))/100d + "%  took " + measure());
				}
			}
		}
		dao.terminate();
		
	}
	
	
	private static String measure() {
		long dur = System.nanoTime() - last;
		last = System.nanoTime();
		return Math.round((dur/1000000000d)*100)/100 + " s";
	}
	
}
