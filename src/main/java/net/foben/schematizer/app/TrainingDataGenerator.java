package net.foben.schematizer.app;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.foben.schematizer.distances.JaccardCommentsSim;
import net.foben.schematizer.distances.NormalizedLevenstheinSim;
import net.foben.schematizer.model.LabelsCommentsResourceDescriptor;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.NTriplesParserSettings;
import org.openrdf.sail.memory.MemoryStore;

public class TrainingDataGenerator {

	public static void main(String[] args) throws RepositoryException, RDFParseException, IOException {
		String equi = "http://www.w3.org/2002/07/owl#equivalentClass";
		Repository repo = new SailRepository(new MemoryStore());
		repo.initialize();
		RepositoryConnection con = repo.getConnection();
		configureParser(con);
		con.add(new File("src/main/resources/all_equis"), null, RDFFormat.NQUADS, (Resource)null);
		con.add(new File("src/main/resources/vocabularies.nq"), null, RDFFormat.NQUADS, (Resource)null);
		RepositoryResult<Statement> res = con.getStatements((Resource)null, new URIImpl(equi), (Value)null, false, (Resource)null);
		
		BufferedWriter br = new BufferedWriter(new FileWriter("temp/training"));
		JaccardCommentsSim jac = new JaccardCommentsSim();
		NormalizedLevenstheinSim lev = new NormalizedLevenstheinSim();
		
		System.out.println("start writing");
		int failcount = 0;
		
		while(res.hasNext()){
			Statement st = res.next();
			String s = st.getSubject().stringValue();
			String o = st.getObject().stringValue();
			try{
				LabelsCommentsResourceDescriptor sr = new LabelsCommentsResourceDescriptor(s, 0, 0, con);
				LabelsCommentsResourceDescriptor or = new LabelsCommentsResourceDescriptor(o, 0, 0, con);
				double jacv = jac.getSim(sr, or);
				double levv = lev.getSim(sr, or);
				
				
				br.write(String.format("%s<>%s;%s;%s;1", s, o, jacv, levv));
				br.newLine();
				br.write(String.format("%s<>%s;%s;%s;1", o, s, jacv, levv));
				br.newLine();
			} catch(IllegalArgumentException e){
				System.out.println(++failcount);
				continue;
			}
			
		}
		br.close();
		
	}
	
	private static void configureParser(RepositoryConnection con) {
		con.getParserConfig().set(BasicParserSettings.VERIFY_DATATYPE_VALUES, false);
		con.getParserConfig().set(BasicParserSettings.NORMALIZE_DATATYPE_VALUES, false);
		con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_LANGUAGE_TAGS, false);
		con.getParserConfig().set(BasicParserSettings.NORMALIZE_LANGUAGE_TAGS, false);
		con.getParserConfig().set(BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES, false);
		con.getParserConfig().set(BasicParserSettings.VERIFY_RELATIVE_URIS, false);
		con.getParserConfig().set(BasicParserSettings.PRESERVE_BNODE_IDS, true);
		con.getParserConfig().addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
		con.getParserConfig().addNonFatalError(BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES);
		con.getParserConfig().addNonFatalError(NTriplesParserSettings.FAIL_ON_NTRIPLES_INVALID_LINES);
		
	}

}
