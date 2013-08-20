package net.foben.schematizer.app;

import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import org.openrdf.rio.RDFParseException;

public class TrainingDataPositivesExtractor {

    public static void main(String[] args) throws RDFParseException, RepositoryException, IOException,
	    InterruptedException {
	WrappedRepo repo = new WrappedRepo();
	RepositoryConnection con = repo.getConnection();

	con.add(new File(FILE_ALL_EQUIVALENCES), null, RDFFormat.TURTLE, (Resource) null);
	con.add(new File(FILE_SCHEMADATA_LOV), null, RDFFormat.NQUADS, (Resource) null);
	con.add(new File(FILE_SCHEMADATA_AGGREGATED_CRAWL), null, RDFFormat.NQUADS, (Resource) null);

	List<String> types = ModelAccess.getTopTypes(500);

	RepositoryResult<Statement> res = repo.getStatements((String) null,
		"http://www.w3.org/2002/07/owl#equivalentClass", (String) null, (String) null);
	// RepositoryResult<Statement> res = con.getStatements((Resource) null,
	// new URIImpl("http://www.w3.org/2002/07/owl#equivalentClass"), (Value)
	// null, false, (Resource)null);
	int results = 0;
	BufferedWriter out = new BufferedWriter(new FileWriter("temp/equizz"));
	while (res.hasNext()) {
	    Statement st = res.next();
	    String sub = st.getSubject().stringValue();
	    String obj = st.getObject().stringValue();
	    if (types.contains(sub) && types.contains(obj)) {
		out.write(st.toString());
		out.newLine();
		results++;
	    }
	}
	out.flush();
	out.close();
	System.out.println("-- " + results);
	System.out.println();
	repo.close();
    }

}
