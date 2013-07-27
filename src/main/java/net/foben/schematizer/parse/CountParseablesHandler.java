package net.foben.schematizer.parse;

import org.openrdf.model.Statement;

public class CountParseablesHandler extends AbstractHandler {
	
	
	int parseable = 0;
	@Override
	public void handleStatementInternal(Statement st) {
		parseable++;

	}

	@Override
	protected void parseEnd() {
		_log.info(parseable + " parseable lines in file");
		
	}

}
