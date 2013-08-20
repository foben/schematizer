package net.foben.schematizer;

import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;

public class Environment {

    public static class DataFiles {

	public static final String FILE_CLASSES = "src/main/resources/resourcedata/classes_sorted.csv";
	public static final String FILE_PROPERTIES = "src/main/resources/resourcedata/classes_sorted.csv";

	public static final String FILE_ALL_EQUIVALENCES = "src/main/resources/mappingdata/equivalentclasses_all";

	// SCHEMA-DATA
	public static final String FILE_SCHEMADATA_LDSPIDER = "src/main/resources/schemadata/schemadata_ldspider";
	// public static final String FILE_SCHEMADATA_LOVS_PYTHONCRAWL_COMBINED
	// = "src/main/resources/schemadata/schemadata_lovs_pythoncrawl.nt";
	// public static final String FILE_SCHEMADATA_LOVS_LDSPIDER =
	// "src/main/resources/schemadata/schemadata_lovs_ldspider.nq";
	public static final String FILE_SCHEMADATA_DBPEDIA_ONTOLOGY = "src/main/resources/schemadata/schemadata_dbpedia_3.8.owl";
	public static final String FILE_SCHEMADATA_AGGREGATED_CRAWL = "src/main/resources/schemadata/aggregatedCrawl.nq";

	public static final String FILE_SCHEMADATA_LOV = "src/main/resources/schemadata/schemadata_lovs_final";

	public static final String[] ALL_SCHEMA_FILES = { FILE_SCHEMADATA_LDSPIDER, FILE_SCHEMADATA_DBPEDIA_ONTOLOGY,
		FILE_SCHEMADATA_AGGREGATED_CRAWL, FILE_SCHEMADATA_LOV };

	public static final String FILE_LOV_VOCABULARIES = "src/main/resources/lov_vocabularies.csv";

    }

    public static final String RDFTYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final String RDFSSUBCLASS = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    public static final String RDFSCOMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";
    public static final String RDFSSLABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String RDFSDOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
    public static final String RDFSRANGE = "http://www.w3.org/2000/01/rdf-schema#range";

    public static URI URI_RDFTYPE = new URIImpl(RDFTYPE);

    public static URI URI_RDFSSUBCLASS = new URIImpl(RDFSSUBCLASS);
    public static URI URI_RDFSCOMMENT = new URIImpl(RDFSCOMMENT);
    public static URI URI_RDFSSLABEL = new URIImpl(RDFSSLABEL);
    public static URI URI_RDFSDOMAIN = new URIImpl(RDFSDOMAIN);
    public static URI URI_RDFSRANGE = new URIImpl(RDFSRANGE);

    public static final Map<String, URI> classStatistics = new HashMap<String, URI>();

    static {
	classStatistics.put(RDFSCOMMENT, URI_RDFSCOMMENT);
	classStatistics.put(RDFSSLABEL, URI_RDFSSLABEL);
	// classStatistics.put(RDFSSUBCLASS, URI_RDFSSUBCLASS);
    }

    public static final String URI_IDS = "http://dws.informatik.uni-mannheim.de/lodschema/ids/";
    public static final String URI_PROPERTIES = "http://dws.informatik.uni-mannheim.de/lodschema/properties/";
    public static final String URI_DATASET = "http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset";
    public static final String URI_TYPESPEC = "http://dws.informatik.uni-mannheim.de/lodschema/types/TypeSpec";
    public static final String URI_HASGRAPH = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph";

    public static void configureTolerantParser(RepositoryConnection con) {
	con.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
	con.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
	con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
	con.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
	con.getParserConfig().set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);
	con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
	con.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);
	con.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
	con.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
	con.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
	con.getParserConfig().addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);

    }
}
