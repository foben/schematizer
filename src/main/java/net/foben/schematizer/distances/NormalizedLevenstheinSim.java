package net.foben.schematizer.distances;

import net.foben.schematizer.model.LabeledResDescriptor;

import com.wcohen.ss.ScaledLevenstein;

public class NormalizedLevenstheinSim implements ISimmilarityMeasure<LabeledResDescriptor> {
	
	ScaledLevenstein lev;
	
	public NormalizedLevenstheinSim(){
		lev = new ScaledLevenstein();
		
	}
	
	@Override
	public double getSim(LabeledResDescriptor a, LabeledResDescriptor b) {
		double score = lev.score(a.getLocalName(), b.getLocalName());
		return score;
	}

	@Override
	public String getMeasureName() {
		return "LevenstheinNorm";
	}


}
