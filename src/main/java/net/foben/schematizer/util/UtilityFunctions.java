package net.foben.schematizer.util;

import java.util.HashSet;
import java.util.Set;

public class UtilityFunctions {
    public static Set<String> getSubStrings(String string, int len) {
	HashSet<String> result = new HashSet<String>();
	int strlen = string.length();
	if (len > strlen)
	    return result;
	for (int i = 0; i <= (strlen - len); i++) {
	    // System.out.println(string.substring(i, i + len));
	    result.add(string.substring(i, i + len));
	}
	return result;
    }

    public static void main(String[] args) {
	System.out.println(getSubStrings("hello", 6));
    }
}
