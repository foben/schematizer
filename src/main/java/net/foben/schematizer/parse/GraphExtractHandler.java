package net.foben.schematizer.parse;

import java.util.Set;

import org.openrdf.model.Statement;

public class GraphExtractHandler extends AbstractHandler {
	
	private Set<String> graphs;
	
	public GraphExtractHandler(Set<String> graphs){
		this.graphs = graphs;
		_log.info("Created Handler with " + this.graphs.size() + " initial graphs");
	}
	
	@Override
	protected void parseEnd() {
		_log.info(graphs.size() + " unique graphs so far");
		for(String str : graphs){
			System.out.println(str);
		}
	}

	@Override
	public void handleStatementInternal(Statement st) {
		boolean added = graphs.add(st.getContext().stringValue());
		if(added){
			_log.info(st.getContext().stringValue() + " added");
		}

	}

}
