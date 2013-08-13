package net.foben.schematizer.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

public class ModelAccess {
	
	private static final String lovfile = "src/main/resources/lov/vocabularies.csv";
	private static final String schemaRDFile = "src/main/resources/vocabularies.nq";
	private static final String typesFile = "src/main/resources/stats/sorted_types"; 
	
	
	private static Logger _log = LoggerFactory.getLogger(ModelAccess.class);
	
	
	public static Set<LOVVocab> getLOVVocabularies(){
		Set<LOVVocab> result = new HashSet<LOVVocab>(400);
		String line = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(lovfile));
			while((line = in.readLine()) != null){
				String[] arr = line.split(";");
				result.add(new LOVVocab(arr[0], arr[1], arr[0]));
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			_log.error("Missing fields in this line: {}", line);
		}
		return result;
	}
	
	public static Set<String> getLOVNamespaces(){
		Set<String> result = new HashSet<String>(400);
		String line = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(lovfile));
			while((line = in.readLine()) != null){
				String[] arr = line.split(";");
				result.add(arr[1]);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			_log.error("Missing fields in this line: {}", line);
		}
		return result;
	}


	public static ComparableResourceDescriptor[] getCandidates(Class<?> expected, int top) {
		if(expected.equals(SimpleResourceDescriptor.class)){
			try{				
				TreeMultiset<SimpleResourceDescriptor> s = TreeMultiset.create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());
				BufferedReader in = new BufferedReader(new FileReader(typesFile));
				String line;
				int count = 0;
				while((line = in.readLine()) != null && ++count <= top){
					SimpleResourceDescriptor t = null;
					try{
						String[] fields = line.split(" ");
						t = new SimpleResourceDescriptor(fields[0], Integer.parseInt(fields[1]), Integer.parseInt(fields[2]));
					} catch (Exception e){
						e.printStackTrace();
					}
					if(t != null){
						s.add(t);
					}
				}
				in.close();
				return s.toArray(new SimpleResourceDescriptor[0]);
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		else if (expected.equals(LabelsCommentsResourceDescriptor.class)){
			try{
				WrappedRepo repo = new WrappedRepo();
				repo.addFile("src/main/resources/vocabularies.nq");
				repo.addFile("src/main/resources/all_equis");
				RepositoryConnection con = repo.getConnection();
				
				TreeMultiset<LabelsCommentsResourceDescriptor> s = TreeMultiset.create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());
				BufferedReader in = new BufferedReader(new FileReader(typesFile));
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
			} catch (IOException e){
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
}
