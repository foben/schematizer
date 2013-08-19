package net.foben.schematizer;

public class Environment {

	public static class DataFiles {

		public static final String FILE_CLASSES = "src/main/resources/resourcedata/classes_sorted";
		public static final String FILE_PROPERTIES = "src/main/resources/resourcedata/classes_sorted";

		public static final String FILE_ALL_EQUIVALENCES = "src/main/resources/mappingdata/equivalentclasses_all";

		public static final String FILE_SCHEMADATA_LDSPIDER = "src/main/resources/schemadata/schemadata_ldspider";
		public static final String FILE_SCHEMADATA_LOVS_COMBINED = "src/main/resources/schemadata/schemadata_lovs_combined";

		public static final String FILE_LOV_VOCABULARIES = "src/main/resources/schemadata/lov_vocabularies.csv";

	}

	public static final String RDFTYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

	public static final String URI_IDS = "http://dws.informatik.uni-mannheim.de/lodschema/ids/";
	public static final String URI_PROPERTIES = "http://dws.informatik.uni-mannheim.de/lodschema/properties/";
	public static final String URI_DATASET = "http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset";
	public static final String URI_TYPESPEC = "http://dws.informatik.uni-mannheim.de/lodschema/types/TypeSpec";
	public static final String URI_HASGRAPH = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph";

}
