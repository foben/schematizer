package net.foben.schematizer.parse;

import org.openrdf.model.Statement;

public class MultiFileTestHandler extends AbstractHandler {

	@Override
	public void handleStatementInternal(Statement st) {
		// TODO Auto-generated method stub

	}

	protected void parseStart() {
		_log.info("Parsing started");
	}

	protected void fileStart() {
		_log.info("New file started");
	}

	protected void fileEnd() {
		_log.info("A file ended");
		_log.info("parsed " + this.stcount + " statements");
	}

	protected void parseEnd() {
		_log.info("Parsing finished");
		_log.info("parsed " + this.stcount + " statements");
	}

}
