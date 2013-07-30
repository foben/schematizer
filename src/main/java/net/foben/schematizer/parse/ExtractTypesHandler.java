package net.foben.schematizer.parse;

import java.util.HashSet;

import org.openrdf.model.Statement;

import static net.foben.schematizer.Environment.*;

public class ExtractTypesHandler extends AbstractHandler {
	
	HashSet<String> types;
	int rdfsc, owlc, subcl;
	
	public ExtractTypesHandler(){
		types = new HashSet<String>();
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		if(st.getPredicate().stringValue().equals(RDFTYPE)){
			String obj = st.getObject().stringValue();
			if (obj.equals(RDFSCLASS)){
				rdfsc = types.add(st.getSubject().stringValue()) ? rdfsc + 1 : rdfsc;
			}
			else if(obj.equals(OWLCLASS)){
				owlc = types.add(st.getSubject().stringValue()) ? owlc + 1 : owlc;
			}
			else{
				types.add(obj);
			}
		}
		else if (st.getPredicate().stringValue().equals(RDFSUBCLASSOF)){
			subcl = types.add(st.getSubject().stringValue()) ? subcl + 1 : subcl;
			subcl = types.add(st.getObject().stringValue()) ? subcl + 1 : subcl;
		}

	}

	@Override
	protected void parseEnd() {
		_log.info(rdfsc + " added via RDFSCLASS");
		_log.info(owlc + " added via OWLCLASS");
		_log.info(subcl + " added via RDFSUBCLASSOF");
		if(true){
			writeToFile(types, "ExtractTypesHandlerOutput");
			return;
		}		
	}

}
