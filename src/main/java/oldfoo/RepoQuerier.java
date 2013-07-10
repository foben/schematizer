package oldfoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.n3.N3Writer;
import org.openrdf.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("unused")
public class RepoQuerier {
	private static Repository repo;
	private static RepositoryConnection con;
	
	private static final String qGraphsPerDataset = "SELECT ?g WHERE {<%s> <http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph> ?g . }";
	private static final String qNumberOfDatasets = "SELECT (COUNT(?d) as ?ds) WHERE {?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> .}";
	private static final String qDatasets = "SELECT ?d WHERE {?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> .}";
	
	private static Logger _log;
	
	public static void main(String[] args) throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException, RDFHandlerException{
		_log = LoggerFactory.getLogger(RepoQuerier.class);
		File dataDir = new File("repositories/mappingrepo");
		repo = new SailRepository( new NativeStore(dataDir) );
		repo.initialize();
		
		con = repo.getConnection();
		
		//performGraphQuery();		
		
		String actquery;
		actquery = qNumberOfDatasets;
		TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, actquery);
		TupleQueryResult res = q.evaluate();
		
		BufferedWriter br = new BufferedWriter(new FileWriter("repoquery.nt"));
		while(res.hasNext()){
			BindingSet result = res.next();
			System.out.println(result);
			br.write(result.toString());
			br.newLine();
		}
		br.close();
	}
	
	static void performGraphQuery() throws RepositoryException, MalformedQueryException, QueryEvaluationException, IOException, RDFHandlerException{
		String graphQuery = "CONSTRUCT { ?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/numberOfGraphs> ?m } " +
				"WHERE { SELECT distinct ?d (COUNT(?g) as ?m)" +
						"WHERE {?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph> ?g. }" +
						"group by ?d order by desc(?m)}";
		GraphQuery gq = con.prepareGraphQuery(QueryLanguage.SPARQL, graphQuery);
		
		GraphQueryResult gres = gq.evaluate();
		
		N3Writer n3 = new N3Writer(new FileWriter("repoquery_graph.nt"));
		n3.startRDF();
		while(gres.hasNext()){
			Statement result = gres.next();
			System.out.println(result);
			n3.handleStatement(result);
			
		}
		n3.endRDF();
		System.exit(0);
	}
}
