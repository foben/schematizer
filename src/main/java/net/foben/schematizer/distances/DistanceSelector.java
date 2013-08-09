package net.foben.schematizer.distances;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DistanceSelector {
	
	public static ISimmilarityMeasure<LabeledResDescriptor> getMeasure(){
		System.out.println("Select instance:");
		for(DISTANCEMEASURE d : DISTANCEMEASURE.values()){
			System.out.println(d.getMeasureName() + " : " + d.index);
		}
		int selection = -1;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			selection = Integer.parseInt(br.readLine());
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return DISTANCEMEASURE.createInstance(selection);
	}
	
	public enum DISTANCEMEASURE {
		LEVENSTHEINNORM (1, NormalizedLevenstheinSim.class),
		JACCARDCOMMENTS (2, JaccardCommentsSim.class);
		
		public final int index;
		public final Class<? extends ISimmilarityMeasure<LabeledResDescriptor>> clazz;
		
		private DISTANCEMEASURE(int index, Class<? extends ISimmilarityMeasure<LabeledResDescriptor>> clazz){
			this.index = index;
			this.clazz = clazz;
		}
		
		public String getMeasureName(){
			return clazz.getName();
		}
		
		public ISimmilarityMeasure<LabeledResDescriptor> getInst(){
			try {
				return clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public static ISimmilarityMeasure<LabeledResDescriptor> createInstance(int i){
			for(DISTANCEMEASURE d : DISTANCEMEASURE.values()){
				if (i == d.index) return d.getInst();
			}
			System.err.println("No alternative with this index!");
			return null;
		}
		
	}
	
}
