package net.foben.schematizer.app;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import net.foben.schematizer.util.DatasetToGraphsMapper;
import net.foben.schematizer.util.PublicSuffixReducer;
import net.foben.schematizer.util.RestrictingPLDReducer;

public class GraphMapper {

	/**
	 * @param args
	 * @throws RDFHandlerException 
	 * @throws RepositoryException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws RepositoryException, RDFHandlerException, IOException {
		DatasetToGraphsMapper mapper = new DatasetToGraphsMapper("src/main/resources/graphs.nt");
		mapper.printStats();
		//boolean success = mapper.createMappings(new RestrictingPLDReducer("src/main/resources/BTCReductions"));
		boolean success = mapper.createMappings(new PublicSuffixReducer());
		if(success){
			mapper.printStats();
			mapper.exportMappingsInternal("GraphMappings.nt");
		}
	}

}
