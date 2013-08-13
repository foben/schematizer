package net.foben.schematizer.da;

import net.foben.schematizer.model.ResDescriptor;

public interface DAO {
	
	public void queue(ResDescriptor row, ResDescriptor column, double simil);
	
	public void executeBatch();
	
	public void terminate();
}
