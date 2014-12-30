package patent_indexer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;

import commons.PREPROCESS_ENUM;
import commons.Patent;

public class LuceneIndexer {

	String indexPath;
	IndexWriter writer = null;
	private PREPROCESS_ENUM e_preprocessStep = PREPROCESS_ENUM.PREPROCESS_NONE;
	
	public LuceneIndexer(String indexPath) {
		this.indexPath = indexPath;		
	}

	public void init(String analyzer, PREPROCESS_ENUM e_preprocess) throws IOException {
		Directory dir = FSDirectory.open(new File(indexPath));
		Analyzer indexAnalyzer = null;
		try {
			indexAnalyzer = (Analyzer) Class.forName(analyzer).getConstructor(Version.class).newInstance(Version.LUCENE_46);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Analyzer indexAnalyzer = new StandardAnalyzer(Version.LUCENE_46);
		IndexWriterConfig cfg = new IndexWriterConfig(Version.LUCENE_46, indexAnalyzer);
		cfg.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
		writer = new IndexWriter(dir, cfg);
		e_preprocessStep = e_preprocess;
	}

	public void index(DBPatents patents,
			HashMap<String, String> columnFieldDic) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, IOException {
		
		// as DB contains duplicate records, keep track of which patents added so don't add again
		HashSet<String> addedPatents = new HashSet<String>();
		for(int i=0; i<patents.size(); i++) {
			// get next patent
			Patent patent = patents.get(i);
			if(addedPatents.contains(patent.id)==false) {
				addedPatents.add(patent.id);
				
				// add new document with column/field mappings
				Document doc = new Document();
				System.out.printf("Adding patent: %s\n", patent.id);
				for(String column : columnFieldDic.keySet()) {
					String field = columnFieldDic.get(column);
					// todo: change "abstract" and "class" columns to other names e.g., "abstract_name" and "main_class"				
					if(column.compareTo("abstract")==0)
						column = "abstract_text";
					else if(column.compareTo("class")==0)
						column = "main_class";
					String data = (String)Patent.class.getDeclaredField(column).get(patent);
					if(e_preprocessStep==PREPROCESS_ENUM.PREPROCESS_STEM)
						data = stem(data);
					/* TODO: add lemmatization
					else if(e_preprocessStep==PREPROCESS_ENUM.PREPROCESS_LEMMATIZE)
						data = lemmatize(data);
					*/
					
					doc.add(new Field(field, data, Field.Store.YES, Field.Index.ANALYZED));
				}
				writer.addDocument(doc);
			}
		}		
	}

	private String stem(String data) {
		// TODO Auto-generated method stub
		PorterStemmer stemmer = new PorterStemmer();
		stemmer.setCurrent(data);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	/* TODO: add lemmatization
	private String lemmatize(String data) {
	
		// TODO Auto-generated method stub
		return data;
	}
    */
	
	public void commit() throws IOException {
		writer.close();		
	}
}
