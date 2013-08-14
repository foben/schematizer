package net.foben.schematizer.distances;

import net.foben.schematizer.model.ComparableResourceDescriptor;
import net.foben.schematizer.model.SimpleResourceDescriptor;

import org.apache.lucene.search.spell.JaroWinklerDistance;

public class JaroWinklerLuceneMeasure implements ISimmilarityMeasure<SimpleResourceDescriptor> {
	
	JaroWinklerDistance jwd;
	
	public JaroWinklerLuceneMeasure(){
		jwd = new JaroWinklerDistance();
	}
	
	@Override
	public double getSim(SimpleResourceDescriptor s, SimpleResourceDescriptor t) {
		return jwd.getDistance(s.getLocalName(), t.getLocalName());
	}

	@Override
	public String getMeasureName() {
		return "JaroWinkler";
	}

	@Override
	public double getSim(ComparableResourceDescriptor s, ComparableResourceDescriptor t) {
		if(s instanceof SimpleResourceDescriptor && t instanceof SimpleResourceDescriptor){
			return getSim((SimpleResourceDescriptor)s, (SimpleResourceDescriptor)t);
		}
		throw new IllegalArgumentException("WAAAAAAAA");
	}

	@Override
	public Class<?> getExpected() {
		return SimpleResourceDescriptor.class;
	}

}
