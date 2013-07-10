package oldfoo;

import static net.foben.schematizer.Environment.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.nativerdf.NativeStore;


public class TypeMapper {

	public static final boolean createMappings = true;
	public static Repository repo;
	public static RepositoryConnection con;
	static HashMap<String, String> mappings;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, RepositoryException, RDFHandlerException, ClassNotFoundException {
		if(args.length > 0 && args[0].equals("serialize")){
			mappings = getMappings();
			System.out.println("Serializing HashMap...");
			FileOutputStream fos = new FileOutputStream("hashser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(mappings);
		    oos.close();
		    System.out.println("...Serialization complete!");
		}
		else{
			System.out.println("Reading HashMap from file...");
			FileInputStream fis = new FileInputStream("hashser");
		    ObjectInputStream ois = new ObjectInputStream(fis);
		    mappings = (HashMap<String,String>)ois.readObject();
		    ois.close();
		    System.out.println("...Reading complete!");
		}
			
		if(createMappings){
			//mappings = getMappings();
			BufferedReader br = new BufferedReader(new FileReader("subset_type.nt"));
			File dataDir = new File("repositories/typemappingrepo");
			repo = new SailRepository( new NativeStore(dataDir) );
			repo.initialize();
			con = repo.getConnection();
			Set<Statement> statements = new HashSet<Statement>();
			Set<String> unknownGraphs = new HashSet<String>();
			final URI hasClass = new URIImpl(URI_HASCLASS);
			int count = 0;
			int duplicates = 0;
			int comits = 0;
			int unknowns = 0;
			
			String line = br.readLine();  //Once to remove first line
			while ((line = br.readLine()) != null){
				count++;
				int firstI = line.indexOf("\"");
				int secondI = line.indexOf("\"", firstI + 1);
				int thirdI = line.indexOf("\"", secondI + 1);
				int fourthI = line.indexOf("\"", thirdI + 1);
				String type = line.substring(firstI+1, secondI);
				String graph = line.substring(thirdI+1, fourthI);
				String dataset = mappings.get(graph);
				if(dataset == null){
					unknowns++;
					unknownGraphs.add(graph);
					continue;
				}
				if(!statements.add(new StatementImpl(new URIImpl(dataset), hasClass, new URIImpl(type)))){
					duplicates++;
				}
				if(count % 1000 == 0){
					System.out.print("*");
					comits += commitStatements(statements);
					statements = new HashSet<Statement>();
					if(count % 1000000 == 0) System.out.println(String.format("\n %s Million!", (count/1000000)));
				}
			}
			System.out.println("****************************************");
			System.out.println("************FINISHED!!!!****************");
			System.out.println("****************************************");
			System.out.println("Total processed: " + count);
			System.out.println("Duplicates     : " + duplicates);
			System.out.println("Unknown graphs: " + unknownGraphs.size() + "     (" + unknowns + ")");
			System.out.println("Total comitted : " + comits + "     [" +(count-duplicates-unknownGraphs.size()-comits + "]" + "   [" +(count-duplicates-unknowns-comits + "]")));
			System.out.println(">>>> "  + (comits/(float)count));
			
			RDFHandler writer = new NTriplesWriter(new FileOutputStream("TypeMappingRepo.nt"));
			con.export(writer, (Resource)null);
			br.close();
			con.close();
			repo.shutDown();
			
//			for(String unk : unknownGraphs){
//				System.out.println(unk);
//			}
		}
	}
	
	private static int commitStatements(Set<Statement> statements) throws RepositoryException {
		int result = statements.size();
		con.add(statements, (Resource) null);
		con.commit();
		return result;
	}

	private static HashMap<String, String> getMappings() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("all_graphs.nt"));
		String line;
		int i = 0;
		int httpoff, slashoff, dots, dotoff;
		HashMap<String, String> mappings = new HashMap<String,String>();
		try{
			while ((line = br.readLine()) != null){
				i++;
				line = line.replace("\"", "");
				String oldLine = line;
				
				if (line.startsWith("http://")) httpoff = 7;
				else if (line.startsWith("https://")) httpoff = 8;
				else throw new IllegalArgumentException("Not a URI: " + line);
				slashoff = line.indexOf("/", httpoff);
				
				dots = StringUtils.countMatches(line.substring(0, slashoff), ".");
				
				dotoff = dots > 1 ? line.indexOf(".") + 1 : httpoff;
				line = URI_IDS + line.substring(dotoff, slashoff);
				mappings.put(oldLine, line);
				
				if(i%100000 == 0) System.out.print(".");
			}
		}
		finally {
			br.close();
		}
		System.out.println("\nTotal URIs: " + i);
		System.out.println("Total keys: " + mappings.keySet().size());
		return mappings;
	}

}
