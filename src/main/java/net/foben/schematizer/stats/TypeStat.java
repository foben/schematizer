package net.foben.schematizer.stats;

public class TypeStat {
	private String dataset;
	private String type;
	private int count;
	
	public TypeStat(String dataset, String type){
		this(dataset, type, 0);
	}
	
	public TypeStat(String dataset, String type, int initial){
		this.dataset = dataset;
		this.type = type;
		this.count = initial;
	}
	
	public void incr(){
		count++;
	}
	
	public void incr(int i){
		if(count + i < 0){
			System.out.println();
		}
		count += i;
	}

	public String getDataset() {
		return dataset;
	}

	public String getType() {
		return type;
	}

	public int getCount() {
		return count;
	}
	
	public String toString(){
		//return "[" + dataset + " | " + type + " | " + count + "]";
		return String.format("[%s]", count);
	}
	
}
