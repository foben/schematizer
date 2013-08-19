package net.foben.schematizer.distances;

import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.SimpleResourceDescriptor;

import com.wcohen.ss.ScaledLevenstein;

public class NormalizedLevenstheinSim implements
		ISimmilarityMeasure<SimpleResourceDescriptor> {

	ScaledLevenstein lev;

	public NormalizedLevenstheinSim() {
		lev = new ScaledLevenstein();

	}

	@Override
	public double getSim(SimpleResourceDescriptor a, SimpleResourceDescriptor b) {
		double score = lev.score(a.getLocalName(), b.getLocalName());
		return score;
	}

	@Override
	public String getMeasureName() {
		return "LevenstheinNorm";
	}

	@Override
	public double getSim(ComparableResourceDescriptor s,
			ComparableResourceDescriptor t) {
		if (s instanceof SimpleResourceDescriptor
				&& t instanceof SimpleResourceDescriptor) {
			return getSim((SimpleResourceDescriptor) s,
					(SimpleResourceDescriptor) t);
		}
		throw new IllegalArgumentException("WAAAAAAAA");
	}

	@Override
	public Class<?> getExpected() {
		return SimpleResourceDescriptor.class;
	}

}
