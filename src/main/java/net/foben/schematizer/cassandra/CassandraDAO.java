package net.foben.schematizer.cassandra;

import java.util.HashMap;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

public class CassandraDAO {
	Cluster cluster;
	String KEYSPACE_NAME = "ks";
	String CF_NAME = "lev";
	Keyspace keyspace;
	ColumnFamilyTemplate<String, String> template;
	
	public CassandraDAO(String cfname){
		this.CF_NAME = cfname;
		init();
	}
	
	public void init(){
		cluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		if (keyspaceDef == null) {
		    createSchema();
		}
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		template = new ThriftColumnFamilyTemplate<String, String>(keyspace, CF_NAME, StringSerializer.get(), StringSerializer.get());
	}
	
	public void addData(String key, HashMap<String, Float> values){
		ColumnFamilyUpdater<String, String> upd = template.createUpdater(key);
		for(String row : values.keySet()){
			upd.setFloat(row, values.get(row));
		}
		template.update(upd);
	}	
	
	public void createSchema(){
		
		KeyspaceDefinition kspdef = HFactory.createKeyspaceDefinition(KEYSPACE_NAME);
		
		ColumnFamilyDefinition cfdef = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, CF_NAME, ComparatorType.UTF8TYPE);
		cfdef.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
		cfdef.setDefaultValidationClass(ComparatorType.FLOATTYPE.getClassName());
		
		cluster.addKeyspace(kspdef, true);
		cluster.addColumnFamily(cfdef, true);
		
		
		
	}
	
	
	
	public void shutdown(){
		cluster.getConnectionManager().shutdown();		
	}
}
