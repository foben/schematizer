package net.foben.schematizer.distances;

import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.ResourceDescriptor;

public interface ISimmilarityMeasure<T extends ResourceDescriptor> {

	public double getSim(T s, T t);

	public double getSim(ComparableResourceDescriptor s,
			ComparableResourceDescriptor t);

	public Class<?> getExpected();

	public String getMeasureName();

}
