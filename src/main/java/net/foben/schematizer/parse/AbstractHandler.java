package net.foben.schematizer.parse;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements RDFHandler {
	protected int stcount = 0;
	protected Logger _log;
	protected long last, now;
	
	public AbstractHandler(){
		this._log = LoggerFactory.getLogger(AbstractHandler.class);
		last = System.nanoTime();
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {}
	
	@Override
	public void endRDF() throws RDFHandlerException {}

	@Override
	public void handleComment(String arg0) throws RDFHandlerException {	}

	@Override
	public void handleNamespace(String arg0, String arg1)
			throws RDFHandlerException { }

	@Override
	public void handleStatement(Statement arg0) throws RDFHandlerException {
		stcount++;
		if(stcount%1000000 == 0){
			_log.info(stcount/1000000 + " million lines parsed in " + measure());
		}
		handleStatementInternal(arg0);
	}
	
	public abstract void handleStatementInternal(Statement st);
	
	private String measure() {
		now = System.nanoTime();
		double dur = (now - last) / 1000000000d;
		String result = ("" + dur);
		last = now;
		return result;
	}

}
