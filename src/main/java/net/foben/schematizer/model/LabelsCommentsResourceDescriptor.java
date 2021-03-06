package net.foben.schematizer.model;

import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.util.ThreeTuple;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class LabelsCommentsResourceDescriptor extends SimpleResourceDescriptor {

    private static final long serialVersionUID = 1337L;
    private Set<Literal> comments;
    private Set<Literal> labels;
    private static String queryComments = "SELECT ?c WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#comment> ?c . }";
    private static String queryLabels = "SELECT ?c WHERE {<%s> <http://www.w3.org/2000/01/rdf-schema#label> ?c . }";

    public LabelsCommentsResourceDescriptor(ThreeTuple<String, Integer, Integer> tuple, RepositoryConnection con) {
	this(tuple.x, tuple.y, tuple.z, con);
    }

    /**
     * Create Resource descriptor that extracts additional information from the
     * supplied RepositoryConnection.
     */
    public LabelsCommentsResourceDescriptor(String type, int datasets, int total, RepositoryConnection con) {
	super(type, datasets, total);
	this.comments = null;
	this.labels = null;
	try {
	    TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, String.format(queryComments, type));
	    TupleQueryResult res = q.evaluate();
	    while (res.hasNext()) {
		if (comments == null)
		    comments = new HashSet<Literal>(1, 1);
		Value v = res.next().getBinding("c").getValue();
		if (v instanceof Literal)
		    comments.add((Literal) v);
		else
		    _log.warn("Literal expected. Received: " + v.toString());
	    }
	    q = con.prepareTupleQuery(QueryLanguage.SPARQL, String.format(queryLabels, type));
	    res = q.evaluate();
	    while (res.hasNext()) {
		if (labels == null)
		    labels = new HashSet<Literal>(1, 1);
		Value v = res.next().getBinding("c").getValue();
		if (v instanceof Literal)
		    labels.add((Literal) v);
		else
		    _log.warn("Literal expected. Received: " + v.toString());
	    }
	    System.out.print("");
	} catch (RepositoryException e) {
	    e.printStackTrace();
	} catch (MalformedQueryException e) {
	    e.printStackTrace();
	} catch (QueryEvaluationException e) {
	    e.printStackTrace();
	}

    }

    public Set<Literal> getComments() {
	return comments;
    }

    public Set<Literal> getLabels() {
	return labels;
    }

    public String toString() {
	return String.format("%s  (%s, %s)", this.getURI(), this.getDatasets(), this.getTotal());
    }

}
