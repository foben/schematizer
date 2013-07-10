package oldfoo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.foben.schematizer.Environment;

import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.nativerdf.NativeStore;

import de.uni_mannheim.informatik.dws.dwslib.virtuoso.Query;


import static net.foben.schematizer.Environment.*;

public class SchemaBuilder {
	File dataDir;
	RepositoryConnection con;
	Repository repo;
	String uri;
	String[] graphs;
	String identifier;
	
	public SchemaBuilder(String uri, String[] graphs) throws RepositoryException{
		this.uri = uri;
		this.graphs = graphs;
		this.identifier = Environment.getAlphaNumeric(12);
		dataDir = new File("dsrepos/" + identifier);
		repo = new SailRepository( new NativeStore(dataDir) );
		repo.initialize();
		con = repo.getConnection();
	}
	
	public void build() throws RepositoryException, QueryEvaluationException, RDFHandlerException, IOException, SQLException {
		List<Statement> statements = new ArrayList<Statement>();
		URI uriimpl = new URIImpl(uri);
		for (String graph : graphs){
			
			String[] qArgs = {"wifo5-32.informatik.uni-mannheim.de:1110",
					  "user",
					  "dws2013",
					  "tempout",
					  String.format(Q_CLASSES, graph)};
			Query.main(qArgs);
			
			BufferedReader br = new BufferedReader(new FileReader("tempout"));
			String line = br.readLine(); //Once to eliminate first line
			while ((line = br.readLine()) != null){
				line = line.replace("\"", "");
				statements.add(new StatementImpl(uriimpl, RES_HAS_CLASS, new URIImpl(line)));
			}
			br.close();
		}
		con.add(statements, (Resource)null);
		con.commit();
		File outFile = new File("dsfiles/" + identifier + ".nt");
		outFile.createNewFile();
		con.export(new NTriplesWriter(new FileOutputStream(outFile)), (Resource)null);
		con.close();
		System.out.println(uri + " finished");
	}
	
	@SuppressWarnings("unused")
	private TupleQueryResult getQueryResult(String query){
		TupleQueryResult res = null;
		try {
			TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			res = q.evaluate();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (OpenRDFException e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
