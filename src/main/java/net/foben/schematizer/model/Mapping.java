package net.foben.schematizer.model;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class Mapping {
    String uri1;
    String uri2;

    public Mapping(URI uri1, URI uri2) {
	this(uri1.stringValue(), uri2.stringValue());
    }

    public Mapping(String uri1, String uri2) {
	if (uri1.equals(uri2)) {
	    throw new IllegalArgumentException("URIs are the same");
	}
	this.uri1 = uri1;
	this.uri2 = uri2;
    }

    public boolean equals(Object other) {
	if (!(other instanceof Mapping)) {
	    return false;
	}
	Mapping o = (Mapping) other;
	if (uri1.equals(o.getUri1())) {
	    return uri2.equals(o.getUri2());
	} else if (uri1.equals(o.getUri2())) {
	    return uri2.equals(o.getUri1());
	}
	return false;
    }

    public int hashCode() {
	int result = 0;
	String concat = uri1.compareTo(uri2) < 0 ? uri1 + uri2 : uri2 + uri1;
	result = concat.hashCode() * concat.length();
	return result;
    }

    public String getUri1() {
	return uri1;
    }

    public URI getUri1AsURI() {
	return new URIImpl(uri1);
    }

    public String getUri2() {
	return uri2;
    }

    public URI getUri2AsURI() {
	return new URIImpl(uri2);
    }

    public String getLocalName1() {
	return new URIImpl(uri1).getLocalName();
    }

    public String getLocalName2() {
	return new URIImpl(uri2).getLocalName();
    }

    public String toString() {
	return uri1 + " <> " + uri2;
    }

}