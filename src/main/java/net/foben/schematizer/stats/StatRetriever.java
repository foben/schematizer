package net.foben.schematizer.stats;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.model.SimpleResourceDescriptor;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
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

public class StatRetriever {

    static Repository repo;
    static RepositoryConnection con;
    static Logger _log;

    public static void main(String[] args) throws RepositoryException, IOException {
	_log = LoggerFactory.getLogger(StatRetriever.class);

	boolean properties = false;
	int top = 500;

	List<ComparableResourceDescriptor> resources = Arrays.asList(ModelAccess.getCandidates(
		SimpleResourceDescriptor.class, top, properties));
	Map<String, URI> statsMap = properties ? propertyStatistics : classStatistics;

	LinkedHashMap<ComparableResourceDescriptor, Map<String, List<Value>>> data = new LinkedHashMap<ComparableResourceDescriptor, Map<String, List<Value>>>();
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

	for (ComparableResourceDescriptor resource : resources) {
	    HashMap<String, List<Value>> currentdata = new HashMap<String, List<Value>>();
	    String resURI = resource.getURI();
	    data.put(resource, currentdata);

	    for (String property : statsMap.keySet()) {
		addObjectsToMap(property, currentdata, con.getStatements(new URIImpl(resURI), statsMap.get(property),
			(Value) null, false, (Resource) null));
	    }
	    _log.info("finished {}", resURI);
	}

	serializeToCSV(data, statsMap.keySet());
    }

    private static void serializeToCSV(LinkedHashMap<ComparableResourceDescriptor, Map<String, List<Value>>> data,
	    Set<String> dataitems) {
	String d = CSVDELIM;
	try (BufferedWriter br = new BufferedWriter(new FileWriter("temp/statsoutNewProps.csv"))) {
	    br.write(d + "distinct PLDs");
	    br.write(d + "instances");
	    for (String item : dataitems) {
		br.write(d + item);
	    }
	    br.newLine();

	    for (ComparableResourceDescriptor resource : data.keySet()) {
		br.write(resource.getURI() + d);
		br.write(resource.getDatasets() + d);
		br.write(resource.getTotal() + d);
		Map<String, List<Value>> datamap = data.get(resource);
		for (String item : dataitems) {
		    List<Value> values = datamap.get(item);
		    if (values != null) {
			br.write(values.size() + d);
		    } else {
			br.write(0 + d);
		    }
		}
		br.newLine();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public static void addObjectsToMap(String property, HashMap<String, List<Value>> map,
	    RepositoryResult<Statement> statements) throws RepositoryException {
	List<Value> values = new ArrayList<Value>();
	while (statements.hasNext()) {
	    Statement st = statements.next();
	    values.add(st.getObject());
	}
	map.put(property, values);
    }
}
