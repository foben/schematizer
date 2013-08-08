package net.foben.schematizer.cassandra;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.FloatSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.SliceQuery;

public class CompQueryTest {

	static String KEYSPACE_NAME = "comp";
	static String CF_NAME = "test";
	static ColumnFamilyTemplate<String, Composite> template;
	static Keyspace keyspace;
	static StringSerializer stringSer = StringSerializer.get();
	static FloatSerializer floatSer = FloatSerializer.get();
	static CompositeSerializer compSer = CompositeSerializer.get();	
	
	public static void main(String[] args) {
		Cluster cluster = HFactory.getOrCreateCluster("test-cluster","localhost:9160");
		KeyspaceDefinition keyspaceDef = cluster.describeKeyspace(KEYSPACE_NAME);
		keyspace = HFactory.createKeyspace(KEYSPACE_NAME, cluster);
		template = new ThriftColumnFamilyTemplate<String, Composite>(keyspace, CF_NAME, stringSer, compSer);
		
		Composite start = new Composite();
		start.addComponent(0, "http", Composite.ComponentEquality.EQUAL);
		
		Composite end = new Composite();
		end.addComponent(0, "https", Composite.ComponentEquality.GREATER_THAN_EQUAL);
		
		
		SliceQuery<String, Composite, String> sliceQuery = HFactory.createSliceQuery(keyspace, stringSer, compSer, stringSer);
		sliceQuery.setColumnFamily(CF_NAME).setKey("all");
		ColumnSliceIterator<String, Composite, String> csIterator = new ColumnSliceIterator<String, Composite, String>(sliceQuery, start, end, false);
		while(csIterator.hasNext()){
			HColumn<Composite, String> col = csIterator.next();
			System.out.println((String)(col.getName().get(0)));
		}
		
	}

}
