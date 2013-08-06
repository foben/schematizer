package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class VocabularyExtractor {

	public static void main(String[] args) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("src/main/resources/stats/sorted_types"));
		String line;
		HashSet<String> vocabs = new HashSet<String>();
		while((line = in.readLine()) != null){
			String[] fields = line.split(" ");
			URI uri = new URIImpl(fields[0]);
			//System.out.println(uri.stringValue() + "  >  " + uri.getNamespace());
			vocabs.add(uri.getNamespace());
		}
		in.close();
		in = new BufferedReader(new FileReader("src/main/resources/stats/sorted_props"));
		while((line = in.readLine()) != null){
			String[] fields = line.split(" ");
			URI uri = new URIImpl(fields[0]);
			//System.out.println(uri.stringValue() + "  >  " + uri.getNamespace());
			vocabs.add(uri.getNamespace());
		}
		in.close();
		for(String str : vocabs){
			System.out.println(str);
		}

	}

}
