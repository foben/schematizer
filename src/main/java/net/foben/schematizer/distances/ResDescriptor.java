package net.foben.schematizer.distances;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResDescriptor implements Comparable<ResDescriptor>{
	
	private int total;
	private int datasets;
	private String type;
	private String localName;
	protected Logger _log;
	
	public ResDescriptor(String type, int total, int datasets){
		_log = LoggerFactory.getLogger(this.getClass());
		this.type = type;
		this.total = total;
		this.datasets = datasets;
		URI uri = new URIImpl(this.type);
		this.localName = uri.getLocalName();
		if(datasets > total)
			throw new IllegalArgumentException("dataset occurences cant be greater than total occurences");
	}
	
	@Override
	public int compareTo(ResDescriptor o) {
		int otot = o.getTotal();
		if(o.getType().equals(this.getType())){
			if(otot > total) return -1;
			if(otot < total) return 1;
			int odat = o.getDatasets();
			if(odat > datasets) return -1;
			if(odat < datasets) return 1;
			return 0;
		}
		if(otot > total) return -1;
		if(otot < total) return 1;
		int odat = o.getDatasets();
		if(odat > datasets) return -1;
		if(odat < datasets) return 1;
		return o.getType().compareTo(type);
		
	}
	
	public String toString(){
		return this.type;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ResDescriptor)) return false;
		return this.compareTo((ResDescriptor) o) == 0 ? true : false;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getDatasets() {
		return datasets;
	}

	public void setDatasets(int datasets) {
		this.datasets = datasets;
	}
	
	public String getType(){
		return this.type;
	}
	
	public String getLocalName() {
		return localName;
	}


}
