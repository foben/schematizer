package net.foben.schematizer.app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.rio.nquads.NQuadsWriter;
import org.openrdf.sail.memory.MemoryStore;

public class Vocabularizer {

	public static void main(String[] args) throws RepositoryException, IOException, RDFParseException, RDFHandlerException {
		Repository repo = new SailRepository(new MemoryStore());
		repo.initialize();
		RepositoryConnection con = repo.getConnection();
		configureParser(con);
		int count = 0;
		
		Set<String> fails = new HashSet<String>();
		for(String arg : args){
			System.out.println(arg + String.format(" (%s of %s)", ++count, args.length));
			if(arg.endsWith("media")|| arg.endsWith("moac") || arg.endsWith("odrs") || arg.endsWith("opmo") 
					|| arg.endsWith("sim") || arg.endsWith("teach") || arg.endsWith("tisc")
					|| arg.endsWith("cold")){
				try{
					con.add(new File(arg), null, RDFFormat.RDFA);
					continue;
				} catch (RDFParseException e3){
					fails.add(arg);
					continue;
				}
			}
			
			
			try{
				con.add(new File(arg), null, RDFFormat.RDFXML);
			} catch (RDFParseException e0){
				try {
					con.add(new File(arg), null, RDFFormat.N3);
					System.out.println("Worked with n3");
				} catch (RDFParseException e1) {
					try {
						con.add(new File(arg), null, RDFFormat.TURTLE);
						System.out.println("Worked with Turtles");
					} catch (RDFParseException e2) {
						try{
							con.add(new File(arg), null, RDFFormat.RDFA);
							continue;
						} catch (RDFParseException e3){
							fails.add(arg);
							e3.printStackTrace();
						}
					}
				}
			}
			
		}
		
		con.export(new NQuadsWriter(new FileWriter("repoout")), (Resource) null);
		
		System.out.println("done");
		System.out.println();
		for(String s : fails){
			System.out.println(s);
		}
	}

	private static void configureParser(RepositoryConnection con) {
		con.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		con.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
		con.getParserConfig().set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);
		con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);
		con.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
		con.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
		con.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
		con.getParserConfig().addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
		
	}

}
