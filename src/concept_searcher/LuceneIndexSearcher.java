package concept_searcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

import commons.PREPROCESS_ENUM;
import commons.Patent;

public class LuceneIndexSearcher {

	String indexPath;
	IndexReader reader = null;
	IndexSearcher searcher = null;
	QueryParser parser = null;
	
	public LuceneIndexSearcher(String indexPath) {
		this.indexPath = indexPath;		
	}

	public void init(String analyzer) throws IOException {
		reader = DirectoryReader.open(FSDirectory.open(new File(indexPath)));
		searcher = new IndexSearcher(reader);
		Analyzer indexAnalyzer = null;
		try {
			indexAnalyzer = (Analyzer) Class.forName(analyzer).getConstructor(Version.class).newInstance(Version.LUCENE_46);
			parser = new QueryParser(Version.LUCENE_46, "text", indexAnalyzer);
			parser.setAllowLeadingWildcard(true);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() throws IOException {
		reader.close();
	}
	
	public ArrayList<Document> search(String query, Iterator<String> targetFields) {
		ArrayList<Document> docs = null;
		
		try {
			String queryTxt = "";
			while(targetFields.hasNext()) {
				if(queryTxt.isEmpty())
					queryTxt += targetFields.next() + ":\"" + query + "\"";
				else
					queryTxt += " OR " + targetFields.next() + ":\"" + query + "\"";				
			}
			if(queryTxt.isEmpty())
				queryTxt = query;
			
			TopDocs topDocs = searcher.search(parser.parse(queryTxt), Integer.MAX_VALUE);
			System.out.println("Searching (" + queryTxt + ") resulted in (" + topDocs.totalHits + ") hits.....");
			if(topDocs.totalHits > 0) {			
				 docs = new ArrayList<Document>();
				 for(int i=0; i<topDocs.scoreDocs.length; i++)
					 docs.add(reader.document(topDocs.scoreDocs[i].doc));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docs;		
	}
}
