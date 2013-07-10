package net.foben.schematizer;

import net.foben.schematizer.util.DatasetMapper;
import net.foben.schematizer.util.RestrictingPLDReducer;

public class MappingApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DatasetMapper mapper = new DatasetMapper("src/main/resources/graphs.nt");
		mapper.printStats();
		boolean success = mapper.createMappings(new RestrictingPLDReducer("src/main/resources/TopLevelDomains"));
		if(success){
			mapper.printStats();
		}
	}

}
