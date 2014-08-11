package patent_indexer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import commons.Patent;
import commons.SQLiteDB;

/**
 * placeholder for each array of all sustainability and resilience related patents in DB
 */
public class DBPatents extends ArrayList<Patent> {

	private static final long serialVersionUID = 1L;

	private SQLiteDB db = null;
	DBPatents(String datasrcpath) {
		// connect to the DB
		db = new SQLiteDB(datasrcpath);
	}
	public void load(HashMap<String, String> columnFieldDic) {
		// load all patents from DB
		
		// determine columns to load
		Iterator<String> columns = columnFieldDic.keySet().iterator();
		String stmt = "select ";
		if(columns.hasNext()==true) {
			stmt += columns.next();
			while(columns.hasNext()==true)
				stmt += "," + columns.next();
		}
		stmt += " from patents";
		PreparedStatement ps = db.prep(stmt);
		ResultSet rs;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				// create a new patent
				add(new Patent(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
	}
}
