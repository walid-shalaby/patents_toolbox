/**
 * 
 */
package topic_crawler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import commons.Patent;
import commons.SQLiteDB;
import commons.Concept;
/**
 * @author walid-shalaby
 *
 */
public class Topics extends ArrayList<Concept>{

	/**
	 * placeholder for each array of all sustainability and resilience related topics in DB
	 */
	private static final long serialVersionUID = 1L;

	private SQLiteDB db = null;
	Topics(String datasrcpath) {
		// connect to the DB
		db = new SQLiteDB(datasrcpath);
	}
	public void load() {
		// load all topTopicLookupModeEnumics not previously indexed		
		PreparedStatement ps = db.prep("select id,topic from topics where id not in (select topic_id from topic_patents)");
		ResultSet rs;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				// create a new topic
				add(new Concept(rs.getInt(1),rs.getString(2)));
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
