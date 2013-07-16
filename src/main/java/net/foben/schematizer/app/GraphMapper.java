package net.foben.schematizer.app;

import java.io.FileNotFoundException;

import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandlerException;

import net.foben.schematizer.util.DatasetToGraphsMapper;
import net.foben.schematizer.util.RestrictingPLDReducer;

public class GraphMapper {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws RDFHandlerException 
	 * @throws RepositoryException 
	 */
	public static void main(String[] args) throws RepositoryException, RDFHandlerException, FileNotFoundException {
		DatasetToGraphsMapper mapper = new DatasetToGraphsMapper("src/main/resources/graphs.nt");
		mapper.printStats();
		boolean success = mapper.createMappings(new RestrictingPLDReducer("src/main/resources/BTCReductions"));
		if(success){
			mapper.printStats();
			mapper.exportMappingsInternal("GraphMappings.nt");
		}
	}

}
