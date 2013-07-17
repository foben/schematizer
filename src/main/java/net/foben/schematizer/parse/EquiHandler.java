package net.foben.schematizer.parse;

import java.io.FileWriter;
import java.io.IOException;
import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.RestrictingPLDReducer;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.foben.schematizer.Environment.*;

public class EquiHandler implements RDFHandler {
	
	private static final String owleCStr = "http://www.w3.org/2002/07/owl#equivalentClass";
	private static final String owlsAStr = "http://www.w3.org/2002/07/owl#sameAs";
	private static final URI owleC = new URIImpl(owleCStr);
	private static final URI owlsA = new URIImpl(owlsAStr);
	
	private IPLDReducer reducer;
	private Logger _log;
	private double now, last;
	private FileWriter fout;
	int stcount, writecount;
	String outfile;	
	
	public EquiHandler(String outfile, IPLDReducer reducer) throws IOException{
		this._log = LoggerFactory.getLogger(EquiHandler.class);
		this.reducer = reducer;
		this.outfile = outfile;
		this.stcount = 0;
		this.writecount = 0;
		fout = new FileWriter(this.outfile);
	}
	
	public EquiHandler(String outfile) throws IOException{
		this(outfile, new RestrictingPLDReducer("src/main/resources/BTCReductions"));
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			fout.flush();
			fout.close();
		} catch (IOException e) {
			_log.error("Error closing handler");
			e.printStackTrace();
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
			
			//pred.equals(owlsA) || 
			if(pred.equals(owlsA)){
				writecount++;
				Value obj = arg0.getObject();
				Resource subj = arg0.getSubject();
				String dataset = reducer.getPLD(arg0.getContext().stringValue());
				try {
					fout.write(String.format("<%s> <%s> <%s> <%s> . \n", subj.stringValue(), pred.stringValue(), obj.stringValue(), URI_IDS + dataset));
				} catch (IOException e) {
					_log.error("Write operation failed!");
					//e.printStackTrace();
				}
				if(writecount%10000 == 0){
					_log.info("Flushing 10000 lines");
					fout.flush();					
				}
			}			
			
		} catch (IllegalArgumentException ia){
			_log.warn("IAE occured: " + ia.getMessage());
		} catch (IOException e) {
			_log.error("Flushing failed!");
			e.printStackTrace();
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
