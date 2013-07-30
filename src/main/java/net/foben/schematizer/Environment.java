package net.foben.schematizer;


public class Environment {
	public static final String OWLCLASS = "http://www.w3.org/2002/07/owl#Class";
	
	public static final String RDFSCLASS = "http://www.w3.org/2000/01/rdf-schema#Class";
	public static final String RDFSUBCLASSOF = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	
	public static final String RDFTYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	
	public static final String XS_DINT = "http://www.w3.org/2001/XMLSchema#integer";
	
	public static final String URI_IDS = "http://dws.informatik.uni-mannheim.de/lodschema/ids/";
	public static final String URI_PROPERTIES = "http://dws.informatik.uni-mannheim.de/lodschema/properties/";
	public static final String URI_DATASET = "http://dws.informatik.uni-mannheim.de/lodschema/types/Dataset";
	public static final String URI_TYPESPEC = "http://dws.informatik.uni-mannheim.de/lodschema/types/TypeSpec";
	
	public static final String URI_HASGRAPH = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasGraph";
	public static final String URI_HASTYPESPEC = "http://dws.informatik.uni-mannheim.de/lodschema/properties/hasTypeSpec";
	public static final String URI_DESCTYPE = "http://dws.informatik.uni-mannheim.de/lodschema/properties/describesType";
	public static final String URI_TYPEOCC = "http://dws.informatik.uni-mannheim.de/lodschema/properties/typeOccurences";
	
	public static final String URI_STATEMENTCOUNT = "http://dws.informatik.uni-mannheim.de/lodschema/properties/statementCount";

	
	
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
	
	public static void printArgs(String[] args){
		System.out.println("Arguments:");
		System.out.println("-----------------");
		for(int i = 0; i < args.length; i++){
			System.out.println(args[i]);
		}
	}
}
