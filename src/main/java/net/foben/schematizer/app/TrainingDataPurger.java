package net.foben.schematizer.app;

import static net.foben.schematizer.Environment.DataFiles.FILE_ALL_EQUIVALENCES;
import static net.foben.schematizer.Environment.DataFiles.FILE_SCHEMADATA_LOVS_COMBINED;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.foben.schematizer.model.ModelAccess;
import net.foben.schematizer.util.WrappedRepo;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

public class TrainingDataPurger {

	public static void main(String[] args) throws RDFParseException,
			RepositoryException, IOException, InterruptedException {
		WrappedRepo repo = new WrappedRepo();
		RepositoryConnection con = repo.getConnection();

		con.add(new File(FILE_ALL_EQUIVALENCES), null, RDFFormat.TURTLE,
				(Resource) null);
		// con.add(new File(schemaFileLDSpider), null, RDFFormat.NQUADS,
		// (Resource)null);
		con.add(new File(FILE_SCHEMADATA_LOVS_COMBINED), null,
				RDFFormat.NQUADS, (Resource) null);

		Set<String> fails = new HashSet<String>();
		for (String arg : args) {
			System.out.print(arg);
			try {
				con.add(new File(arg), null, RDFFormat.RDFA);
				System.out.println("  [ok]");
			} catch (RDFParseException e0) {
				try {
					con.add(new File(arg), null, RDFFormat.N3);
					System.out.println("  [ok]");
				} catch (RDFParseException e1) {
					try {
						con.add(new File(arg), null, RDFFormat.TURTLE);
						System.out.println("  [ok]");
					} catch (RDFParseException e2) {
						try {
							con.add(new File(arg), null, RDFFormat.RDFXML);
							System.out.println("  [ok]");
							continue;
						} catch (RDFParseException e3) {
							System.out.println("  [FAIL]");
							fails.add(arg);
							// e3.printStackTrace();

						}
					}
				}
			}
		}

		for (String s : fails) {
			System.out.println(s);
		}

		List<String> types = ModelAccess.getTopTypes(500);

		RepositoryResult<Statement> res = repo.getStatements((String) null,
				"http://www.w3.org/2002/07/owl#equivalentClass", (String) null,
				(String) null);
		// RepositoryResult<Statement> res = con.getStatements((Resource) null,
		// new URIImpl("http://www.w3.org/2002/07/owl#equivalentClass"), (Value)
		// null, false, (Resource)null);
		int results = 0;
		BufferedWriter out = new BufferedWriter(new FileWriter("temp/equizz"));
		while (res.hasNext()) {
			Statement st = res.next();
			String sub = st.getSubject().stringValue();
			String obj = st.getObject().stringValue();
			if (types.contains(sub) && types.contains(obj)) {
				out.write(st.toString());
				out.newLine();
				results++;
			}
		}
		out.flush();
		out.close();
		System.out.println("-- " + results);
		System.out.println();
		repo.close();
	}

}
