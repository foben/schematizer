package net.foben.schematizer.parse;

import java.util.HashSet;

import org.openrdf.model.Statement;

import static net.foben.schematizer.Environment.*;

public class ExtractTypesHandler extends AbstractHandler {
	
	HashSet<String> types;
	
	public ExtractTypesHandler(){
		types = new HashSet<String>();
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		if(st.getPredicate().stringValue().equals(RDFTYPE)){
			types.add(st.getObject().stringValue());
		}
		else if (st.getPredicate().stringValue().equals(RDFSUBCLASSOF)){
			types.add(st.getSubject().stringValue());
		}

	}

	@Override
	protected void parseEnd() {
		if(true){
			writeToFile(types, "ExtractTypesHandlerOutput");
			return;
		}		
	}

}
