package net.foben.schematizer.app;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.stats.StatRetriever;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Foobar {

    static Logger _log;
    static Repository repo;
    static RepositoryConnection con;

    public static void main(String[] args) throws RepositoryException, IOException {
	_log = LoggerFactory.getLogger(StatRetriever.class);
	List<String> resources = ModelAccess.getTopResources(500);
	LinkedHashMap<String, Map<String, List<Value>>> data = new LinkedHashMap<String, Map<String, List<Value>>>();
	repo = new SailRepository(new MemoryStore());
	repo.initialize();
	con = repo.getConnection();
	configureTolerantParser(con);

	_log.info("Starting data import");
	for (String filename : ALL_SCHEMA_MAP.keySet()) {
	    try {
		con.add(new File(filename), null, ALL_SCHEMA_MAP.get(filename), (Resource) null);
	    } catch (RDFParseException e) {
		e.printStackTrace();
		_log.error("error parsing {} with {}", filename, ALL_SCHEMA_MAP.get(filename));
	    }
	    _log.info("finished importing {}", filename);
	}
	_log.info("data import complete");

	RepositoryResult<Statement> res = con.getStatements(new URIImpl("http://usefulinc.com/ns/doap#SVNRepository"),
		new URIImpl("http://www.w3.org/2000/01/rdf-schema#comment"), null, false, (Resource) null);

	int count = 0;
	while (res.hasNext()) {
	    Statement st = res.next();
	    count++;
	}
	System.out.println(count);
    }
}
