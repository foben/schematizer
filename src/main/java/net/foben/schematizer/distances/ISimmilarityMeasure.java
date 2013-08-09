package net.foben.schematizer.distances;

public interface ISimmilarityMeasure<T extends LabeledResDescriptor> {

	public double getSim(T s, T t);
	
	public String getMeasureName();

}
