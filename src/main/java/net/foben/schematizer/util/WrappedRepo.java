package net.foben.schematizer.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.nquads.NQuadsWriter;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WrappedRepo implements IRepo, Closeable {
	
	Repository repo;
	RepositoryConnection con;
	boolean working;
	Logger _log;
	List<Statement> queue;
	public WrappedRepo(String str){
		this(new NativeStore(new File(str)));
	}
	
	public WrappedRepo(){
		this(new MemoryStore());
	}
	public WrappedRepo(Sail sail){
		_log = LoggerFactory.getLogger(WrappedRepo.class);
		queue = new ArrayList<Statement>();
		repo = new SailRepository(sail);
		working = true;
		try {
			repo.initialize();
			con = repo.getConnection();
		} catch (RepositoryException e) {
			working = false;
			_log.error("Failed to create repository!");
		}
		_log.debug("Repository successfully created");
	}
	@Override
	public void close() {
		try {
			con.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	public void queue(Resource subj, URI pred, Value obj) {
		queue.add(new StatementImpl(subj, pred, obj));
		if(queue.size() > 100000) flushQueue();
	}
	
	public void flushQueue(){
		_log.info(String.format("Flushing %s statements into repo!", queue.size()));
		try {
			con.add(queue, (Resource) null);
			queue.clear();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TupleQueryResult sparql(String query){
		try {
			TupleQuery q = con.prepareTupleQuery(QueryLanguage.SPARQL, query);
			TupleQueryResult res = q.evaluate();
			return res;
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean add(Statement st) {
		return add(st, (Resource) null);
	}
	
	public boolean add(Statement st, Resource context) {
		boolean result = true;
		if(!working){
			_log.warn("Repository not operable");
			return false;
		}
		try {
			con.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
			con.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
			con.add(st, context);
		} catch (RepositoryException e) {
			_log.error("Exception while adding statement");
			result = false;
		}
		return result;
	}
	
	@Override
	public boolean remove(Statement st) {
		return remove(st, (Resource) null);
	}
	
	public boolean remove(Statement st, Resource context) {
		boolean result = true;
		if(!working){
			_log.warn("Repository not operable");
			return false;
		}
		try {
			con.remove(st, context);
		} catch (RepositoryException e) {
			_log.error("Exception while removing statement");
			result = false;
		}
		return result;
	}
	
	public boolean addFile(String filename){
		boolean result = true;
		File file = new File(filename);
		if(!file.exists()){
			_log.error("File does not exist!");
			return false;
		}
		try {
			con.add(file, null, RDFFormat.NQUADS);
		} catch (RDFParseException e) {
			_log.error("Error parsing supplied file!");
			result = false;
		} catch (RepositoryException e) {
			_log.error("RepositoryException occured while processing file");
			result = false;
		} catch (IOException e) {
			_log.error("IOException occured while processing file");
			result = false;
		}
		return result;		
	}
	
	public RepositoryResult<Statement> getStatements(String subj, String pred, String obj, String... contexts){
		URIImpl _subj = subj == null ? null : new URIImpl(subj);
		URIImpl _pred = pred == null ? null : new URIImpl(pred);
		URIImpl _obj  = obj  == null ? null : new URIImpl(obj);
		if(contexts.length > 0 && contexts[0] != null) {
			Resource[] _contexts = new URIImpl[contexts.length];
			for(int i = 0; i < contexts.length; i++){
				_contexts[i] = new URIImpl(contexts[i]);				
			}
			return getStatements(_subj, _pred, _obj, _contexts);
		}
		else{
			return getStatements(_subj, _pred, _obj, (Resource) null);
		}
		
	}
	
	public RepositoryResult<Statement> getStatements(Resource subj, URI pred, Value obj, Resource... contexts){
		RepositoryResult<Statement> result = null;
		try {
			result = con.getStatements(subj, pred, obj, false, contexts);
		} catch (RepositoryException e) {
		}		
		return result;
	}
	
	public void toFile(String filename){
		flushQueue();
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(filename);
			con.export(new NTriplesWriter(fout), (Resource)null);
		} catch (RepositoryException e) {
		} catch (RDFHandlerException e) {
		} catch (FileNotFoundException e) {
		} finally {
			try {
				fout.close();
			} catch (IOException e) {
			}
		}
	}

}
