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
	public void writePatents(ArrayList<Patent> patents) {
		// insert into patents table
		PreparedStatement patents_bulk_insert = db.prep("insert into patents (id,abstract,description,class,main_classification,title," + 
														"subclass,claims,claims_statement,kind,type,state,assignee," + 
														"further_classification,publication_date,city) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		// insert into topic_patents table
		PreparedStatement topic_patents_bulk_insert = db.prep("insert into topic_patents (topic_id,patent_id,score) values (?,?,?)");
		for(int i=0; i<patents.size(); i++) {
			try {
				Patent patent = patents.get(i);
				patents_bulk_insert.setString(1,patent.id);
				patents_bulk_insert.setString(2,patent.abstract_text);
				patents_bulk_insert.setString(3,patent.description);
				patents_bulk_insert.setString(4,patent.main_class);
				patents_bulk_insert.setString(5,patent.main_classification);
				patents_bulk_insert.setString(6,patent.title);
				patents_bulk_insert.setString(7,patent.subclass);
				patents_bulk_insert.setString(8,patent.claims);
				patents_bulk_insert.setString(9,patent.claims_statement);
				patents_bulk_insert.setString(10,patent.kind);
				patents_bulk_insert.setString(11,patent.type);
				patents_bulk_insert.setString(12,patent.state);
				patents_bulk_insert.setString(13,patent.assignee);
				patents_bulk_insert.setString(14,patent.further_classification);
				patents_bulk_insert.setString(15,patent.publication_date);
				patents_bulk_insert.setString(16,patent.city);
				patents_bulk_insert.addBatch();
				
				topic_patents_bulk_insert.setInt(1,patent.topic_id);
				topic_patents_bulk_insert.setString(2,patent.id);
				topic_patents_bulk_insert.setFloat(3,patent.score);
				topic_patents_bulk_insert.addBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			patents_bulk_insert.executeBatch();
			topic_patents_bulk_insert.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
}
