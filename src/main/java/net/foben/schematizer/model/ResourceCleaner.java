package net.foben.schematizer.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class ResourceCleaner {

    static Set<String> namespaceBlacklist;
    static {
	namespaceBlacklist = new HashSet<String>();
	namespaceBlacklist.add("http://www.w3.org/2002/07/owl#");
	namespaceBlacklist.add("http://www.w3.org/2006/http#");
	namespaceBlacklist.add("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
	namespaceBlacklist.add("http://www.w3.org/2000/01/rdf-schema#");
    }

    public static void main(String[] args) {

	for (String arg : args) {
	    File inputFile = new File(arg);
	    File outputFile = new File("temp/" + inputFile.getName() + "_new");
	    File blockedFile = new File("temp/" + inputFile.getName() + "_blocked");

	    try (BufferedReader in = new BufferedReader(new FileReader(inputFile));
		    BufferedWriter outNew = new BufferedWriter(new FileWriter(outputFile));
		    BufferedWriter outBlocked = new BufferedWriter(new FileWriter(blockedFile))) {
		String line = "";
		while ((line = in.readLine()) != null) {
		    String[] lineArr = line.split(";");
		    URI uri = new URIImpl(lineArr[0]);
		    String uriNS = uri.getNamespace();
		    if (namespaceBlacklist.contains(uriNS)) {
			outBlocked.write(line);
			outBlocked.newLine();
		    } else {
			outNew.write(line);
			outNew.newLine();
		    }
		}

	    } catch (FileNotFoundException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }
}
