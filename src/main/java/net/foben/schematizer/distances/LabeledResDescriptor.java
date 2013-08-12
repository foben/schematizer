package net.foben.schematizer.distances;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class LabeledResDescriptor extends ResDescriptor {
	
	private static final long serialVersionUID = 1337L;
	private Set<Literal> comments;
	private Set<Literal> labels;
	private static String queryComments = "SELECT ?c WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#comment> ?c . }";
	private static String queryLabels = "SELECT ?c WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#label> ?c . }";
	
	/**
	 * Create Resource descriptor that extracts additional information from the supplied RepositoryConnection.
	 */
	public LabeledResDescriptor(String type, int total, int datasets, RepositoryConnection con) {
		super(type, total, datasets);
		this.comments = null;
		this.labels = null;
		try {
			TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, String.format(queryComments, type));
			TupleQueryResult res = q.evaluate();
			while(res.hasNext()){
				if(comments == null) comments = new HashSet<Literal>(1,1);
				Value v = res.next().getBinding("c").getValue();
				if(v instanceof Literal) comments.add((Literal) v);
				else _log.warn("Literal expected. Received: " + v.toString());
			}
			q = con.prepareTupleQuery(QueryLanguage.SPARQL, String.format(queryLabels, type));
			res = q.evaluate();
			while(res.hasNext()){
				if(labels == null) labels = new HashSet<Literal>(1,1);
				Value v = res.next().getBinding("c").getValue();
				if(v instanceof Literal) labels.add((Literal) v);
				else _log.warn("Literal expected. Received: " + v.toString());
			}
		}
		catch (RepositoryException e) {e.printStackTrace();}
		catch (MalformedQueryException e) {e.printStackTrace();}
		catch (QueryEvaluationException e) {e.printStackTrace();}
		
	}

	public Set<Literal> getComments() {
		return comments;
	}

	public Set<Literal> getLabels() {
		return labels;
	}

}
