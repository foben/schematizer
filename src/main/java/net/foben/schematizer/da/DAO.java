package net.foben.schematizer.da;

import net.foben.schematizer.model.ResourceDescriptor;

public interface DAO {

	public void queue(ResourceDescriptor row, ResourceDescriptor column,
			double simil);

	public void executeBatch();

	public void terminate();
}
