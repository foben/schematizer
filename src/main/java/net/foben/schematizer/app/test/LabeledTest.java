package net.foben.schematizer.app.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.openrdf.repository.RepositoryConnection;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import net.foben.schematizer.distances.LabeledResDescriptor;
import net.foben.schematizer.util.WrappedRepo;

public class LabeledTest {

	public static void main(String[] args) throws IOException {
		WrappedRepo repo = new WrappedRepo();
		repo.addFile("src/main/resources/vocabularies.nq");
		SortedMultiset<LabeledResDescriptor> list = getCandidates(25, "src/main/resources/stats/sorted_types", repo.getConnection());
		for(LabeledResDescriptor lab : list){
			System.out.println(String.format("%s   %s   %s", lab.getLocalName(), lab.getLabels(), lab.getComments()));
		}
		repo.close();
	}
	
	private static SortedMultiset<LabeledResDescriptor> getCandidates(int top, String filename, RepositoryConnection con) throws IOException {

		TreeMultiset<LabeledResDescriptor> s = TreeMultiset.create();
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
		return s.descendingMultiset();
	}
}
