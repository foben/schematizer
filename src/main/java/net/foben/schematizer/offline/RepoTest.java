package net.foben.schematizer.offline;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

public class RepoTest {

	/**
	 * @param args
	 * @throws RepositoryException 
	 */
	public static void main(String[] args) throws RepositoryException {
		MemRepository repo = new MemRepository();
		double x = 0e-9;
		System.out.println(x);
		
		System.out.println(repo.addFile("src/main/resources/quadtest_huge_fixed.nq"));
		
		RepositoryResult<Statement> res = repo.getStatements((Resource)null, null, null);
		int i = 0;
		while(res.hasNext()){
			System.out.println(res.next());
			if(i++ > 1000000) break;
		}
		
		
		
		repo.toFile("testout");
		repo.close();
	}

}
