/**
 * 
 */
package topic_indexer;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

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

	public ArrayList<Patent> search(Topic topic) {
		
		ArrayList<Patent> patents = new ArrayList<Patent>();
		
		System.out.println("Searching (" + topic.text + ").....");		
		PreparedStatement ps = searcher.prep("select id, title, abstract, description, claims, uspc from patents where lower(title) like \"%"+topic.text+"%\"");
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
		
		return patents;
	}	
}
