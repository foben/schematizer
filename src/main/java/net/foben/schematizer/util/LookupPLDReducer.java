package net.foben.schematizer.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookupPLDReducer implements IPLDReducer {
	
	private Logger _log;
	private GraphToDatasetMapper mapper;
	
	public LookupPLDReducer() throws IOException{
		_log = LoggerFactory.getLogger(LookupPLDReducer.class);
		
		init();
	}
	
	private void init() throws IOException {
		_log.info("Initializing Lookup table");
		mapper = new GraphToDatasetMapper("src/main/resources/graphs.nt", new PublicSuffixReducer());
		mapper.createMappings();
		mapper.printStats();
		_log.info("Initialization complete");
	}

	@Override
	public String getPLD(String uri) {
		return mapper.get(uri);
	}

}
