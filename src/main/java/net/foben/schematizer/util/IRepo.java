package net.foben.schematizer.util;

import org.openrdf.model.Statement;

public interface IRepo {
	
	public void close();
	
	public boolean add(Statement st);
	
	public boolean remove(Statement st);
	
	
}
