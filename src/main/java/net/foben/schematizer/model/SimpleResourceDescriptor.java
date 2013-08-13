package net.foben.schematizer.model;

public class SimpleResourceDescriptor extends ComparableResourceDescriptor{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -647131551265820718L;

	public SimpleResourceDescriptor(String uri, int total, int datasets) {
		super(uri, total, datasets);
	}

//	private static final long serialVersionUID = 1336L;
//	private int total;
//	private int datasets;
//	private String uri;
//	private String localName;
//	protected Logger _log;
	
//	public SimpleResourceDescriptor(String uri, int total, int datasets){
//		_log = LoggerFactory.getLogger(this.getClass());
//		this.uri = uri;
//		this.total = total;
//		this.datasets = datasets;
//		URI uriobj = new URIImpl(this.uri);
//		this.localName = uriobj.getLocalName();
//		if(datasets > total)
//			throw new IllegalArgumentException("dataset occurences cant be greater than total occurences");
//	}
	
//	@Override
//	public int compareTo(SimpleResourceDescriptor o) {
//		int otot = o.getTotal();
//		if(o.getURI().equals(this.getURI())){
//			if(otot > total) return -1;
//			if(otot < total) return 1;
//			int odat = o.getDatasets();
//			if(odat > datasets) return -1;
//			if(odat < datasets) return 1;
//			return 0;
//		}
//		if(otot > total) return -1;
//		if(otot < total) return 1;
//		int odat = o.getDatasets();
//		if(odat > datasets) return -1;
//		if(odat < datasets) return 1;
//		return o.getURI().compareTo(uri);
//		
//	}
	
//	public String toString(){
//		return this.uri;
//	}
//	
//	@Override
//	public boolean equals(Object o){
//		if(!(o instanceof SimpleResourceDescriptor)) return false;
//		return this.compareTo((SimpleResourceDescriptor) o) == 0 ? true : false;
//	}
//
//	public int getTotal() {
//		return total;
//	}
//
//	public int getDatasets() {
//		return datasets;
//	}
//	
//	public String getURI(){
//		return this.uri;
//	}
//	
//	public String getLocalName() {
//		return localName;
//	}
//	
//	public static class ResDescriptorReverseOrdering extends Ordering<SimpleResourceDescriptor> implements Serializable{
//
//		private static final long serialVersionUID = 24L;
//
//		@Override
//		public int compare(SimpleResourceDescriptor left, SimpleResourceDescriptor right) {
//			return left.compareTo(right) * -1;
//		}
//	}

}
