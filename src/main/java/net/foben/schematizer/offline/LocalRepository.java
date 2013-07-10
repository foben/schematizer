package net.foben.schematizer.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalRepository {
	
	private String repofolder;
	private Logger _log;
	
	public LocalRepository(String repofolder){
		_log = LoggerFactory.getLogger(LocalRepository.class);
		this.repofolder = repofolder;
		_log.error(repofolder);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}
}
