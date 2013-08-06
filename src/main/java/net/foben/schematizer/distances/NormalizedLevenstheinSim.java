package net.foben.schematizer.distances;

import com.wcohen.ss.ScaledLevenstein;

public class NormalizedLevenstheinSim implements ISimmilarityMeasure {
	
	ScaledLevenstein lev;
	
	public NormalizedLevenstheinSim(){
		lev = new ScaledLevenstein();
		
	}
	
	@Override
	public double getSim(ResDescriptor a, ResDescriptor b) {
		return lev.score(a.getLocalName(), b.getLocalName());
	}


}
