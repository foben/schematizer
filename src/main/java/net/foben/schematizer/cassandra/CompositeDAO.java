package net.foben.schematizer.cassandra;

import java.util.HashMap;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.FloatSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;

public class CompositeDAO {
	Cluster cluster;
	String KEYSPACE_NAME = "comp";
	String CF_NAME = "lev";
	Keyspace keyspace;
	ColumnFamilyTemplate<String, Composite> template;
	StringSerializer stringSer;
	FloatSerializer floatSer;
	CompositeSerializer compSer;
	
	public CompositeDAO(String cfname){
		this.CF_NAME = cfname;
		this.stringSer = StringSerializer.get();
		this.floatSer = FloatSerializer.get();
		this.compSer = CompositeSerializer.get();		
		init();
	}
	
	public void init(){
		cluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		cluster.getConnectionManager().setCassandraHostRetryDelay(25);
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		if (keyspaceDef == null) {
		    createKSDef();
		    keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		}
		
		boolean cfExists = false;
		for(ColumnFamilyDefinition cf : keyspaceDef.getCfDefs()){
			if(CF_NAME.equals(cf.getName())) cfExists = true;
		}
		if(!cfExists){
			createCFDef();
		}
		
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		template = new ThriftColumnFamilyTemplate<String, Composite>(keyspace, CF_NAME, stringSer, compSer);
	}
	
	public void addData(String key, HashMap<String, Float> values){
		
		ColumnFamilyUpdater<String, Composite>  updater = template.createUpdater("all");
		for(String row : values.keySet()){
			Composite comp = new Composite();
			comp.setSerializersByPosition(stringSer, stringSer, floatSer);
			comp.add(0, key);
			comp.add(1, row);
			comp.add(2, values.get(row));
			updater.setString(comp, "");
			//ColumnFamilyUpdater<Composite, String> updater = template.createUpdater(comp);
			template.update(updater);
		}
	}	
	
	private void createKSDef(){
		KeyspaceDefinition kspdef = HFactory.createKeyspaceDefinition(KEYSPACE_NAME);
		cluster.addKeyspace(kspdef, true);
	}
	
	private void createCFDef(){
//		ColumnFamilyDefinition cfdef = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, CF_NAME, ComparatorType.UTF8TYPE);
//		cfdef.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
//		cfdef.setDefaultValidationClass(ComparatorType.FLOATTYPE.getClassName());
//		cluster.addColumnFamily(cfdef, true);
		
		ColumnFamilyDefinition cfdef = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, CF_NAME, ComparatorType.COMPOSITETYPE);
		cfdef.setComparatorTypeAlias("(UTF8Type, UTF8Type, FloatType)");
		cfdef.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
		cfdef.setDefaultValidationClass(ComparatorType.UTF8TYPE.getClassName());
		cluster.addColumnFamily(cfdef, true);
	}	
	
	public void shutdown(){
		cluster.getConnectionManager().shutdown();		
	}
}
