package net.foben.schematizer;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Environment {

    private static Logger _log = LoggerFactory.getLogger(Environment.class);

    public static class DataFiles {

	public static final String FILE_CLASSES = "src/main/resources/resourcedata/classes_sorted.csv";
	public static final String FILE_PROPERTIES = "src/main/resources/resourcedata/properties_sorted.csv";

	public static final String FILE_ALL_EQUIVALENCES = "src/main/resources/mappingdata/equivalentclasses_all";

	// SCHEMA-DATA
	public static final String FILE_SCHEMADATA_LDSPIDER = "src/main/resources/schemadata/schemadata_ldspider.nq";
	public static final String FILE_SCHEMADATA_AGGREGATED_CRAWL = "src/main/resources/schemadata/aggregatedCrawl.n3";
	public static final String FILE_SCHEMADATA_DBPEDIA_ONTOLOGY = "src/main/resources/schemadata/schemadata_dbpedia_3.8.owl";
	public static final String FILE_SCHEMADATA_METALEX = "src/main/resources/schemadata/schemadata_metalex.owl";
	public static final String FILE_SCHEMADATA_CREATIVECOMMONS = "src/main/resources/schemadata/schemadata_creaivecommons.rdf";
	public static final String FILE_SCHEMADATA_LOV = "src/main/resources/schemadata/schemadata_lovs_final.nt";
	public static final String FILE_SCHEMADATA_LD_TOP500CLASSES = "src/main/resources/schemadata/schemadata_ldspider_Top500classes.nq";
	public static final String FILE_SCHEMADATA_LD_TOP500PROPERTIES = "src/main/resources/schemadata/schemadata_ldspider_Top500properties.nq";

	public static final String FILE_SCHEMADATA_LD_TOP300MISSING = "src/main/resources/schemadata/schemadata_output_top300missing.nq";

	public static final Map<String, RDFFormat> ALL_SCHEMA_MAP;

	static {
	    ALL_SCHEMA_MAP = new HashMap<String, RDFFormat>();
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_LDSPIDER, RDFFormat.NQUADS);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_AGGREGATED_CRAWL, RDFFormat.N3);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_DBPEDIA_ONTOLOGY, RDFFormat.RDFXML);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_METALEX, RDFFormat.RDFXML);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_CREATIVECOMMONS, RDFFormat.RDFXML);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_LOV, RDFFormat.NTRIPLES);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_LD_TOP500CLASSES, RDFFormat.NQUADS);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_LD_TOP500PROPERTIES, RDFFormat.NQUADS);
	    ALL_SCHEMA_MAP.put(FILE_SCHEMADATA_LD_TOP300MISSING, RDFFormat.NQUADS);
	}

	public static final String FILE_LOV_VOCABULARIES = "src/main/resources/lov_vocabularies.csv";

	public static void addAllSchemaFiles(RepositoryConnection con) {
	    configureTolerantParser(con);
	    _log.info("Adding all available schema files to repository");
	    for (String file : ALL_SCHEMA_MAP.keySet()) {
		try {
		    con.add(new File(file), null, ALL_SCHEMA_MAP.get(file), (Resource) null);
		    _log.info("{} added successfully!", file);
		} catch (Exception e) {
		    e.printStackTrace();
		    _log.error("An excpetion occured while adding {} schema files: {}", file, e.getMessage());
		}
	    }
	    _log.info("Data import complete");
	}

    }

    public static final String CSVDELIM = ";";

    public static final String RDFTYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public static final String RDFSSUBCLASS = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
    public static final String RDFSCOMMENT = "http://www.w3.org/2000/01/rdf-schema#comment";
    public static final String RDFSSLABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    public static final String RDFSDOMAIN = "http://www.w3.org/2000/01/rdf-schema#domain";
    public static final String RDFSRANGE = "http://www.w3.org/2000/01/rdf-schema#range";
    public static final String RDFSSUBPROPERTY = "http://www.w3.org/2000/01/rdf-schema#subPropertyOf";

    public static final String OWLEQUICLASS = "http://www.w3.org/2002/07/owl#equivalentClass";
    public static final String OWLEQUIPROPERTY = "http://www.w3.org/2002/07/owl#equivalentProperty";

    public static URI URI_RDFTYPE = new URIImpl(RDFTYPE);

    public static URI URI_RDFSSUBCLASS = new URIImpl(RDFSSUBCLASS);
    public static URI URI_RDFSCOMMENT = new URIImpl(RDFSCOMMENT);
    public static URI URI_RDFSSLABEL = new URIImpl(RDFSSLABEL);
    public static URI URI_RDFSDOMAIN = new URIImpl(RDFSDOMAIN);
    public static URI URI_RDFSRANGE = new URIImpl(RDFSRANGE);
    public static URI URI_RDFSSUBPROPERTY = new URIImpl(RDFSSUBPROPERTY);
    public static URI URI_OWLEQUICLASS = new URIImpl(OWLEQUICLASS);

    public static final Map<String, URI> classStatistics = new LinkedHashMap<String, URI>();
    public static final Map<String, URI> propertyStatistics = new LinkedHashMap<String, URI>();
    static {
	classStatistics.put(RDFSSLABEL, URI_RDFSSLABEL);
	classStatistics.put(RDFSCOMMENT, URI_RDFSCOMMENT);
	classStatistics.put(RDFSSUBCLASS, URI_RDFSSUBCLASS);

	propertyStatistics.put(RDFSSLABEL, URI_RDFSSLABEL);
	propertyStatistics.put(RDFSCOMMENT, URI_RDFSCOMMENT);
	propertyStatistics.put(RDFSSUBPROPERTY, URI_RDFSSUBPROPERTY);
	propertyStatistics.put(RDFSDOMAIN, URI_RDFSDOMAIN);
	propertyStatistics.put(RDFSRANGE, URI_RDFSRANGE);
    }

    public static final String URI_IDS = "http://dws.informatik.uni-mannheim.de/lodschema/ids/";
    public static final String URI_PROPERTIES = "http://dws.informatik.uni-mannheim.de/lodschema/properties/";
    public static final String URI_DATASET = "http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset";
    public static final String URI_TYPESPEC = "http://dws.informatik.uni-mannheim.de/lodschema/types/TypeSpec";
    public static final String URI_HASGRAPH = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph";

    public static void configureTolerantParser(RepositoryConnection con) {
	_log.info("Configuring tolerant Parser");
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
