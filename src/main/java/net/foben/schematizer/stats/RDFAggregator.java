package net.foben.schematizer.stats;

import static net.foben.schematizer.Environment.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.ntriples.NTriplesWriter;
import org.openrdf.sail.memory.MemoryStore;

//TODO: Statements about documents (e.g. ontologies) are referenced by absolute filepath!
public class RDFAggregator {
    static final Set<String> rdfaList;
    static {
	rdfaList = new HashSet<String>();
	rdfaList.add("media");
	rdfaList.add("cc");
	rdfaList.add("cc");
	rdfaList.add("cc");
	rdfaList.add("cc");
	rdfaList.add("cc");
    }

    public static void main(String[] args) throws RepositoryException, IOException, RDFParseException,
	    RDFHandlerException {
	Repository repo = new SailRepository(new MemoryStore());
	repo.initialize();
	RepositoryConnection con = repo.getConnection();
	configureTolerantParser(con);
	int count = 0;

	Set<String> fails = new HashSet<String>();
	for (String arg : args) {
	    System.out.print(arg + String.format(" (%s of %s) .. ", ++count, args.length));
	    File inputfile = new File(arg);
	    // FIXME: Fix semargl parser
	    // System.err.close();

	    try {
		if (rdfaList.contains(arg.substring(arg.lastIndexOf("/") + 1))) {
		    throw new RDFParseException("declared as RDFa");
		}
		con.add(inputfile, null, RDFFormat.RDFXML);
		System.out.println("[ok (rdfxml)]");
	    } catch (RDFParseException rdfxml) {
		try {
		    con.add(inputfile, null, RDFFormat.N3);
		    System.out.println("[ok (N3)]");
		} catch (RDFParseException n3) {
		    try {
			con.add(inputfile, null, RDFFormat.TURTLE);
			System.out.println("[ok (Turtle)]");
		    } catch (RDFParseException turtle) {

			try {
			    con.add(inputfile, null, RDFFormat.RDFA, (Resource) null);
			    System.out.println("[ok (RDFa)]");
			} catch (RDFParseException rdfa) {

			    System.out.println("[fail]");
			    System.err.println("fail");
			    fails.add(arg);
			    System.out.println("rdfa   :" + rdfa.getMessage());
			    System.out.println("rdfxml :" + rdfxml.getMessage());
			    System.out.println("n3     :" + n3.getMessage());
			    System.out.println("turtle :" + turtle.getMessage());
			}
		    }
		}
	    }
	}
	con.commit();
	con.export(new NTriplesWriter(new FileWriter("lovoldnew")), (Resource) null);

	System.out.println();
	for (String s : fails) {
	    System.out.println(s);
	}

	int failcount = fails.size();
	int total = args.length;
	int parsed = total - failcount;
	System.out.println("done");
	System.out.println(String.format("%s of %s parsed (%s fails)", parsed, total, failcount));
	System.out.println("Total statements: " + con.size((Resource) null));
    }

}
