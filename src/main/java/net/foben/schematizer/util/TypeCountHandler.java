package net.foben.schematizer.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.foben.schematizer.offline.MemRepository;
import net.foben.schematizer.stats.PLDStat;
import static net.foben.schematizer.Environment.*;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeCountHandler implements RDFHandler {
	private int stcount = 0;
	private IPLDReducer reducer;
	private Logger _log;
	Map<String, PLDStat> map;
	String outfile;
	
	
	public TypeCountHandler(Map<String, PLDStat> map, String outfile){
		_log = LoggerFactory.getLogger(TypeCountHandler.class);
		this.map = map;
		reducer = new RestrictingPLDReducer("src/main/resources/TopLevelDomains");
		this.outfile = outfile;
	}
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outfile));
			for(String key : map.keySet()){
				PLDStat stat = map.get(key);
				out.write(stat.toString());
				out.flush();
			}
			out.close();
		} catch (IOException e) {
			_log.error("Exception occurred while writing statistics!");
			_log.error(e.getStackTrace().toString());
		}
		
	}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {	}

	@Override
	public void handleNamespace(String arg0, String arg1)
			throws RDFHandlerException { }

	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		try {
			stcount++;
			if(stcount%1000000 == 0)_log.info(stcount/1000000 + " million lines parsed");
			URI pred = arg0.getPredicate();
			Value obj = arg0.getObject();
			String dataset = reducer.getPLD(arg0.getContext().stringValue());
			
			map.get(dataset).incr();
			if(pred.stringValue().equals(RDFTYPE)){
				if(obj instanceof Resource)	map.get(dataset).incrType(obj.stringValue());
			}
			
		} catch (IllegalArgumentException ia){
			_log.warn("IAE occured: " + ia.getMessage());
		}
	}

}
