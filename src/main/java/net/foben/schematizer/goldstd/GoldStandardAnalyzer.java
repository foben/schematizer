package net.foben.schematizer.goldstd;

import static net.foben.schematizer.Environment.*;

import java.util.HashSet;
import java.util.Set;

import net.foben.schematizer.model.Mapping;
import net.foben.schematizer.util.UtilityFunctions;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldStandardAnalyzer {

    Logger _log;

    private WrappedRepo repo;
    private Set<Mapping> uniqueMappings;
    private long totalMappings;

    private int equalMappings;
    private int substringMappings;
    private int otherMappings;

    public GoldStandardAnalyzer(String filename) throws RepositoryException {
	_log = LoggerFactory.getLogger(GoldStandardAnalyzer.class);
	repo = new WrappedRepo();
	configureTolerantParser(repo.getConnection());
	repo.addFile(filename, RDFFormat.NTRIPLES);
	uniqueMappings = new HashSet<Mapping>();
	RepositoryResult<Statement> res = repo.getStatements(null, OWLEQUICLASS, null, (String) null);
	try {
	    while (res.hasNext()) {
		Statement st = res.next();
		if ((st.getSubject() instanceof URI) && (st.getObject() instanceof URI)) {
		    boolean succ = uniqueMappings.add(new Mapping((URI) st.getSubject(), (URI) st.getObject()));
		    if (!succ) {
			_log.info("Duplicate mapping: {}", st);

		    }

		}
	    }
	} catch (RepositoryException e) {
	    e.printStackTrace();
	}
	totalMappings = uniqueMappings.size();
    }

    public long getEquiClassMappings() {
	return totalMappings;
    }

    public boolean localNameMatch(Mapping m, boolean matchCase) {
	String ln1 = m.getUri1AsURI().getLocalName();
	String ln2 = m.getUri2AsURI().getLocalName();
	if (!matchCase) {
	    ln1 = ln1.toLowerCase();
	    ln2 = ln2.toLowerCase();
	}
	return ln1.equals(ln2);
    }

    public boolean substringMatch(Mapping m, int minLen, boolean matchCase) {

	Set<String> matchingSubs = new HashSet<String>();
	String ln1 = m.getLocalName1();
	String ln2 = m.getLocalName2();
	if (!matchCase) {
	    ln1 = ln1.toLowerCase();
	    ln2 = ln2.toLowerCase();
	}
	if (ln1.equals(ln2)) {
	    return true;
	}
	String shorter, longer;
	if (ln1.length() > ln2.length()) {
	    shorter = ln2;
	    longer = ln1;
	} else {
	    shorter = ln1;
	    longer = ln2;
	}
	for (int len = shorter.length(); len >= minLen; len--) {
	    Set<String> shorterss = UtilityFunctions.getSubStrings(shorter, len);
	    Set<String> longerss = UtilityFunctions.getSubStrings(longer, len);
	    for (String subs : shorterss) {
		if (longerss.contains(subs)) {
		    matchingSubs.add(subs);
		}
	    }
	    if (matchingSubs.size() > 0)
		break;
	}
	return (matchingSubs.size() > 0);

    }

    private void calculateStatistics(boolean matchCase) {
	for (Mapping m : uniqueMappings) {
	    if (localNameMatch(m, matchCase)) {
		equalMappings++;
	    } else if (substringMatch(m, 3, matchCase)) {
		substringMappings++;
	    } else {
		otherMappings++;
		// System.out.println(m);
	    }
	}
	System.out.println(equalMappings);
	System.out.println(substringMappings);
	System.out.println(otherMappings);
	System.out.println("==========");
	System.out.println(totalMappings);

    }

    public static void main(String[] args) throws RepositoryException {
	for (String arg : args) {
	    System.out.println(arg + " :");
	    GoldStandardAnalyzer gsa = new GoldStandardAnalyzer(arg);
	    gsa.calculateStatistics(false);
	    System.out.println("\n");
	}

    }

}
