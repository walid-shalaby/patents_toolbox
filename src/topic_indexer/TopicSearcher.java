/**
 * 
 */
package topic_indexer;

import java.io.File;
import java.io.IOException;

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

/**
 * @author wshalaby
 *
 */

/**
 * A placeholder for patent in index matching a sustainability topic 
 */
class IndexPatent {
	public final String id;
	public final String abstract_text;
	public final String description; 
	public final String main_class; 
	public final String title;
	public final String subclass; 
	public final String claims;
	public final String claims_statement; 
	public final String kind;
	public final String type;
	public final String state;
	public final String assignee;
	public final String further_classification; 
	public final String publication_date; 
	public final String city;
	public final String main_classification;
	public final int topic_id;
	public final float score;
	
	IndexPatent(int topic_id, Document d, float score) {
		IndexableField f = d.getField("PatentId");
		if(f!=null)
			id = f.stringValue();
		else id = "";
		
		f = d.getField("Title");
		if(f!=null)
			title = f.stringValue();
		else title = "";
		
		f = d.getField("AbstractText");
		if(f!=null)
			abstract_text  = f.stringValue();
		else abstract_text  = "";
		 
		f = d.getField("Description");
		if(f!=null)
			description = f.stringValue();
		else description = "";
		
		f = d.getField("Class");
		if(f!=null)
			main_class = f.stringValue();
		else main_class = "";
		
		f = d.getField("MainClassification");
		if(f!=null)
			main_classification = f.stringValue();
		else main_classification = "";
		
		f = d.getField("SubClass");
		if(f!=null)
			subclass = f.stringValue();
		else subclass = "";
				
		f = d.getField("claimText");
		if(f!=null)
			claims = f.stringValue();
		else claims = "";
				
		f = d.getField("usClaimStatement");
		if(f!=null)
			claims_statement = f.stringValue();
		else claims_statement = "";
		
		f = d.getField("kind");
		if(f!=null)
			kind = f.stringValue();
		else kind = "";
		
		f = d.getField("Type");
		if(f!=null)
			type = f.stringValue();
		else type = "";
		
		f = d.getField("State");
		if(f!=null)
			state = f.stringValue();
		else state = "";
		
		f = d.getField("Assignee");
		if(f!=null)
			assignee = f.stringValue();
		else assignee = "";
				
		f = d.getField("FurtherClassification");
		if(f!=null)
			further_classification = f.stringValue();
		else further_classification = "";
				
		f = d.getField("DatePublished");
		if(f!=null)
			publication_date = f.stringValue();
		else publication_date = "";
		
		f = d.getField("City");
		if(f!=null)
			city = f.stringValue();
		else city = "";
		
		this.topic_id = topic_id;
		this.score = score;
	}
	
}

/**
 * Using lucene index of all patents, query sustainability related topic and return query hits 
 */
public class TopicSearcher {

	private IndexSearcher searcher = null;
	private QueryParser qparser = null;
	private IndexReader indexReader = null;
	
	public TopicSearcher(String indexpath) {		
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

	public IndexPatent[] search(Topic topic) {
		
		qparser.setAllowLeadingWildcard(true);				
		Query query;
		try {
			// search for topic in patent title field and return search results
			query = qparser.parse("Title:\""+topic.text+"\"");
			TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);
			System.out.println("Searching (" + query + ") resulted in (" + topDocs.totalHits + ") hits.....");
			if(topDocs.totalHits > 0) {			
				IndexPatent[] patents = new IndexPatent[topDocs.scoreDocs.length];
				 for(int i=0; i<topDocs.scoreDocs.length; i++)
					 patents[i] = new IndexPatent(topic.id, indexReader.document(topDocs.scoreDocs[i].doc),topDocs.scoreDocs[i].score);
				 
				 return patents;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
}
