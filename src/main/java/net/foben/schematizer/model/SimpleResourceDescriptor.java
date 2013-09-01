package net.foben.schematizer.model;

import net.foben.schematizer.util.ThreeTuple;

public class SimpleResourceDescriptor extends ComparableResourceDescriptor {

    /**
	 * 
	 */
    private static final long serialVersionUID = -647131551265820718L;

    public SimpleResourceDescriptor(String uri, int datasets, int total) {
	super(uri, datasets, total);
    }

    public SimpleResourceDescriptor(ThreeTuple<String, Integer, Integer> tuple) {
	super(tuple);
    }

}
