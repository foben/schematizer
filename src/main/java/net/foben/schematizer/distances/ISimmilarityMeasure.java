package net.foben.schematizer.distances;

import net.foben.schematizer.model.LabeledResDescriptor;

public interface ISimmilarityMeasure<T extends LabeledResDescriptor> {

	public double getSim(T s, T t);
	
	public String getMeasureName();

}
