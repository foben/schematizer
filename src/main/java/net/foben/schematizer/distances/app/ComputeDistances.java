package net.foben.schematizer.distances.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.da.DAO;
import net.foben.schematizer.da.cassandra.CassandraDAO;
import net.foben.schematizer.da.mysql.MySQLDAO;
import net.foben.schematizer.distances.DistanceSelector;
import net.foben.schematizer.distances.ISimmilarityMeasure;
import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.LabelsCommentsResourceDescriptor;
import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.model.ResourceDescriptor;
import net.foben.schematizer.model.SimpleResourceDescriptor;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;


@SuppressWarnings("unused")
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
		
		//JaccardCommentsSim sim = new JaccardCommentsSim();
		
		ISimmilarityMeasure<? extends ResourceDescriptor> sim = DistanceSelector.getMeasure();
		
//		WrappedRepo repo = new WrappedRepo();
//		repo.addFile("src/main/resources/vocabularies.nq");
//		repo.addFile("src/main/resources/all_equis");
		
		
		Class<?> exp = sim.getExpected();
		ComparableResourceDescriptor[] candArray = null;
		
		Array.newInstance(exp, 5);
		candArray = ModelAccess.getCandidates(exp, top);
		
//		if(typesprops == 1) candArray = getCandidates(top, "src/main/resources/stats/sorted_types", repo.getConnection());
//		else if(typesprops == 2) candArray = getCandidates(top, "src/main/resources/stats/sorted_props", repo.getConnection());
//		else {
//			repo.close();
//			throw new IllegalArgumentException("You fool");
//		}
//		repo.close();
		
		String tablename = sim.getMeasureName() + "Top" + top;
		tablename += typesprops == 1 ? "Types" : "Props" ;
		
		//DAO dao = new MySQLDAO(tablename);
		DAO dao = new CassandraDAO(tablename);
		
		long total = candArray.length * (candArray.length + 1) / 2;
		long oneP = Math.max(((long) (total * 0.01)),1);
		long count = 0;
		last = System.nanoTime();
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			ComparableResourceDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				ComparableResourceDescriptor column = candArray[coli];
				double simil = sim.getSim(row, column);
				System.out.println(simil);
				//dao.queue(row, column, simil);
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

	private static <T> T deSerialize(){
		try {
			ObjectInputStream obin = new ObjectInputStream(new FileInputStream("obj"));
			@SuppressWarnings("unchecked")
			T obj = (T) obin.readObject();
			obin.close();
			return obj;
		} catch(ClassCastException e){
			e.printStackTrace();			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	private static <T> void serialize(T obj){
		try {
			ObjectOutputStream obout = new ObjectOutputStream(new FileOutputStream("obj"));
			obout.writeObject(obj);
			obout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static ResourceDescriptor[] getCandidates(int top, String filename, RepositoryConnection con) throws IOException {
		TreeMultiset<LabelsCommentsResourceDescriptor> s = TreeMultiset.create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		int count = 0;
		while((line = in.readLine()) != null && ++count <= top){
			LabelsCommentsResourceDescriptor t = null;
			try{
				String[] fields = line.split(" ");
				t = new LabelsCommentsResourceDescriptor(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), con);
			} catch (Exception e){
				e.printStackTrace();
			}
			if(t != null){
				s.add(t);
			}
		}
		in.close();
		return s.toArray(new LabelsCommentsResourceDescriptor[0]);
	}
}
