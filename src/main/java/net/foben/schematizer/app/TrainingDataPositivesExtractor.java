package net.foben.schematizer.app;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.ntriples.NTriplesWriter;

public class TrainingDataPositivesExtractor {

    public static void main(String[] args) throws RDFParseException, RepositoryException, IOException,
	    InterruptedException, RDFHandlerException {
	WrappedRepo repo = new WrappedRepo();
	RepositoryConnection con = repo.getConnection();

	addAllSchemaFiles(con);
	con.add(new File(FILE_ALL_EQUIVALENCES), null, RDFFormat.TURTLE, (Resource) null);

	int top = 267;
	boolean properties = false;

	List<String> types = ModelAccess.getTopResources(top, properties);
	RepositoryResult<Statement> res;

	if (properties) {
	    res = repo.getStatements((String) null, OWLEQUIPROPERTY, (String) null, (String) null);
	} else {
	    res = repo.getStatements((String) null, OWLEQUICLASS, (String) null, (String) null);
	}

	RepositoryResult<Statement> resSA = repo.getStatements((String) null, "http://www.w3.org/2002/07/owl#sameAs",
		(String) null, (String) null);

	int results = 0;
	String outFile = properties ? "temp/equiPropertiesFromSchema.nt" : "temp/equiClassesFromSchema.nt";
	NTriplesWriter ntWriter = new NTriplesWriter(new FileOutputStream(outFile));
	ntWriter.startRDF();
	while (res.hasNext()) {
	    Statement st = res.next();
	    String sub = st.getSubject().stringValue();
	    String obj = st.getObject().stringValue();
	    if (types.contains(sub) && types.contains(obj)) {
		if (st.getSubject().equals(st.getObject()))
		    continue;
		ntWriter.handleStatement(st);
		results++;
	    }
	}
	while (resSA.hasNext()) {
	    Statement st = resSA.next();
	    String sub = st.getSubject().stringValue();
	    String obj = st.getObject().stringValue();
	    if (types.contains(sub) && types.contains(obj)) {
		if (st.getSubject().equals(st.getObject()))
		    continue;
		ntWriter.handleStatement(st);
		results++;
	    }
	}
	ntWriter.endRDF();
	System.out.println("-- " + results);
	System.out.println();
	repo.close();
    }

}
