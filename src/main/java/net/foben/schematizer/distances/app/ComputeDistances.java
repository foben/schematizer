package net.foben.schematizer.distances.app;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.distances.LabeledResDescriptor;
import net.foben.schematizer.distances.ResDescriptor;
import net.foben.schematizer.mysql.MySQLDAO;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;


@SuppressWarnings("unused")
public class ComputeDistances {

	static Logger _log = LoggerFactory.getLogger(ComputeDistances.class);
	static String del = ";";
	static long last;
	
	public static void main(String[] args) throws IOException {
		int top = Integer.parseInt(args[0]);
		
		JaccardCommentsSim sim = new JaccardCommentsSim();
		
//		WrappedRepo repo = new WrappedRepo();
//		repo.addFile("src/main/resources/vocabularies.nq");
//		LabeledResDescriptor[] candArray = getCandidates(top, "src/main/resources/stats/sorted_types", repo.getConnection());
//		repo.close();
		
		LabeledResDescriptor[] candArray = deSerialize();
		
		MySQLDAO dao = new MySQLDAO("JaccardCommentTop30Types");
		
		Set<String> uniq = new HashSet<String>();
		
		long total = candArray.length * (candArray.length + 1) / 2;
		long count = 0;
		last = System.nanoTime();
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			LabeledResDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				LabeledResDescriptor column = candArray[coli];
				boolean news = uniq.add(row.getType()+column.getType());
				if(!news){
					System.out.println("!!!!!!!");
				}
				double simil = sim.getSim(row, column);
				dao.queue(row, column, simil);
				if(++count%100000 == 0){
					_log.info(((int)(count*10000d/total))/100d + "% + took " + measure());
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
	
	private static LabeledResDescriptor[] getCandidates(int top, String filename, RepositoryConnection con) throws IOException {
		TreeMultiset<LabeledResDescriptor> s = TreeMultiset.create(new ResDescriptor.ResDescriptorReverseOrdering());
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		int count = 0;
		while((line = in.readLine()) != null && ++count <= top){
			LabeledResDescriptor t = null;
			try{
				String[] fields = line.split(" ");
				t = new LabeledResDescriptor(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]), con);
			} catch (Exception e){
				e.printStackTrace();
			}
			if(t != null){
				s.add(t);
			}
		}
		in.close();
		return s.toArray(new LabeledResDescriptor[0]);
	}
}
