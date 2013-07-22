package net.foben.schematizer.app;

import org.openrdf.model.URI;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Table;

import net.foben.schematizer.stats.TypeStat;
import net.foben.schematizer.util.WrappedRepo;

import static net.foben.schematizer.Environment.*;

public class STATRDFizer {
	
	static URI a = new URIImpl(RDFTYPE);
	static URI Dataset = new URIImpl(URI_DATASET);
	static URI TypeSpec = new URIImpl(URI_TYPESPEC);
	static URI xsd_int = new URIImpl(XS_DINT);
	
	static URI statementCount = new URIImpl(URI_STATEMENTCOUNT);
	static URI hasTypeSpec = new URIImpl(URI_HASTYPESPEC);
	static URI describesType = new URIImpl(URI_DESCTYPE);
	static URI typeOccurences = new URIImpl(URI_TYPEOCC);
	
//	static URI describesType = new URIImpl(URI_DESCTYPE);
	
	private static Logger _log;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		_log = LoggerFactory.getLogger(STATRDFizer.class);
		Table<String, String, TypeStat> stats = new TypeStatsAggregator(args).parseTypeStats();
		WrappedRepo repo = new WrappedRepo("repooo");
		
		int pds = 0;
		int maxds = 1;
		int pty = 0;
		int maxty = 5;
		int totals = 0;
		maxty = maxds = Integer.MAX_VALUE;
		//Create TypeSpecs
		for(String dataset : stats.rowKeySet()){
			pds++;
			URI ds = new URIImpl(URI_IDS + dataset);
			repo.queue(ds, a, Dataset);
			for(String type : stats.columnKeySet()){
				pty++;
				TypeStat stat = stats.get(dataset, type);
				if(stat != null && !type.equals(stat.getType())) throw new IllegalStateException("TYPES DONT MATCH");
				if (stat != null){
					if(type.equals("TOTAL")){
						totals++;
						int xy = stat.getCount();
						if (xy < 0){
							System.out.println();
						}
						repo.queue(ds, statementCount, new LiteralImpl(""+stat.getCount(), xsd_int));
					}
					else{
						String typespec = URI_IDS + "typespec" + getAlphaNumeric(6);
						URIImpl ts = new URIImpl(typespec);
						repo.queue(ts, a, TypeSpec);
						repo.queue(ds, hasTypeSpec, ts);
						repo.queue(ts, describesType, new URIImpl(type));
						repo.queue(ts, typeOccurences, new LiteralImpl(""+stat.getCount(), xsd_int));
					}
				}
				else{
					
				}
				if(pty >= maxty) break;
			}
			_log.info("Finished dataset no. " + pds);
			if(pds >= maxds) break;
		}
		repo.flushQueue();
		repo.toFile("rep");
		repo.close();
	}

}
