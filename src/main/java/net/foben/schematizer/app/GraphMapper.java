package net.foben.schematizer.app;

import java.io.IOException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import net.foben.schematizer.util.DatasetToGraphsMapper;
import net.foben.schematizer.util.PublicSuffixReducer;

public class GraphMapper {

	/**
	 * @param args
	 * @throws RDFHandlerException
	 * @throws RepositoryException
	 * @throws IOException
	 */
	public static void main(String[] args) throws RepositoryException,
			RDFHandlerException, IOException {
		DatasetToGraphsMapper mapper = new DatasetToGraphsMapper(
				"src/main/resources/graphs.nt");
		mapper.printStats();
		PublicSuffixReducer reducer = new PublicSuffixReducer();
		// boolean success = mapper.createMappings(new
		// RestrictingPLDReducer("src/main/resources/BTCReductions"));
		boolean success = mapper.createMappings(reducer);
		if (success) {
			mapper.printStats();
			mapper.exportDatasets("GraphMappings");
			reducer.printBench();
			// mapper.exportMappingsInternal("GraphMappings.nt");
		}
	}

}
