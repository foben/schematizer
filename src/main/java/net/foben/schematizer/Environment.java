package net.foben.schematizer;

import org.openrdf.model.impl.URIImpl;

public class Environment {
	public static final String RDFSCLASS = "http://www.w3.org/2000/01/rdf-schema#Class";
	public static final String RDFSUBCLASSOF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	
	public static final String RDFTYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	public static final String URI_IDS = "http://dws.informatik.uni-mannheim.de/lodschema/ids/";
	public static final String URI_PROPERTIES = "http://dws.informatik.uni-mannheim.de/lodschema/properties/";
	public static final String URI_DATASET = "http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset";
	public static final String URI_HASGRAPH = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph";
	public static final String URI_HASCLASS = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasClass";

	public static final String URI_EXTERNALVOCAB  = "http://dws.informatik.uni-mannheim.de/lodschema/properties/usesExternalVocabulary";
	public static final String URI_EXTERNALRES  = "http://dws.informatik.uni-mannheim.de/lodschema/properties/usesExternalResource";
	
	public static final URIImpl RES_HAS_CLASS = new URIImpl(URI_HASCLASS);
	
	
	public static final String Q_DATASETS = "SELECT ?a WHERE {?a a <http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset> } LIMIT %s ";
	public static final String Q_GRAPHS = "SELECT ?g WHERE {<%s> <http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph> ?g } ";
	public static final String Q_CLASSES = "SELECT ?c WHERE { GRAPH <%s> {?a a ?c} }";
	public static final String Q_CLASSES2 = "SELECT ?c WHERE { GRAPH ?g {?a a ?c} FILTER}";
	
	private static final String ALPHA_NUM = "0123456789abcdefghijklmnopqrstuvwxyz";
	
	public static String getAlphaNumeric(int len) {
		StringBuffer sb = new StringBuffer(len);
		for (int i=0;  i<len;  i++) {
			int ndx = (int)(Math.random()*ALPHA_NUM.length());
			sb.append(ALPHA_NUM.charAt(ndx));
		}
		return sb.toString();
	}
}
