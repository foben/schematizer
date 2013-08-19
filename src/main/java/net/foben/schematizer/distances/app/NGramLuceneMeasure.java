package net.foben.schematizer.distances.app;

import org.apache.lucene.search.spell.NGramDistance;

import net.foben.schematizer.distances.ISimmilarityMeasure;
import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.SimpleResourceDescriptor;

public class NGramLuceneMeasure implements
		ISimmilarityMeasure<SimpleResourceDescriptor> {

	NGramDistance ngr;

	public NGramLuceneMeasure() {
		ngr = new NGramDistance();
	}

	@Override
	public double getSim(SimpleResourceDescriptor s, SimpleResourceDescriptor t) {
		return ngr.getDistance(s.getLocalName(), t.getLocalName());
	}

	@Override
	public String getMeasureName() {
		return "NGramLucene";
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
