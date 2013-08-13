package net.foben.schematizer.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelAccess {
	
	private static final String lovfile = "src/main/resources/lov/vocabularies.csv";
	private static Logger _log = LoggerFactory.getLogger(ModelAccess.class);
	
	
	public static Set<LOVVocab> getLOVVocabularies(){
		Set<LOVVocab> result = new HashSet<LOVVocab>(400);
		String line = null;
		try {
			BufferedReader in = new BufferedReader(new FileReader(lovfile));
			while((line = in.readLine()) != null){
				String[] arr = line.split(";");
				result.add(new LOVVocab(arr[0], arr[1], arr[0]));
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException e){
			_log.error("Missing fields in this line: {}", line);
		}
		return result;
	}
	
}
