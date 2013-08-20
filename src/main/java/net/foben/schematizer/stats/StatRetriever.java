package net.foben.schematizer.stats;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.foben.schematizer.model.ModelAccess;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatRetriever {

    static Repository repo;
    static RepositoryConnection con;
    static Logger _log;

    public static void main(String[] args) throws RepositoryException, RDFParseException, IOException {
	_log = LoggerFactory.getLogger(StatRetriever.class);
	List<String> resources = ModelAccess.getTopTypes(50);
	Map<String, Map<String, List<Value>>> data = new HashMap<String, Map<String, List<Value>>>();
	repo = new SailRepository(new MemoryStore());
	repo.initialize();
	con = repo.getConnection();
	configureTolerantParser(con);
	_log.info("Starting data import");
	con.add(new File(FILE_SCHEMADATA_AGGREGATED_CRAWL), null, RDFFormat.NQUADS, (Resource) null);
	con.add(new File(FILE_SCHEMADATA_LDSPIDER), null, RDFFormat.NQUADS, (Resource) null);
	_log.info("data import complete");
	for (String resource : resources) {
	    HashMap<String, List<Value>> currentdata = new HashMap<String, List<Value>>();
	    data.put(resource, currentdata);

	    for (String property : classStatistics.keySet()) {
		addObejctsToMap(property, currentdata, con.getStatements(new URIImpl(resource),
			classStatistics.get(property), (Value) null, false, (Resource) null));
	    }
	    _log.info("finished {}", resource);
	}
	System.out.println("sdf");
    }

    public static void addObejctsToMap(String property, HashMap<String, List<Value>> map,
	    RepositoryResult<Statement> statements) throws RepositoryException {
	List<Value> values = new ArrayList<Value>();
	while (statements.hasNext()) {
	    Statement st = statements.next();
	    values.add(st.getObject());
	}
	map.put(property, values);
    }
}
