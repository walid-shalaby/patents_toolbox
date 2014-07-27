/**
 * 
 */
package topic_indexer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import commons.SQLiteDB;

/**
 * @author walid-shalaby
 *
 */
class Topic {
	/**
	 * placeholder for each sustainability and resilience related topic 
	 */
	public final int id;
	public final String text;
	Topic(int id, String text) {
		this.id = id;
		this.text = text;
		//System.out.println(this.id + "_" + this.text);
	}
}
public class Topics extends ArrayList<Topic>{

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
		// load all topics not previously indexed		
		PreparedStatement ps = db.prep("select id,topic from topics where id not in (select topic_id from topic_patents)");
		ResultSet rs;
		try {
			rs = ps.executeQuery();
			while (rs.next()) {
				// create a new topic
				add(new Topic(rs.getInt(1),rs.getString(2)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
	}
	public void writePatents(IndexPatent[] patents) {
		// insert into patents table
		PreparedStatement patents_bulk_insert = db.prep("insert into patents (id,abstract,description,class,main_classification,title," + 
														"subclass,claims,claims_statement,kind,type,state,assignee," + 
														"further_classification,publication_date,city) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		// insert into topic_patents table
		PreparedStatement topic_patents_bulk_insert = db.prep("insert into topic_patents (topic_id,patent_id,score) values (?,?,?)");
		for(int i=0; i<patents.length; i++) {
			try {
				patents_bulk_insert.setString(1,patents[i].id);
				patents_bulk_insert.setString(2,patents[i].abstract_text);
				patents_bulk_insert.setString(3,patents[i].description);
				patents_bulk_insert.setString(4,patents[i].main_class);
				patents_bulk_insert.setString(5,patents[i].main_classification);
				patents_bulk_insert.setString(6,patents[i].title);
				patents_bulk_insert.setString(7,patents[i].subclass);
				patents_bulk_insert.setString(8,patents[i].claims);
				patents_bulk_insert.setString(9,patents[i].claims_statement);
				patents_bulk_insert.setString(10,patents[i].kind);
				patents_bulk_insert.setString(11,patents[i].type);
				patents_bulk_insert.setString(12,patents[i].state);
				patents_bulk_insert.setString(13,patents[i].assignee);
				patents_bulk_insert.setString(14,patents[i].further_classification);
				patents_bulk_insert.setString(15,patents[i].publication_date);
				patents_bulk_insert.setString(16,patents[i].city);
				patents_bulk_insert.addBatch();
				
				topic_patents_bulk_insert.setInt(1,patents[i].topic_id);
				topic_patents_bulk_insert.setString(2,patents[i].id);
				topic_patents_bulk_insert.setFloat(3,patents[i].score);
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
