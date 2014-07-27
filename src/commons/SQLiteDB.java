package commons;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author unknown
 *
 */
public class SQLiteDB {
	private final Connection conn;
	private static final Map<String, Connection> connections = new HashMap<String, Connection>();
	private final Map<String, PreparedStatement> statements = new HashMap<String, PreparedStatement>();
	private final String datasrcPath;
	
	
	public SQLiteDB(String datasrcpath) {
		this.datasrcPath = datasrcpath;
		// First, see if the database is already opened.
		if (connections.containsKey(datasrcPath)) {
			conn = connections.get(datasrcPath); 
		} else {
			try {
			    // Load the sqlite-JDBC driver using the current class loader
			    Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:" + datasrcPath);
				conn.createStatement().execute("PRAGMA journal_mode = TRUNCATE;");
				conn.createStatement().execute("PRAGMA synchronous = OFF;");

				// JDBC's SQLite uses autocommit (So commit() is redundant)
				// Furthermore, close() is a no-op as long as the results are commit()'d
			} catch(SQLException | ClassNotFoundException e) {
		       // if the error message is "out of memory", 
		       // it probably means no database file is found
		       e.printStackTrace();
		       throw new RuntimeException("Can't run without a database.");
			}
			connections.put(datasrcPath, conn);
		}		
	}
	
	/** Caching proxy for Connection.prepareStatement.
	 * Repeated calls to this method are efficient. */
	public PreparedStatement prep(String sql) {
		PreparedStatement ps = statements.get(sql);
		if (ps == null) {
			try {
				ps = conn.prepareStatement(sql);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't prepare an SQL statement \"" + sql + "\"");
			}
			statements.put(sql, ps);
		}
		return ps;
	}
}
