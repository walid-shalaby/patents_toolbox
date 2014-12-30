/**
 * 
 */
package concept_searcher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.lucene.document.Document;

import commons.Patent;
import commons.SQLiteDB;
import commons.Concept;
/**
 * @author walid-shalaby
 *
 */
public class Concepts extends ArrayList<Concept>{

	/**
	 * placeholder for each array of all concepts in DB
	 */
	private static final long serialVersionUID = 1L;

	private SQLiteDB db = null;
	private String conceptsTable = null;
	private String conceptsField = null;
	private String targetTable = null;
	private String targetConceptField = null;
	private String targetPatentField = null;
	
	Concepts(String datasrcpath, String conceptsTableName, String conceptsFieldName, 
			String targetTableName, String targetConceptFieldName, String targetPatentFieldName) {
		// connect to the DB
		db = new SQLiteDB(datasrcpath);
		conceptsTable = conceptsTableName;
		conceptsField = conceptsFieldName;
		targetTable = targetTableName;
		targetConceptField = targetConceptFieldName;
		targetPatentField = targetPatentFieldName;
	}
	public void load() {
		// load all topTopicLookupModeEnumics not previously indexed		
		PreparedStatement ps = db.prep("select id,"+ conceptsField + " from " + conceptsTable);
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

	public void writeConceptHits(int id, ArrayList<Document> hits) {
		// TODO Auto-generated method stub

		// insert into patents table
		PreparedStatement concept_bulk_insert = db.prep("insert into "+targetTable+"("+targetConceptField+","+targetPatentField+") values (?,?)");
		for(Document h : hits) {
			try {
				concept_bulk_insert.setInt(1,id);
				concept_bulk_insert.setString(2,h.getField("patent_id").stringValue());
				concept_bulk_insert.addBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		try {		
			concept_bulk_insert.executeBatch();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
