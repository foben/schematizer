package net.foben.schematizer.mysql;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import net.foben.schematizer.distances.ResDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySQLDAO {
	
	private Connection conn = null;
	private PreparedStatement pst = null;
	private int batchCount = 0;
	private int duplicateCount = 0;
	private int updateCount = 0;
	private final int maxBatch = 1000000;
	private String dbName = "schematizer";
	private String tableName;
	private boolean operational = true;
	Logger _log;
	
	public static void main(String[] args){
		MySQLDAO inst = new MySQLDAO("testta");
		
		ResDescriptor r1 = new ResDescriptor("http://xmlns.com/foaf/0.1/Document", 0, 0);
		ResDescriptor r2 = new ResDescriptor("http://xmlns.com/foaf/0.1/Doc", 25, 5);
		ResDescriptor r3 = new ResDescriptor("http://xmlns.com/foaf/0.1/Document2", 0, 0);
		ResDescriptor r4 = new ResDescriptor("http://xmlns.com/foaf/0.1/Doc2", 25, 5);
		ResDescriptor r5 = new ResDescriptor("http://xmlns.com/foaf/0.1/Document3", 0, 0);
		ResDescriptor r6 = new ResDescriptor("http://xmlns.com/foaf/0.1/Doc3", 25, 5);
		
		inst.queue(r2, r1, 0.2);
		//inst.executeBatch();
		inst.queue(r4, r3, 0.2);
		//inst.executeBatch();
		inst.queue(r5, r6, 0.2);
		
		inst.terminate();
	}
	
	public MySQLDAO(String tableName){
		this.tableName = tableName;
		this._log = LoggerFactory.getLogger(this.getClass().getName());
		
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/schematizer?useServerPrepStmts=true&rewriteBatchedStatements=true",
					"root","dbpass");
			
			pst = conn.prepareStatement(String.format("INSERT INTO %s.%s values ( ?, ?, ?)", dbName, tableName));
			createTable();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void createTable(){
		try {
			java.sql.Statement createSt = conn.createStatement();

			createSt.executeUpdate(String.format ("CREATE TABLE IF NOT EXISTS %s.%s("
												+ "res1 VARCHAR(255) NOT NULL ,"
												+ "res2 VARCHAR(255) NOT NULL ,"
												+ "similarity DOUBLE NULL ,"
												+ "PRIMARY KEY (res1, res2),"
												+ "INDEX (similarity) )"
												+ "DEFAULT CHARACTER SET utf8 COLLATE utf8_bin,"
												+ "ENGINE = MYISAM", dbName, tableName));
			
			SQLWarning w = createSt.getWarnings();
			if(w == null) _log.info("Table {} successfully created", tableName);
			else _log.warn("There were warnings: {}", w.getMessage());
			
		} catch (SQLException e) {
			if(e.getMessage().startsWith("Unknown database")){
				_log.error("Database does not exist, DAO not operational!  ({})", e.getMessage());
				operational = false;
			}
			else e.printStackTrace();
		}
	}

	public void queue(ResDescriptor row, ResDescriptor column, double simil) {
		if(!operational()) return;
		try {
			String uri1 = row.getType();
			String uri2 = column.getType();
			pst.setString(1, uri1);
			pst.setString(2, uri2);
			pst.setDouble(3, simil);
			pst.addBatch();
			
			if(++batchCount%maxBatch == 0){
				executeBatch();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void executeBatch(){
		if(!operational()) return;
		_log.info("Executing batch");
		try {
			int res[] = pst.executeBatch();
			for(int i : res){
				if(i == Statement.EXECUTE_FAILED) duplicateCount++;
				if(i > 0) updateCount++;
			}

		} catch(BatchUpdateException be){
			if(be.getMessage().startsWith("Duplicate entry")) {
				_log.error(be.getMessage());
				int[] cts = be.getUpdateCounts();
				for(int i :cts) {
					if(i == Statement.EXECUTE_FAILED) duplicateCount++;
					if(i > 0) updateCount++;
				}
			}
			else be.printStackTrace();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pst.clearBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			batchCount = 0;
		}
		try {
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_log.info("batch execution complete");
	}
	
	public void terminate(){
		executeBatch();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(!operational){
				_log.error("Shutting down non-operational DAO");
				return;
			}
			if(duplicateCount > 0) _log.warn("{} duplicate inserts", duplicateCount);
			else _log.info("No duplicate inserts");
			if(updateCount > 0) _log.info("{} successful inserts", updateCount);
			else _log.warn("No successful updates");
		}
	}
	
	private boolean operational(){
		if(!operational){
			StackTraceElement ste = Thread.currentThread().getStackTrace()[2];
			_log.error("This DAO is not operational, action ignored ({})", ste.getMethodName());
		}
		return operational;
	}

}
