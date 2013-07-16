package net.foben.schematizer.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphToDatasetMapper {
	
	private String graphsFile;
	private HashMap<String, String> mappings;
	private boolean mappingSuccess = false;
	private double runtime = -1;
	private int datasets   = -1;
	private int processedGraphs     = -1;
	private Logger _log, _logu;
	private RestrictingPLDReducer reducer;
	
	public GraphToDatasetMapper(String graphsFile){
		_log = LoggerFactory.getLogger(GraphToDatasetMapper.class);
		_logu = LoggerFactory.getLogger("net.foben.schematizer.util.RestrictingPLDReducer.Unknowns");
		this.graphsFile = graphsFile;
		mappings = new HashMap<String, String>();
	}
	
	
	public boolean createMappings(){
		reducer = new RestrictingPLDReducer("src/main/resources/BTCReductions");
		long start = System.nanoTime();
		boolean result = true;
		BufferedReader br = null;
		int count = 0;
		Set<String> uniqDatasets = new HashSet<String>();
		try {
			br = new BufferedReader(new FileReader(graphsFile));
			String line;
				while ((line = br.readLine()) != null){
					count++;
					String graph = line;
					String dataset = reducer.getPLD(line);
					uniqDatasets.add(dataset);
					addMapping(graph, dataset);
					if(count%1000000 == 0) _log.info((count/1000000) + " million graphs parsed");
				}
		} catch (FileNotFoundException e) {
			result = false;
			e.printStackTrace();
		} catch (IOException e) {
			result = false;
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				result = false;
			}
		}
		for(String str : uniqDatasets){
			System.out.println(str);
		}
		runtime = result ? (System.nanoTime()- start)/Math.pow(10, 9) : -1;
		datasets = uniqDatasets.size();
		processedGraphs = result ? mappings.keySet().size() : -1;
		mappingSuccess = result;
		uniqDatasets = null;
		return result;
	}
	
	private void addMapping(String graph, String dataset){
		if(!mappings.containsKey(graph)){
			mappings.put(graph, dataset);
		}
		else if(!mappings.get(graph).equals(dataset)){
			_log.error("One graph mapped to different datasets!:");
			_log.error("Graph          : " + graph);
			_log.error("Current mapping: " + mappings.get(graph));
			_log.error("New mapping    : " + dataset);
			System.exit(-1);
		}
	}
	
	public void printStats(){
		if(!mappingSuccess){
			_log.warn("No successful mapping yet!");
			return;
		}
		_log.info("Graphs Processed   : " + processedGraphs);
		_log.info("Mapped to Datasets : " + datasets);
		_log.info("Runtime	          : " + runtime);
	}
	
	public HashMap<String, String> getMappings() {
		if(mappingSuccess) return mappings;
		else return null;
	}


	public String get(String uri) {
		String result = mappings.get(uri);
		if(result != null) return result;
		_logu.info(uri);
		addMapping(uri, reducer.getPLD(uri));
		result = mappings.get(uri);
		if(result == null){
			_log.error("Something went 'orribly wrong");
			System.exit(-1);
		}
		return result;
	}

}
