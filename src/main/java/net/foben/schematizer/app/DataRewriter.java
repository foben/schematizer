package net.foben.schematizer.app;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import net.foben.schematizer.parse.CustomQuadsParser;
import net.foben.schematizer.parse.DataRewriteHandler;
import net.foben.schematizer.parse.QuadFileParser;
import net.foben.schematizer.util.GraphToDatasetMapper;
import net.foben.schematizer.util.PublicSuffixReducer;

public class DataRewriter {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		for(String str : args){
			System.out.println(str);
		}
		
		File targetRoot = new File(args[0]);
		if(!targetRoot.exists() || targetRoot.isFile() || !targetRoot.canWrite()){
			throw new IllegalArgumentException("Target directory either does not exist, is a file or is not writable (by you).");
		}
		
		GraphToDatasetMapper mapper = new GraphToDatasetMapper("src/main/resources/graphs.nt", new PublicSuffixReducer());
		mapper.createMappings();
		//mapper.exportGraphs("mappedgraphs");
		Map<String, String> mappings = mapper.getMappings();
		Set<String> datasets = mapper.getUniqueDatasets();
		File targetFile;
		File targetDir;
		File currentFile;
		for(int i = 1; i < args.length; i++){
			currentFile = new File(args[i]);
			targetDir = new File(targetRoot, currentFile.getParentFile().getName());
			targetFile = new File(targetDir, currentFile.getName());
			targetDir.delete();
			targetDir.mkdir();
			
			System.out.println("****************************************");
			System.out.println(args[i]);
			System.out.println("****************************************");
			QuadFileParser parser = new QuadFileParser(new DataRewriteHandler(targetFile, mappings, datasets),	args[i], new CustomQuadsParser());
			parser.startParsing();
			System.out.println();
			
		}

	}

}
