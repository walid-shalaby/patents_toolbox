/**
 * 
 */
package topic_crawler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import commons.Concept;
import commons.Patent;
import commons.SQLiteDB;

/**
 * Using sqlite DB of all patents, search sustainability related patents and return query hits 
 */
public class TopicDBSearcher implements TopicSearcher {

	private SQLiteDB searcher = null;
	
	public TopicDBSearcher(String datasrcpath) {		
		// open the input DB
		searcher = new SQLiteDB(datasrcpath);
		
	}

	public ArrayList<Patent> search(Concept topic, TopicLookupModeEnum topicLookupMode) {
		
		ArrayList<Patent> patents = new ArrayList<Patent>();
		
		System.out.println("Searching (" + topic.text + ").....");
		PreparedStatement ps;
		if(topic.text.equals("*"))
			ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents");
		else {			
			if(topicLookupMode==TopicLookupModeEnum.e_TITLE)
				ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents where lower(title) like \"%"+topic.text+"%\"");
			else if(topicLookupMode==TopicLookupModeEnum.e_ABSTRACT)
				ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents where lower(abstract) like \"%"+topic.text+"%\"");
			else if(topicLookupMode==TopicLookupModeEnum.e_TITLE_ABSTRACT)
				ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents where lower(title) like \"%"+topic.text+"%\" or lower(abstract) like \"%"+topic.text+"%\"");
			else
				ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents");
		}
		ResultSet rs;
		try {
			rs = ps.executeQuery();
			while(rs.next()) {
				patents.add(new Patent(rs,topic.id));
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(patents.size()==0)
			patents = null;
		else
			System.out.println("Searching (" + topic.text + ") resulted in (" + patents.size() + ") hits.....");
		return patents;
	}	
}
