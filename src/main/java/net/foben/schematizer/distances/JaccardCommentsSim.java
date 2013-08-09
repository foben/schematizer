package net.foben.schematizer.distances;

import java.util.Set;

import org.openrdf.model.Literal;

import com.wcohen.ss.Jaccard;
import com.wcohen.ss.tokens.SimpleTokenizer;

public class JaccardCommentsSim implements ISimmilarityMeasure<LabeledResDescriptor> {
	
	private Jaccard jacc;
	
	public JaccardCommentsSim(){
		jacc = new Jaccard(new SimpleTokenizer(true, true));
	}
	
	@Override
	public double getSim(LabeledResDescriptor s, LabeledResDescriptor t) {		
		Set<Literal> sComms = s.getComments();
		Set<Literal> tComms = t.getComments();
		String sCommStr = "";
		String tCommStr = "";
		//TODO: How to handle inexistent comments?
		if(sComms != null){
			for(Literal l : sComms){
				sCommStr += l.stringValue() + " ";
			}
		}
		if(tComms != null){
			for(Literal l : tComms){
				tCommStr += l.stringValue() + " ";
			}
		}
		
		double res = jacc.score(jacc.prepare(sCommStr), jacc.prepare(tCommStr));
		if(Double.isNaN(res)) return -1;
		return res;
	}

	@Override
	public String getMeasureName() {
		return "JaccardComments";
	}




}
