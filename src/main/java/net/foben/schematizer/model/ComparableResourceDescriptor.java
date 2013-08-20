package net.foben.schematizer.model;

import java.io.Serializable;

import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Ordering;

public abstract class ComparableResourceDescriptor implements ResourceDescriptor,
	Comparable<ComparableResourceDescriptor>, Serializable {

    private static final long serialVersionUID = -2395220140518932603L;
    protected final int total;
    protected final int datasets;
    protected final String uri;
    protected final String localName;
    protected final Logger _log;

    public ComparableResourceDescriptor(String uri, int datasets, int occurences) {
	this.uri = uri;
	this.total = occurences;
	this.datasets = datasets;
	this.localName = new URIImpl(uri).getLocalName();
	this._log = LoggerFactory.getLogger(this.getClass());
    }

    public int compareTo(ComparableResourceDescriptor o) {
	int otherNoDatasets = o.getDatasets();
	if (o.getURI().equals(this.getURI())) {
	    if (otherNoDatasets != o.getDatasets() || total != o.getTotal()) {
		throw new IllegalStateException("Two ResDescriptors with same URI but differend values!");
	    }

	}
	if (otherNoDatasets > datasets)
	    return -1;
	if (otherNoDatasets < datasets)
	    return 1;
	int otherNoOccurences = o.getTotal();
	if (otherNoOccurences > total)
	    return -1;
	if (otherNoOccurences < total)
	    return 1;
	return o.getURI().compareTo(uri);
    }

    @Override
    public boolean equals(Object o) {
	if (!(o instanceof ComparableResourceDescriptor))
	    return false;
	return this.compareTo((ComparableResourceDescriptor) o) == 0 ? true : false;
    }

    public int getTotal() {
	return total;
    }

    public int getDatasets() {
	return datasets;
    }

    public String getURI() {
	return uri;
    }

    public String getLocalName() {
	return localName;
    }

    public static class ResDescriptorReverseOrdering extends Ordering<SimpleResourceDescriptor> implements Serializable {

	private static final long serialVersionUID = 24L;

	@Override
	public int compare(SimpleResourceDescriptor left, SimpleResourceDescriptor right) {
	    return left.compareTo(right) * -1;
	}
    }

}
