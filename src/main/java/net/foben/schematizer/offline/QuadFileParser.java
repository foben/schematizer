package net.foben.schematizer.offline;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import net.foben.schematizer.util.CustomHandler;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.foben.schematizer.Environment.*;

public class QuadFileParser {
	
	private RDFParser parser;
	private RDFHandler handler;
	private String fileToParse;
	private Logger _log;
	
	public QuadFileParser(RDFHandler handler, String filename){
		_log = LoggerFactory.getLogger(QuadFileParser.class);
		this.parser = Rio.createParser(RDFFormat.NQUADS);
		this.handler = handler;
		this.fileToParse = filename;
		parser.setRDFHandler(handler);
		configureParser(parser);
	}
	
	private void configureParser(RDFParser parser){
		parser.setPreserveBNodeIDs(true);
		parser.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		parser.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		parser.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
		parser.getParserConfig().set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);
		parser.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
		parser.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);
		parser.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
		parser.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
		parser.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
	}
	
	public void startParsing(){
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fileToParse));
			parser.parse(in, URI_IDS);
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
	
	public static void main(String[] args) throws RDFParseException, RDFHandlerException, IOException {
		QuadFileParser parser = new QuadFileParser(new CustomHandler(),
				"src/main/resources/quadtest_.nq");
		parser.startParsing();
	}
	

}
