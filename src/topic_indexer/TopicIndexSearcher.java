/**
 * 
 */
package topic_indexer;

import java.io.File;
import java.io.IOException;
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

/**
 * @author wshalaby
 *
 */

/**
 * Using lucene index of all patents, query sustainability related topic and return query hits 
 */
public class TopicIndexSearcher implements TopicSearcher {

	private IndexSearcher searcher = null;
	private QueryParser qparser = null;
	private IndexReader indexReader = null;
	
	public TopicIndexSearcher(String indexpath) {		
		try {
			// open the input index
			indexReader = DirectoryReader.open(FSDirectory.open(new File(indexpath)));
			
			// Initialize searcher and query parser
			searcher = new IndexSearcher(indexReader);
			qparser = new QueryParser(Version.LUCENE_46, "text", new StandardAnalyzer(Version.LUCENE_46));			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Patent> search(Topic topic) {
		
		ArrayList<Patent> patents = null;
		
		qparser.setAllowLeadingWildcard(true);				
		Query query;
		try {
			// search for topic in patent title field and return search results
			query = qparser.parse("Title:\""+topic.text+"\"");
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			System.out.println("Searching (" + query + ") resulted in (" + topDocs.totalHits + ") hits.....");
			if(topDocs.totalHits > 0) {			
				patents = new ArrayList<Patent>();
				 for(int i=0; i<topDocs.scoreDocs.length; i++)
					 patents.add(new Patent(topic.id, indexReader.document(topDocs.scoreDocs[i].doc),topDocs.scoreDocs[i].score));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return patents;
	}	
}
