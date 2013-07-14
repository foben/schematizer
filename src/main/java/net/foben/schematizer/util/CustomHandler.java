package net.foben.schematizer.util;

import net.foben.schematizer.offline.MemRepository;
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

public class CustomHandler implements RDFHandler {
	private int stcount = 0;
	private MemRepository repo;
	private IPLDReducer reducer;
	private URI exVo, exRes;
	private Logger _log;
	
	
	public CustomHandler(){
		_log = LoggerFactory.getLogger(CustomHandler.class);
		repo = new MemRepository();
		reducer = new RestrictingPLDReducer("src/main/resources/TopLevelDomains");
		exVo = new URIImpl(URI_EXTERNALVOCAB);
		exRes = new URIImpl(URI_EXTERNALRES);

	}
	@Override
	public void startRDF() throws RDFHandlerException {	}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		repo.toFile("footest");
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
			if(stcount%100000 == 0)System.out.print(".");
			if(stcount%1000000 == 0)System.out.println();
			Resource subj = arg0.getSubject();
			URI pred = arg0.getPredicate();
			Value obj = arg0.getObject();
			
			
			String pldStr = reducer.getPLD(arg0.getContext().stringValue());
			URI pld = new URIImpl(URI_IDS + pldStr);
			
			
			//SUBJECT
			if(subj instanceof URI){
				if(!pldStr.equals(reducer.getPLD(subj.stringValue()))){
					repo.add(new StatementImpl(pld, exRes, subj));
				}
			}
			else if(subj instanceof BNode){
				
			}
			
			
			//PREDICATE
			if(!pldStr.equals(reducer.getPLD(pred.stringValue()))){
				repo.add(new StatementImpl(pld, exRes, pred));
			}
			
			//OBJECT
			if(obj instanceof Resource){
				if(obj instanceof URI){
					if(!pldStr.equals(reducer.getPLD(((Resource)obj).stringValue()))){
						repo.add(new StatementImpl(pld, exRes, obj));
					}
				}
				else if(obj instanceof BNode){
					
				}
			}
			else if (obj instanceof Literal){
				
			}
		} catch (IllegalArgumentException ia){
			//_log.warn("IAE occured: " + ia.getMessage());
		}
	}

}
