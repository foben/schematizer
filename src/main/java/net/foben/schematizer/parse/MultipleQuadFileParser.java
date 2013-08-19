package net.foben.schematizer.parse;

import static net.foben.schematizer.Environment.URI_IDS;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleQuadFileParser {

	private NonNodeStoringNQuadsParser parser;
	private AbstractHandler handler;
	private String[] filenames;
	private Logger _log;

	public MultipleQuadFileParser(AbstractHandler handler, String... filenames) {
		if (filenames == null || filenames.length == 0) {
			throw new IllegalArgumentException(
					"Must supply at least one input file");
		}
		_log = LoggerFactory.getLogger(MultipleQuadFileParser.class);
		this.filenames = filenames;
		this.parser = new NonNodeStoringNQuadsParser();
		this.handler = handler;

		this.parser.setRDFHandler(this.handler);
		configureParser(parser);
	}

	private void configureParser(RDFParser parser) {
		_log.trace("Configuring Parser");
		parser.setPreserveBNodeIDs(true);
		parser.getParserConfig().set(
				BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		parser.getParserConfig().set(
				BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		parser.getParserConfig().set(
				BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		parser.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS,
				false);
		parser.getParserConfig().set(
				BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);
		parser.getParserConfig().set(
				BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
		parser.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS,
				false);
		parser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS,
				true);
		parser.getParserConfig().addNonFatalError(
				BasicParserSettings.VERIFY_DATATYPE_VALUES);
		parser.getParserConfig().addNonFatalError(
				BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
		parser.getParserConfig().addNonFatalError(
				NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
	}

	public void startParsing() {
		handler.setChunks(filenames.length);
		for (String file : filenames) {
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(file));
				parser.parse(in, URI_IDS);
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (RDFParseException e) {
				e.printStackTrace();
			} catch (RDFHandlerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
