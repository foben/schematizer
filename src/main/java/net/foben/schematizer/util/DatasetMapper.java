package net.foben.schematizer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.foben.schematizer.Environment.*;

public class DatasetMapper {
	
	private String graphsFile;
	private HashMap<String, List<String>> mappings;
	private boolean mappingSuccess = false;
	private double runtime = -1;
	private int datasets   = -1;
	private int graphs     = -1;
	private Logger _log;
	
	public DatasetMapper(String graphsFile){
		_log = LoggerFactory.getLogger(DatasetMapper.class);
		this.graphsFile = graphsFile;
		mappings = new HashMap<String, List<String>>();
	}
	
	public boolean createMappings(){
		return createMappings(null);
	}
	
	public boolean createMappings(IPLDReducer reducer){
		if(reducer == null) reducer = new RestrictingPLDReducer();
		long start = System.nanoTime();
		boolean result = true;
		BufferedReader br = null;
		int count = 0;
		try {
			br = new BufferedReader(new FileReader(graphsFile));
			String line;
				while ((line = br.readLine()) != null){
					count++;
					String graph = line;
					String dataset = reducer.getPLD(line);
					if(!mappings.containsKey(dataset)){
						ArrayList<String> newL = new ArrayList<String>();
						newL.add(graph);
						mappings.put(dataset, newL);
					}
					else {
						mappings.get(dataset).add(graph);
					}
					if(count%1000000 == 0) _log.info((count/1000000) + " million graphs parsed");
				}
		} catch (FileNotFoundException e) {
			result = false;
			e.printStackTrace();
		} catch (IOException e) {
			result = false;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				result = false;
			}
		}
		runtime = result ? (System.nanoTime()- start)/Math.pow(10, 9) : -1;
		datasets = result ? mappings.keySet().size() : -1;
		graphs = result ? count : -1;
		mappingSuccess = result;
		return result;
	}
	
	public void printStats(){
		if(!mappingSuccess){
			_log.warn("No successful mapping yet!");
			return;
		}
		_log.info("Graphs Processed  : " + graphs);
		_log.info("Resulting Datasets: " + datasets);
		_log.info("Runtime	          : " + runtime);
	}
	
	public HashMap<String, List<String>> getMappings() {
		return mappings;
	}
	
	
	
	public void exportMappingsInternal(String filename) throws RepositoryException, RDFHandlerException, FileNotFoundException {
		if(!mappingSuccess){
			_log.warn("No successful mapping yet!");
			return;
		}
		Repository repo = new SailRepository( new MemoryStore() );
		repo.initialize();
		List<Statement> statements = new ArrayList<Statement>();
		URI pred = new URIImpl(URI_HASGRAPH);
		URI a = new URIImpl(RDFTYPE);
		URI ds = new URIImpl(URI_DATASET);
		int count = 0;
		for(String key : mappings.keySet()){
			URI subj = new URIImpl(URI_IDS + key);
			List<String> mappedURIs = mappings.get(key);
			statements.add(new StatementImpl(subj, a, ds));
			for (String mappedURI : mappedURIs) {
				statements.add(new StatementImpl(subj, pred, new URIImpl(mappedURI)));
			}
			System.out.println(++count);
			if(count%50 == 0 || true){
				RepositoryConnection con = repo.getConnection();
				con.add(statements, (Resource) null);
				con.commit();
				statements.clear();
				con.close();
			}
		}
		RepositoryConnection con = repo.getConnection();
		con.add(statements, (Resource) null);
		con.commit();
		RDFHandler rdfxmlWriter = new NTriplesWriter(new FileOutputStream(filename));
		con.export(rdfxmlWriter, (Resource)null);
		con.close();
	}

}
