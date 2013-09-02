package net.foben.schematizer.test;

import java.util.List;

import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.util.ThreeTuple;

public class ThreeTupleTest {

    public static void main(String[] args) {
	List<ThreeTuple<String, Integer, Integer>> li = ModelAccess.getResourceTuples(500,
		"src/main/resources/resourcedata/classgroups/computer2");
	for (ThreeTuple<String, Integer, Integer> l : li) {
	    System.out.println(l);
	}

    }
}
