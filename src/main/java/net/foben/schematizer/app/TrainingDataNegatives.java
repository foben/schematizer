package net.foben.schematizer.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.distances.NormalizedLevenstheinSim;
import net.foben.schematizer.distances.app.ComputeDistances;
import net.foben.schematizer.model.LabelsCommentsResourceDescriptor;
import net.foben.schematizer.model.SimpleResourceDescriptor;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.TreeMultiset;

import static net.foben.schematizer.Environment.DataFiles.*;

public class TrainingDataNegatives {

	static Logger _log = LoggerFactory.getLogger(ComputeDistances.class);
	static long last;

	public static void main(String[] args) throws RDFParseException,
			RepositoryException, IOException {
		int typesprops = -1;
		int top = -1;
		try {
			System.out.println("Types(1) or Props(2) ??");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			typesprops = Integer.parseInt(br.readLine());

			System.out.println("How much ???");
			top = Integer.parseInt(br.readLine());

		} catch (IOException e) {
			e.printStackTrace();
		}

		WrappedRepo repo = new WrappedRepo();
		configureParser(repo.getConnection());
		repo.addFile(FILE_SCHEMADATA_LDSPIDER);
		repo.addFile(FILE_ALL_EQUIVALENCES);
		LabelsCommentsResourceDescriptor[] candArray;
		if (typesprops == 1)
			candArray = getCandidates(top, FILE_CLASSES, repo.getConnection());
		else if (typesprops == 2)
			candArray = getCandidates(top, FILE_PROPERTIES,
					repo.getConnection());
		else {
			repo.close();
			throw new IllegalArgumentException("You fool");
		}
		repo.close();

		long total = candArray.length * (candArray.length + 1) / 2;
		long oneP = Math.max(((long) (total * 0.01)), 1);
		long count = 0;
		JaccardCommentsSim jac = new JaccardCommentsSim();
		NormalizedLevenstheinSim lev = new NormalizedLevenstheinSim();

		BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter out = new BufferedWriter(new FileWriter(
				"temp/training_neg"));

		for (int rowi = 0; rowi < candArray.length; rowi++) {
			LabelsCommentsResourceDescriptor row = candArray[rowi];
			for (int coli = rowi; coli < candArray.length; coli++) {
				LabelsCommentsResourceDescriptor column = candArray[coli];
				// System.out.println(row.getType() + " <> " + column.getType()
				// + "  ??");
				// String ans = rd.readLine().trim().replace("\n", "");
				// System.out.println(ans);

				// if(ans.equals("n") || ans.equals("N")){
				if (!row.getURI().equals(column.getURI())) {

					double jacv = jac.getSim(row, column);
					double levv = lev.getSim(row, column);
					out.write(String.format("%s<>%s;%s;%s;0", row.getURI(),
							column.getURI(), jacv, levv));
					out.newLine();
					out.write(String.format("%s<>%s;%s;%s;0", column.getURI(),
							row.getURI(), jacv, levv));
					out.newLine();
					out.flush();
				}
				if (++count % oneP == 0) {
					_log.info(((int) (count * 10000d / total)) / 100d + "%");
				}
			}
		}
		out.close();
		rd.close();

	}

	private static LabelsCommentsResourceDescriptor[] getCandidates(int top,
			String filename, RepositoryConnection con) throws IOException {
		TreeMultiset<LabelsCommentsResourceDescriptor> s = TreeMultiset
				.create(new SimpleResourceDescriptor.ResDescriptorReverseOrdering());
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line;
		int count = 0;
		while ((line = in.readLine()) != null && ++count <= top) {
			LabelsCommentsResourceDescriptor t = null;
			try {
				String[] fields = line.split(" ");
				t = new LabelsCommentsResourceDescriptor(fields[0],
						Integer.parseInt(fields[1]),
						Integer.parseInt(fields[2]), con);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (t != null) {
				s.add(t);
			}
		}
		in.close();
		return s.toArray(new LabelsCommentsResourceDescriptor[0]);
	}

	private static void configureParser(RepositoryConnection con) {
		con.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES,
				false);
		con.getParserConfig().set(
				BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		con.getParserConfig().set(
				BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS,
				false);
		con.getParserConfig().set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS,
				false);
		con.getParserConfig().set(
				BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS,
				false);
		con.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
		con.getParserConfig().addNonFatalError(
				BasicParserSettings.VERIFY_DATATYPE_VALUES);
		con.getParserConfig().addNonFatalError(
				BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
		con.getParserConfig().addNonFatalError(
				NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);

	}

}
