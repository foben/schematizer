package net.foben.schematizer.da;

import net.foben.schematizer.model.SimpleResourceDescriptor;

public interface DAO {
	
	public void queue(SimpleResourceDescriptor row, SimpleResourceDescriptor column, double simil);
	
	public void executeBatch();
	
	public void terminate();
}
