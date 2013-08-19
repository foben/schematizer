package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.model.ModelAccess;

import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import static net.foben.schematizer.Environment.DataFiles.*;

public class LOVFromTypes {

	static HTTPRepository rep = null;
	static RepositoryConnection con = null;

	public static void main(String[] args) throws IOException {
		int top = 500;

		Set<String> lovnss = ModelAccess.getLOVNamespaces();
		BufferedReader br = new BufferedReader(new FileReader(FILE_PROPERTIES));
		HashMap<String, Set<String>> mappings = new HashMap<String, Set<String>>();
		String line = null;
		int count = 0;
		while ((line = br.readLine()) != null) {
			String res = line.split(" ")[0];
			URIImpl type = new URIImpl(res);
			String typens = type.getNamespace();
			if (lovnss.contains(typens)) {
				if (!mappings.containsKey(typens)) {
					mappings.put(typens, new HashSet<String>());
				}
				mappings.get(typens).add(res);
				// System.out.println(res + "  (" + type.getNamespace() + ")");
			}
			count += 1;
			if (count >= top)
				break;
		}
		for (String key : mappings.keySet()) {
			String q = queryEP(key);
			String ln = q != null ? (new URIImpl(q)).getLocalName() : "NULL";
			System.out.println(ln + "  " + key);
			for (String val : mappings.get(key)) {
				System.out.println("  " + val);
			}
		}
		br.close();

	}

	public static String queryEP(String namespace) {
		int hashindex = namespace.indexOf("#");
		if (hashindex > 0) {
			namespace = namespace.substring(0, hashindex);
		}

		try {
			if (rep == null) {
				rep = new HTTPRepository("http://lov.okfn.org/endpoint/lov");
				rep.initialize();
				con = rep.getConnection();
			}

			String queryString = "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>"
					+ "PREFIX dcterms:<http://purl.org/dc/terms/>"
					+ "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>"
					+ "PREFIX owl:<http://www.w3.org/2002/07/owl#>"
					+ "PREFIX skos:<http://www.w3.org/2004/02/skos/core#>"
					+ "PREFIX foaf:<http://xmlns.com/foaf/0.1/>"
					+ "PREFIX void:<http://rdfs.org/ns/void#>"
					+ "PREFIX bibo:<http://purl.org/ontology/bibo/>"
					+ "PREFIX vann:<http://purl.org/vocab/vann/>"
					+ "PREFIX voaf:<http://purl.org/vocommons/voaf#>"
					+ "PREFIX frbr:<http://purl.org/vocab/frbr/core#>"
					+ "PREFIX lov:<http://lov.okfn.org/dataset/lov/lov#> "
					+ "SELECT * WHERE{ "
					+ " ?a dcterms:hasPart <%s> ."
					+ "} limit 500000000000";

			String query = String.format(queryString, namespace);
			TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL,
					query);
			TupleQueryResult result = tupleQuery.evaluate();
			while (result.hasNext()) {
				BindingSet set = result.next();
				return (set.getBinding("a").getValue().stringValue());
			}

		} catch (RepositoryException e1) {

		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}

		return null;

	}

}
