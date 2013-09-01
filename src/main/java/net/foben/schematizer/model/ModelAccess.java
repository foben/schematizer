package net.foben.schematizer.model;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.foben.schematizer.Environment;
import net.foben.schematizer.util.ThreeTuple;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

public class ModelAccess {

    private static Logger _log = LoggerFactory.getLogger(ModelAccess.class);

    public static Set<LOVVocab> getLOVVocabularies() {
	Set<LOVVocab> result = new HashSet<LOVVocab>(400);
	String line = null;
	try {
	    BufferedReader in = new BufferedReader(new FileReader(FILE_LOV_VOCABULARIES));
	    while ((line = in.readLine()) != null) {
		String[] arr = line.split(CSVDELIM);
		result.add(new LOVVocab(arr[0], arr[1], arr[0]));
	    }
	    in.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ArrayIndexOutOfBoundsException e) {
	    _log.error("Missing fields in this line: {}", line);
	}
	return result;
    }

    public static Set<String> getLOVNamespaces() {
	Set<String> result = new HashSet<String>(400);
	String line = null;
	try {
	    BufferedReader in = new BufferedReader(new FileReader(FILE_LOV_VOCABULARIES));
	    while ((line = in.readLine()) != null) {
		String[] arr = line.split(CSVDELIM);
		result.add(arr[1]);
	    }
	    in.close();
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} catch (ArrayIndexOutOfBoundsException e) {
	    _log.error("Missing fields in this line: {}", line);
	}
	return result;
    }

    public static List<String> getTopResources(int top) {
	return getTopResources(top, false);
    }

    public static List<String> getTopResources(int top, boolean properties) {
	List<String> result = new ArrayList<String>();
	try {
	    String filename = properties ? FILE_PROPERTIES : FILE_CLASSES;
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    String line = "";
	    int count = 0;
	    while ((line = br.readLine()) != null) {
		result.add(line.split(CSVDELIM)[0]);
		count++;
		if (count >= top)
		    break;
	    }
	    br.close();

	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return result;
    }

    public static List<ThreeTuple<String, Integer, Integer>> getResourceTuples(int top, String filename) {
	List<ThreeTuple<String, Integer, Integer>> result = new LinkedList<ThreeTuple<String, Integer, Integer>>();
	try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
	    String line;
	    int count = 0;
	    while ((line = in.readLine()) != null) {
		count++;
		try {
		    String[] lineArr = line.split(CSVDELIM);
		    result.add(new ThreeTuple<String, Integer, Integer>(lineArr[0], Integer.parseInt(lineArr[1]),
			    Integer.parseInt(lineArr[2])));
		} catch (Exception e) {
		    String[] foo = { "" + count, filename, e.toString() };
		    _log.warn("Error on line {} of {} : {}", foo);

		}
		if (count >= top)
		    break;
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
	return result;
    }

    public static ComparableResourceDescriptor[] getCandidates(Class<?> expected, int top) {
	return getCandidates(expected, top, false);
    }

    public static ComparableResourceDescriptor[] getCandidates(Class<?> expected, int top, boolean properties) {
	String resourceFile = properties ? FILE_PROPERTIES : FILE_CLASSES;
	return getCandidates(expected, top, resourceFile);
    }

    public static ComparableResourceDescriptor[] getCandidates(Class<?> expected, int top, String filename) {
	String resourceFile = filename;
	List<ThreeTuple<String, Integer, Integer>> resources = getResourceTuples(top, resourceFile);
	if (expected.equals(SimpleResourceDescriptor.class)) {
	    TreeMultiset<SimpleResourceDescriptor> s = TreeMultiset
		    .create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());

	    for (ThreeTuple<String, Integer, Integer> resource : resources) {
		s.add(new SimpleResourceDescriptor(resource));
	    }
	    return s.toArray(new SimpleResourceDescriptor[0]);

	} else if (expected.equals(LabelsCommentsResourceDescriptor.class)) {
	    WrappedRepo repo = new WrappedRepo();
	    Environment.DataFiles.addAllSchemaFiles(repo.getConnection());
	    repo.addFile(FILE_ALL_EQUIVALENCES);
	    RepositoryConnection con = repo.getConnection();

	    TreeMultiset<LabelsCommentsResourceDescriptor> s = TreeMultiset
		    .create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());

	    for (ThreeTuple<String, Integer, Integer> resource : resources) {
		s.add(new LabelsCommentsResourceDescriptor(resource, con));
	    }
	    repo.close();
	    return s.toArray(new LabelsCommentsResourceDescriptor[0]);

	}
	return null;
    }

}
