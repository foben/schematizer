package net.foben.schematizer.app.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import net.foben.schematizer.cassandra.CassandraDAO;
import net.foben.schematizer.distances.ComputeDistances;
import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.distances.LabeledResDescriptor;
import net.foben.schematizer.distances.ResDescriptor;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

public class LabeledTest {
	static Logger _log = LoggerFactory.getLogger(LabeledTest.class);

	public static void main(String[] args) throws IOException {
		boolean serialize = Boolean.parseBoolean(args[0]);
		int top = Integer.parseInt(args[1]);
		TreeMultiset<LabeledResDescriptor> list;
		if(serialize){
			WrappedRepo repo = new WrappedRepo();
			repo.addFile("src/main/resources/vocabularies.nq");
			list = getCandidates(top, "src/main/resources/stats/sorted_types", repo.getConnection());
			serialize(list);
			repo.close();
			System.exit(0);
		}
		else{
			list = deSerialize();
		}
		CassandraDAO cass = new CassandraDAO("JaccardTop30Types");
		LabeledResDescriptor[] candArray = list.toArray(new LabeledResDescriptor[0]);
		JaccardCommentsSim sim = new JaccardCommentsSim();
		long total = candArray.length * (candArray.length + 1) / 2;
		long count = 0;
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			HashMap<String, Float> map = new HashMap<String, Float>();
			LabeledResDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				LabeledResDescriptor column = candArray[coli];
				double simil = sim.getSim(row, column);
				map.put(column.getType(), (float)simil);
				if(map.keySet().size() > 5000){
					cass.addData(row.getType(), map);
					map.clear();
				}
				if(++count%100000 == 0) _log.info(((int)(count*10000d/total))/100d + "%");
			}
			cass.addData(row.getType(), map);
		}
		System.out.println(total);
		cass.shutdown();
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
	
	private static TreeMultiset<LabeledResDescriptor> getCandidates(int top, String filename, RepositoryConnection con) throws IOException {

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
		return s;
		//return s.descendingMultiset();
	}
}
