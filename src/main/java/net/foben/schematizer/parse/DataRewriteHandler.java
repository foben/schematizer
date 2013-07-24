package net.foben.schematizer.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.nquads.NQuadsWriter;

public class DataRewriteHandler extends AbstractHandler {
	
	File outputFile;
	NQuadsWriter writer;
	FileOutputStream fout;
	Map<String, String> mappings;
	int failures;
	
	public DataRewriteHandler(File outputFile, Map<String, String> mappings){
		super();
		this.outputFile = outputFile;
		this.failures = 0;
		this.mappings = mappings;
		outputFile.delete();
		try {
			fout = new FileOutputStream(outputFile, true);
			writer = new NQuadsWriter(fout);
			writer.startRDF();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
	}
	
	public DataRewriteHandler(String outputFile, Map<String, String> mappings){
		this(new File(outputFile), mappings);
	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		_log.info("Number of failures: " + failures);
		try {
			writer.endRDF();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		timingStart("newContext");
		String dataset = mappings.get(st.getContext().stringValue());
		if(dataset == null){
			_log.warn("Unknown graph: " + st.getContext().stringValue());
			failures++;
			return;
		}
		String newContext = "http://" + dataset;
		timingEnd("newContext");
		try {
			timingStart("writer");
			writer.handleStatement(new ContextStatementImpl(st.getSubject(), st.getPredicate(), st.getObject(), new URIImpl(newContext)));
			timingEnd("writer");
		} catch (RDFHandlerException e) {
			e.printStackTrace();
			failures++;
		}
	}
}
