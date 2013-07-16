package net.foben.schematizer.parse;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.foben.schematizer.stats.PropertyStat;
import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.RestrictingPLDReducer;
import static net.foben.schematizer.Environment.*;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeCountHandler implements RDFHandler {
	private int stcount = 0;
	private IPLDReducer reducer;
	private Logger _log;
	Map<String, PropertyStat> map;
	String outfile;
	private int gt = 0;
	long last, now;
	
	public TypeCountHandler(String outfile, IPLDReducer reducer){
		this._log = LoggerFactory.getLogger(TypeCountHandler.class);
		this.map = new HashMap<String, PropertyStat>(900, 0.9f);
		this.reducer = reducer;
		this.outfile = outfile;
	}
	
	public TypeCountHandler(String outfile){
		this(outfile, new RestrictingPLDReducer("src/main/resources/BTCReductions"));
	}
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		System.out.println(gt);
		try {
			FileWriter out = new FileWriter(outfile);
			for(String key : map.keySet()){
				PropertyStat stat = map.get(key);
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
			
			gt++;
			if(map.containsKey(dataset))map.get(dataset).incr();
			else{
				map.put(dataset, new PropertyStat(dataset));
				map.get(dataset).incr();
			}
			
			if(pred.stringValue().equals(RDFTYPE)){
				if(obj instanceof Resource)	map.get(dataset).incrType(obj.stringValue());
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
