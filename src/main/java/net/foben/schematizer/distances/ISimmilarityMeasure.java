package net.foben.schematizer.distances;

public interface ISimmilarityMeasure<T extends ResDescriptor> {

	public double getSim(T s, T t);


}
