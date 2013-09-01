package net.foben.schematizer.app;

import static net.foben.schematizer.Environment.*;
import static net.foben.schematizer.Environment.DataFiles.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import net.foben.schematizer.model.LabelsCommentsResourceDescriptor;
import net.foben.schematizer.model.ModelAccess;

import org.apache.commons.lang.StringUtils;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.ntriples.NTriplesWriter;

public class CrossMappingProposer {

    public static void main(String[] args) throws IOException, RDFHandlerException {
	if (args.length != 3 || !(new File(args[2]).exists())) {
	    System.out.println("Incorrect number of arguments, must be offset offset filename !");
	    System.exit(0);
	}

	int c1off = Integer.parseInt(args[0]);
	int c2off = Integer.parseInt(args[1]);

	String inputFileName = args[2];
	String inputFileLocalName = new File(inputFileName).getName();

	int top = 3;

	LabelsCommentsResourceDescriptor[] candidates = (LabelsCommentsResourceDescriptor[]) ModelAccess.getCandidates(
		LabelsCommentsResourceDescriptor.class, top, inputFileName);

	LabelsCommentsResourceDescriptor[] topclasses = (LabelsCommentsResourceDescriptor[]) ModelAccess.getCandidates(
		LabelsCommentsResourceDescriptor.class, top, FILE_CLASSES);

	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	int filecount = 0;
	String outFileName = "temp/custom_matchings_" + inputFileLocalName + " _" + filecount + ".nt";
	while ((new File(outFileName)).exists()) {
	    filecount++;
	    outFileName = "temp/custom_matchings_" + inputFileLocalName + " _" + filecount + ".nt";
	}

	FileOutputStream fout = new FileOutputStream(outFileName);
	NTriplesWriter ntWriter = new NTriplesWriter(fout);
	ntWriter.startRDF();

	outerloop: for (int c1 = 0; c1 < candidates.length; c1++) {
	    for (int c2 = 0; c2 < topclasses.length; c2++) {
		// "Clear" screen
		for (int cl = 0; cl < 300; cl++) {
		    System.out.println();
		}
		if (c1 == 0 && c2 == 0) {
		    c1 = c1off;
		    c2 = c2off;
		}
		LabelsCommentsResourceDescriptor cand1 = candidates[c1];
		LabelsCommentsResourceDescriptor cand2 = topclasses[c2];

		if (cand1.equals(cand2))
		    continue;

		display(cand1, cand2, c1, c2, candidates.length, topclasses.length);

		URI c1URI = new URIImpl(cand1.getURI());
		URI c2URI = new URIImpl(cand2.getURI());
		// Handling input
		String input = in.readLine();

		if (input.toLowerCase().startsWith("y")) {
		    ntWriter.handleStatement(new StatementImpl(c1URI, URI_OWLEQUICLASS, c2URI));
		    fout.flush();
		} else if (input.startsWith("sc")) {
		    if (input.equals("sc12")) {
			ntWriter.handleStatement(new StatementImpl(c1URI, URI_RDFSSUBCLASS, c2URI));
		    } else if (input.equals("sc21")) {
			ntWriter.handleStatement(new StatementImpl(c2URI, URI_RDFSSUBCLASS, c1URI));
		    }
		    fout.flush();
		} else if (input.equals("b")) {

		    c2 -= 2;
		    if (c2 < c1)
			c2 = c1;
		    continue;

		} else if (input.equals("quit") || input.equals("exit")) {
		    System.out.println("Use these offsets: " + c1 + "  " + c2);
		    BufferedWriter bww = new BufferedWriter(new FileWriter("continue.sh"));
		    bww.write(String
			    .format("java -Xmx5000m -Xms5000m -cp target/foobar-0.0.1-SNAPSHOT-jar-with-dependencies.jar net.foben.schematizer.app.MappingProposer %s %s %s",
				    c1, c2, inputFileName));
		    bww.close();
		    break outerloop;
		}
	    }
	}
	ntWriter.endRDF();
	fout.close();
    }

    public static void display(LabelsCommentsResourceDescriptor cand1, LabelsCommentsResourceDescriptor cand2, int c1,
	    int c2, int total1, int total2) {
	displayDescription(cand1, c1, total1);
	System.out.println("\n");
	displayDescription(cand2, c2, total2);
    }

    private static void displayDescription(LabelsCommentsResourceDescriptor candidate, int c, int total) {
	int width = 70;
	char pad = '*';
	Set<Literal> comments = candidate.getComments();
	Set<Literal> labels = candidate.getLabels();
	System.out.println(StringUtils.leftPad(c + "/" + (total - 1) + "**", width, pad));
	System.out.println(candidate.getURI());
	System.out.println(StringUtils.leftPad("", width, pad));
	System.out.print("   ");
	if (labels != null) {
	    for (Literal l : labels) {
		System.out.print(l.stringValue() + ",  ");
	    }
	}
	System.out.println();
	System.out.println(StringUtils.leftPad("", width, pad));
	if (comments != null) {
	    for (Literal l : comments) {
		System.out.println("   " + l.stringValue());
	    }
	}
    }
}
