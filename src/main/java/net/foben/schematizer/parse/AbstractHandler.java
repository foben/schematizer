package net.foben.schematizer.parse;

import java.util.HashMap;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractHandler implements RDFHandler {
	protected int stcount = 0;
	protected Logger _log;
	protected long last, now;
	private HashMap<String, Timing> timings;
	
	public AbstractHandler(){
		this._log = LoggerFactory.getLogger(this.getClass());
		last = System.nanoTime();
		timings = new HashMap<String, Timing>();
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
			everyMillion();
		}
		if(stcount%100000000 == 0){
			everyHundredMillion();
		}
		handleStatementInternal(arg0);
	}
	protected void everyMillion() {
		_log.info(stcount/1000000 + " million lines parsed. Speed: " + measure());
		if(timings.keySet().size() > 0){
			_log.info("Timings:");
			for(String key : timings.keySet()){
				timings.get(key).reset();
				_log.info(" " + key + " - " + timings.get(key).getTime());
			}
		}
	}
	protected void everyHundredMillion() {}

	public abstract void handleStatementInternal(Statement st);
	
	private String measure() {
		now = System.nanoTime();
		double dur = (now - last) / 1000000000d;
		String result = ("" + dur);
		last = now;
		return result;
	}
	
	
	
	protected void timingStart(String str){
		Timing t = timings.get(str);
		if(t == null){
			timings.put(str, new Timing());
		}
		else{
			t.tickStart();
		}
	}
	
	protected void timingEnd(String str){
		Timing t = timings.get(str);
		if(t == null){
			throw new IllegalArgumentException("Dont finish before start!");
		}
		else{
			t.tickEnd();
		}
	}
	
	private class Timing{
		
		private long lastTick, total;
		
		public Timing(){
			lastTick = System.nanoTime();
			total = 0;
		}
		
		public void tickStart() {
			lastTick = System.nanoTime();			
		}
		
		public void tickEnd(){
			total += System.nanoTime() - lastTick;
		}
		
		public double getTime(){
			return total / 1000000000d;
		}
		
		public void reset(){
			total = 0;
		}
		
	}
	
}
