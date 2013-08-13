package net.foben.schematizer.model;

public abstract interface ResourceDescriptor {
	
	public int getTotal();

	public int getDatasets();
	
	public String getURI();
	
	public String getLocalName();
}
