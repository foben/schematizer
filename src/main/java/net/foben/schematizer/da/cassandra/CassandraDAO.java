package net.foben.schematizer.da.cassandra;

import me.prettyprint.cassandra.serializers.FloatSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.SliceQuery;
import net.foben.schematizer.da.DAO;
import net.foben.schematizer.model.ResDescriptor;

public class CassandraDAO implements DAO {
	Cluster cluster;
	String KEYSPACE_NAME = "schematizer";
	String CF_NAME = "lev";
	Keyspace keyspace;
	ColumnFamilyTemplate<String, Float> template;
	
	public CassandraDAO(String cfname){
		this.CF_NAME = cfname;
		init();
	}
	
	public void init(){
		cluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		cluster.getConnectionManager().setCassandraHostRetryDelay(25);
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		if (keyspaceDef == null) {
		    createKSDef();
		}
		keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		boolean cfExists = false;
		for(ColumnFamilyDefinition cf : keyspaceDef.getCfDefs()){
			if(CF_NAME.equals(cf.getName())) cfExists = true;
		}
		if(!cfExists){
			createCFDef();
		}
		
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		template = new ThriftColumnFamilyTemplate<String, Float>(keyspace, CF_NAME, StringSerializer.get(), FloatSerializer.get());
	}
	
	public void queue(ResDescriptor row, ResDescriptor column, double simil) {
		ColumnFamilyUpdater<String, Float> upd = template.createUpdater(row.getType() + " " + column.getType());
		upd.setString((float)simil, "");
		template.update(upd);
	}	
	
	private void createKSDef(){
		KeyspaceDefinition kspdef = HFactory.createKeyspaceDefinition(KEYSPACE_NAME);
		cluster.addKeyspace(kspdef, true);
	}
	
	private void createCFDef(){
		ColumnFamilyDefinition cfdef = HFactory.createColumnFamilyDefinition(KEYSPACE_NAME, CF_NAME, ComparatorType.FLOATTYPE);
		cfdef.setKeyValidationClass(ComparatorType.UTF8TYPE.getClassName());
		cfdef.setDefaultValidationClass(ComparatorType.FLOATTYPE.getClassName());
		cluster.addColumnFamily(cfdef, true);
	}	
	
	public void terminate(){
		cluster.getConnectionManager().shutdown();		
	}

	@Override
	public void executeBatch() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args){
		CassandraDAO dao = new CassandraDAO("LevenstheinNormTop1000Types");
		dao.getData();
	}
	
	public void getData(){
		SliceQuery<String, Float, String> q =HFactory.createSliceQuery(keyspace, StringSerializer.get(), FloatSerializer.get(), StringSerializer.get());
		q.setColumnFamily(CF_NAME);
		q.setRange(0.8f, 0.9f, false, 0);
		QueryResult<ColumnSlice<Float, String>> res = q.execute();
		
		ColumnSlice<Float, String> sl = res.get();
		for( HColumn<Float, String> col : sl.getColumns()){
			System.out.println(col);
		}
		
	}
}
