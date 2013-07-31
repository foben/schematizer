package net.foben.schematizer.parse;

import java.util.HashMap;
import org.openrdf.model.Statement;

import static net.foben.schematizer.Environment.*;

public class ExtractTypesHandler extends AbstractHandler {
	
	HashMap<String, TypeStat> types;
	
	public ExtractTypesHandler(){
		types = new HashMap<String, TypeStat>();
	}
	
	@Override
	public void handleStatementInternal(Statement st) {
		if(st.getPredicate().stringValue().equals(RDFTYPE)){
			String obj = st.getObject().stringValue();
			if(types.containsKey(obj)){
				types.get(obj).incr(st.getContext().stringValue());
			}
			else{
				types.put(obj, new TypeStat(obj, st.getContext().stringValue()));
			}
		}
	}

	@Override
	protected void parseEnd() {
		if(true){
			writeToFile(types.values(), "ExtractTypesHandlerOutputNew");
			return;
		}		
	}
	
	private class TypeStat implements Comparable<TypeStat>{
		private String type;
		private int totalinstances;
		private HashMap<String, Integer> instancesPerDataset;
		
		public TypeStat(String type, String dataset){
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
		public int compareTo(TypeStat o) {
			if(this.type.equals(o.getType())) return 0;
			else{
				return this.type.compareTo(o.getType());
			}
		}
		
		@Override
		public boolean equals(Object o){
			if(o instanceof TypeStat){
				return this.type.equals(((TypeStat)o).getType());
			}
			else{
				return false;
			}
		}
	}

}
