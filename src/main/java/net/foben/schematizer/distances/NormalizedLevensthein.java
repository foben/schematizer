package net.foben.schematizer.distances;

import com.wcohen.ss.Levenstein;

public class NormalizedLevensthein implements IDistanceCalculator<String> {
	
	Levenstein lev;
	
	public NormalizedLevensthein(){
		lev = new Levenstein();
	}
	
	@Override
	public double getDistance(String s, String t) {
		int max = Math.max(s.length(), t.length());
		double levscore = lev.score(s, t);
		return Math.abs(levscore) / max;
	}


}
