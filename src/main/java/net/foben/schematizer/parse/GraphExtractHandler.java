package net.foben.schematizer.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphExtractHandler implements RDFHandler {
	private int stcount = 0;
	private Logger _log;
	private Set<String> graphs;
	private double now, last;
	String outfile;
	
	
	public GraphExtractHandler(String outfile) throws IOException{
		this._log = LoggerFactory.getLogger(GraphExtractHandler.class);
		this.outfile = outfile;
		graphs = new HashSet<String>();
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/lovvocabs"));
		String ns;
		while((ns = in.readLine()) != null){
			graphs.add(ns);
		}
		in.close();
	}
	
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			String linesep = System.getProperty("line.separator");
			FileWriter out = new FileWriter(outfile);
			int lines = 0;
			for(String graph : graphs){
				lines++;
				out.write(graph);
				out.write(linesep);
				if(lines%10000 == 0){
					_log.info("Flushing 10,000 lines");
					out.flush();
				}
			}
			out.flush();
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
				_log.info(stcount/1000000 + " million lines parsed. Speed: " + measure());
			}
			graphs.add(arg0.getContext().stringValue());
			
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
