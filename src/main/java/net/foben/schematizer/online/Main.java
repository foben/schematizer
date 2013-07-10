package net.foben.schematizer.online;
import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import de.uni_mannheim.informatik.dws.dwslib.virtuoso.Query;

@SuppressWarnings("unused")
public class Main {
	
	private static final String qTypesPerGraph 	= "SELECT distinct ?c ?g  WHERE {GRAPH ?g { ?a a ?c} } ";
	private static final String qTypesAll		= "SELECT distinct ?t WHERE { GRAPH ?g { ?s  a ?t . } }";
	private static final String qGraphsAll 		= "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o . } }";
	private static final String qGraphsAllLimit	= "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o . } } LIMIT 500000";
	private static final String qGraphsByType 	= "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?a a ?c .} }";
	private static final String qGraphsAll2 	= "SELECT DISTINCT ?g WHERE { GRAPH ?g { ?s ?p ?o . } }";
	
	private static final String GraphToQuery = "http://128.32.78.9/ontology";
	private static final String qAllTriplesInGraph = String.format("SELECT * WHERE { GRAPH %s {?s ?p ?o .} }", GraphToQuery);
	
	private static final String query = qAllTriplesInGraph;

	public static void main(String[] args) throws SQLException, IOException {
		String filename = "out.nt";
		boolean printResults = true;
		if (args.length > 0){
			filename = args[0];
			if(args.length > 1){
				if(args[1].equals("false")) printResults = false;
			}
			
		}	
				
		String[] qArgs = {"wifo5-32.informatik.uni-mannheim.de:1110",
						  "user",
						  "dws2013",
						  filename,
						  query
						  };
		Query.main(qArgs);
		
		if(printResults){
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			int i = 0;
			while((line = br.readLine()) != null){
				System.out.println(i++ + " " + line);
			}
			br.close();
		}
		System.out.println("======================");
		System.out.println("====all finished!!====");
		
	}

}
