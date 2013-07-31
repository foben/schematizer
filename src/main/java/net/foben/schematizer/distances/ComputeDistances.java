package net.foben.schematizer.distances;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeSet;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class ComputeDistances {

	public static void main(String[] args) throws IOException {
		
		
		URI foo = new URIImpl("akt:Person");
		System.out.println(foo.getNamespace());
		
		NormalizedLevensthein lev = new NormalizedLevensthein();
		
		BufferedReader in = new BufferedReader(new FileReader("ExtractTypesHandlerOutput"));
		String line;
		TreeSet<String> xax = new TreeSet<String>();
		TreeSet<String> yax = new TreeSet<String>();
		while((line = in.readLine()) != null){
			xax.add(line);
			yax.add(line);
		}
		in.close();
		System.out.println("read complete");
		int i = 0;
		for(String x : xax){
			
			try{
				URI xuri = new URIImpl(x);
			} catch (Exception e){
				System.out.println(++i + ": " + e.getMessage());
			}
			/*
			for(String y : yax){
				double val;
				try{
					URI xuri = new URIImpl(x);
					URI yuri = new URIImpl(y);
					val = lev.getDistance(xuri.getLocalName(), yuri.getLocalName());
					if(val <= 0.15 && val > 0){
						System.out.println();
						System.out.println(xuri.getLocalName() + " <> " + yuri.getLocalName() + "  : " + val);
						System.out.println(x + " <> " + y + "  : " + val);
					}
				} catch(Exception e){
					val = 1;
					//System.out.println(e.getMessage());
				}
			}*/
		}
	}

}
