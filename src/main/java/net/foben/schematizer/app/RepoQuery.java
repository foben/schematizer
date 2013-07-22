package net.foben.schematizer.app;

import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import net.foben.schematizer.util.WrappedRepo;

public class RepoQuery {
	
	
	static String getAllDatasets = "SELECT * WHERE { ?a a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> .}";
	//(SUM(?c) AS ?total)
	static String getTotalStatements = "SELECT ?c WHERE { ?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> ." +
														 "?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/statementCount> ?c." +
														"	}";
	static String getTotalStatementsAgg = "SELECT (SUM(?c) AS ?total) WHERE { ?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> ." +
			 "?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/statementCount> ?c." +
			"	}";
	
	static String getTotalSt = "SELECT * WHERE { ?a <http://dws.informatik.uni-mannheim.de/lodschema/properties/statementCount> ?b.}";
	
	static String getHighVals = "SELECT * WHERE { ?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> . " +
												 "?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/hasTypeSpec> ?ts ." +
												 "?ts a <http://dws.informatik.uni-mannheim.de/lodschema/types/TypeSpec> ." +
												 "?ts <http://dws.informatik.uni-mannheim.de/lodschema/properties/describesType> ?type ." +
												 "?ts <http://dws.informatik.uni-mannheim.de/lodschema/properties/typeOccurences> ?oc ." +
												 "FILTER (?oc > 10000)" +
												 "FILTER (?d != <http://dws.informatik.uni-mannheim.de/lodschema/ids/dbpedia.org>)" +
												 "} ";
	static String getStsPerDS = "SELECT * WHERE {?d a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> ." +
												 "?d <http://dws.informatik.uni-mannheim.de/lodschema/properties/statementCount> ?c." +
												 "} ORDER BY desc( ?c) LIMIT 15"; 

	
	
	/**
	 * @param args
	 * @throws QueryEvaluationException 
	 */
	public static void main(String[] args) throws QueryEvaluationException {
		WrappedRepo repo = new WrappedRepo("repooo");
		String actquery;
		if(args.length < 1) actquery = getStsPerDS;
		else actquery = args[0];
		TupleQueryResult res = repo.sparql(actquery);
		while(res.hasNext()){
			System.out.println(res.next());
		}
		repo.close();
	}

}
