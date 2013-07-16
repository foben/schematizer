package net.foben.schematizer.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.foben.schematizer.stats.LOVStat;
import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.RestrictingPLDReducer;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LOVHandler implements RDFHandler {
	private int stcount = 0;
	private IPLDReducer reducer;
	private Logger _log;
	private Set<String> namespaces;
	private double now, last;
	Map<String, LOVStat> map;
	String outfile;
	
	
	public LOVHandler(String outfile, IPLDReducer reducer) throws IOException{
		this._log = LoggerFactory.getLogger(LOVHandler.class);
		this.map = new HashMap<String, LOVStat>(900, 0.9f);
		this.reducer = reducer;
		this.outfile = outfile;
		namespaces = new HashSet<String>();
		
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/lovvocabs"));
		String ns;
		while((ns = in.readLine()) != null){
			namespaces.add(ns);
		}
		in.close();
	}
	
	public LOVHandler(String outfile) throws IOException{
		this(outfile, new RestrictingPLDReducer("src/main/resources/BTCReductions"));
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			FileWriter out = new FileWriter(outfile);
			for(String key : map.keySet()){
				LOVStat stat = map.get(key);
				stat.write(out);
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

	private void add(String dataset, String namespace){
		if(map.containsKey(dataset)){
			map.get(dataset).incrVoc(namespace);
		}
		else{
			map.put(dataset, new LOVStat(dataset));
			map.get(dataset).incrVoc(namespace);
		}
	}
	
	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		
		try {
			stcount++;
			if(stcount%1000000 == 0){
				_log.info(stcount/1000000 + " million lines parsed in " + measure());
			}
			URI pred = arg0.getPredicate();
			Value obj = arg0.getObject();
			String dataset = reducer.getPLD(arg0.getContext().stringValue());
			
			String predNS = pred.getNamespace();
			if(namespaces.contains(predNS)){
				add(dataset, predNS);
			}
			
			if(obj instanceof URI){
				String objNS = ((URI)obj).getNamespace();
				if(namespaces.contains(objNS)){
					add(dataset, objNS);
				}
			}
			
		} catch (IllegalArgumentException ia){
			_log.warn("IAE occured: " + ia.getMessage());
		}
	}
	
	private String measure() {
		now = System.nanoTime();
		double dur = (now - last) / 1000000000d;
		String result = ("" + dur);
		last = now;
		return result;
	}

}
