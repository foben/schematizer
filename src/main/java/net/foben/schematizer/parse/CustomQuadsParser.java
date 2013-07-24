package net.foben.schematizer.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.input.BOMInputStream;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.rio.nquads.NQuadsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomQuadsParser extends NQuadsParser {
	private long count = 0;
	private Logger _log;
	
	public CustomQuadsParser(){
		super();
		this._log = LoggerFactory.getLogger(CustomQuadsParser.class);
		_log.info("Custom Parser created");
	}
	public void clearNodes(){
		this.clearBNodeIDMap();
	}
	
	@Override
	public synchronized void parse(final InputStream inputStream, final String baseURI)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (inputStream == null) {
			throw new IllegalArgumentException("Input stream can not be 'null'");
		}
		// Note: baseURI will be checked in parse(Reader, String)

		try {
			parse(new InputStreamReader(new BOMInputStream(inputStream, false), "US-ASCII"), baseURI);
		}
		catch (UnsupportedEncodingException e) {
			// Every platform should support the US-ASCII encoding...
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void parse(final Reader reader, final String baseURI)
		throws IOException, RDFParseException, RDFHandlerException
	{
		if (reader == null) {
			throw new IllegalArgumentException("Reader can not be 'null'");
		}
		if (baseURI == null) {
			throw new IllegalArgumentException("base URI can not be 'null'");
		}

		rdfHandler.startRDF();

		this.reader = reader;
		lineNo = 1;

		reportLocation(lineNo, 1);

		try {
			int c = reader.read();
			c = skipWhitespace(c);

			while (c != -1) {
				if (c == '#') {
					// Comment, ignore
					c = skipLine(c);
				}
				else if (c == '\r' || c == '\n') {
					// Empty line, ignore
					c = skipLine(c);
				}
				else {
					c = parseQuad(c);
				}

				c = skipWhitespace(c);
			}
		}
		finally {
			clear();
		}

		rdfHandler.endRDF();
	}
	

	private int parseQuad(int c)
			throws IOException, RDFParseException, RDFHandlerException
		{
			if(++count%10000000 == 0){
				this.clearBNodeIDMap();
				_log.info("Cleared BNode map");
			}
			boolean ignoredAnError = false;
			try {
				c = parseSubject(c);

				c = skipWhitespace(c);

				c = parsePredicate(c);

				c = skipWhitespace(c);

				c = parseObject(c);

				c = skipWhitespace(c);

				// Context is not required
				if (c != '.') {
					c = parseContext(c);
					c = skipWhitespace(c);
				}
				if (c == -1) {
					throwEOFException();
				}
				else if (c != '.') {
					reportFatalError("Expected '.', found: " + (char)c);
				}

				c = assertLineTerminates(c);
			}
			catch (RDFParseException rdfpe) {
				if (getParserConfig().isNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES)) {
					reportError(rdfpe, NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
					ignoredAnError = true;
				}
				else {
					throw rdfpe;
				}
			}

			c = skipLine(c);

			if (!ignoredAnError) {
				Statement st = createStatement(subject, predicate, object, context);
				rdfHandler.handleStatement(st);
			}

			subject = null;
			predicate = null;
			object = null;
			context = null;

			return c;
		}
}
