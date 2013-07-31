package net.foben.schematizer.parse;

import java.util.HashMap;
import org.openrdf.model.Statement;

public class ExtractPropertiesHandler extends AbstractHandler {
	
	HashMap<String, PropStat> props;
	
	public ExtractPropertiesHandler(){
		props = new HashMap<String, PropStat>();
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		String prop = st.getPredicate().stringValue();
		
		if(props.containsKey(prop)){
			props.get(prop).incr(st.getContext().stringValue());
		}
		else{
			props.put(prop, new PropStat(prop, st.getContext().stringValue()));
		}
	}

	@Override
	protected void parseEnd() {
		if(true){
			writeToFile(props.values(), "ExtractPropsHandlerOutputNew");
			return;
		}		
	}
	
	private class PropStat implements Comparable<PropStat>{
		private String type;
		private int totalinstances;
		private HashMap<String, Integer> instancesPerDataset;
		
		public PropStat(String type, String dataset){
			this.type = type;
			totalinstances = 1;
			instancesPerDataset = new HashMap<String, Integer>();
			instancesPerDataset.put(dataset, 1);
		}
		
		public void incr(String dataset){
			totalinstances++;
			if (instancesPerDataset.containsKey(dataset)){
				int newCt = instancesPerDataset.get(dataset) + 1;
				instancesPerDataset.put(dataset, newCt);
			}
			else{
				instancesPerDataset.put(dataset, 1);
			}
		}
		
		public String toString(){
			String result = "";
			result += type;
			result += " " + totalinstances;
			result += " " + instancesPerDataset.keySet().size();
			return result;
		}
		
		public String getType(){
			return this.type;
		}

		@Override
		public int compareTo(PropStat o) {
			if(this.type.equals(o.getType())) return 0;
			else{
				return this.type.compareTo(o.getType());
			}
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof PropStat){
				return this.type.equals(((PropStat)o).getType());
			}
			else{
				return false;
			}
		}
	}

}
