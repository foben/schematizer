package net.foben.schematizer.distances.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.distances.LabeledResDescriptor;
import net.foben.schematizer.distances.ResDescriptor;
import net.foben.schematizer.mysql.MySQLDAO;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

public class ComputeDistances {

	static Logger _log = LoggerFactory.getLogger(ComputeDistances.class);
	static String del = ";";
	
	public static void main(String[] args) throws IOException {
		int top = Integer.parseInt(args[0]);
		
		JaccardCommentsSim sim = new JaccardCommentsSim();
		
		WrappedRepo repo = new WrappedRepo();
		repo.addFile("src/main/resources/vocabularies.nq");
		LabeledResDescriptor[] candArray = getCandidates(top, "src/main/resources/stats/sorted_types", repo.getConnection());
		repo.close();
		
		MySQLDAO dao = new MySQLDAO("JaccardTop30Types");
		
		long total = candArray.length * (candArray.length + 1) / 2;
		long count = 0;
		
		for(int rowi = 0;  rowi < candArray.length; rowi++){
			LabeledResDescriptor row = candArray[rowi];
			for(int coli = rowi; coli < candArray.length; coli ++){
				LabeledResDescriptor column = candArray[coli];
				double simil = sim.getSim(row, column);
				dao.queue(row, column, simil);
				if(++count%10000 == 0) _log.info(((int)(count*10000d/total))/100d + "%");
			}
		}
		dao.terminate();
		
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
