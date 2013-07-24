package net.foben.schematizer.parse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import net.foben.schematizer.util.IPLDReducer;
import net.foben.schematizer.util.PublicSuffixReducer;

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
	Set<String> datasets;
	int failures;
	IPLDReducer backupreducer;
	
	public DataRewriteHandler(File outputFile, Map<String, String> mappings, Set<String> datasets){
		super();
		this.outputFile = outputFile;
		this.failures = 0;
		this.mappings = mappings;
		this.datasets = datasets;
		outputFile.delete();
		try {
			this.backupreducer = new PublicSuffixReducer();
			fout = new FileOutputStream(outputFile, true);
			writer = new NQuadsWriter(fout);
			writer.startRDF();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}
		timingStart("between");
	}
	
	public DataRewriteHandler(String outputFile, Map<String, String> mappings, Set<String> datasets){
		this(new File(outputFile), mappings, datasets);
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
		timingEnd("between");
		timingStart("newContext");
		String oriContext = st.getContext().stringValue();
		String dataset = mappings.get(oriContext);
		if(dataset == null){
			dataset = backupreducer.getPLD(oriContext);
			if(!datasets.contains(dataset)){
				_log.warn("Unprocessable graph: " + st.getContext().stringValue());
				failures++;
				return;
			}
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
		timingStart("between");
	}
}
