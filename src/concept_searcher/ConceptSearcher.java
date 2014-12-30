package concept_searcher;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import commons.Concept;
import commons.PREPROCESS_ENUM;

public class ConceptSearcher {

	static String patentsIndexPath = "";
	static String indexFields = "";
	static final String patentsIndexPattern = "--patents-index";
	static final String indexFieldsPattern = "--target-Index-fields";
	static String conceptsDBPath = "";
	static final String conceptsDBPathPattern = "--concepts-db";
	static String conceptsTableName = "";
	static final String conceptsTableNamePattern = "--concepts-table";
	static String targetTableName = "";
	static final String targetTableNamePattern = "--target-table";
	static String targetConceptFieldName = "";
	static final String targetConceptFieldNamePattern = "--target-concept-field";
	static String targetPatentFieldName = "";
	static final String targetPatentFieldNamePattern = "--target-patent-field";
	static String conceptsFieldName = "";
	static final String conceptsFieldNamePattern = "--concepts-field";
	static final String analyzerPattern = "--analyzer";
	static String analyzer = "org.apache.lucene.analysis.standard.StandardAnalyzer";
	
	static Vector<String> indexFieldsCol = null;
	
	static boolean parseArgs(String[] args) {
		boolean result = false;
		if(args.length>=14) {
			for(int i=0; i<args.length; i++) {
				if(args[i].compareTo(analyzerPattern)==0 && i+1<args.length)
					analyzer = args[++i];
				
				else if(args[i].compareTo(targetPatentFieldNamePattern)==0 && i+1<args.length)
					targetPatentFieldName = args[++i];
				
				else if(args[i].compareTo(targetConceptFieldNamePattern)==0 && i+1<args.length)
					targetConceptFieldName = args[++i];
				
				else if(args[i].compareTo(targetTableNamePattern)==0 && i+1<args.length)
					targetTableName = args[++i];
				
				else if(args[i].compareTo(indexFieldsPattern)==0 && i+1<args.length)
					indexFields = args[++i];
				
				else if(args[i].compareTo(conceptsFieldNamePattern)==0 && i+1<args.length)
					conceptsFieldName = args[++i];
				
				else if(args[i].compareTo(conceptsTableNamePattern)==0 && i+1<args.length)
					conceptsTableName = args[++i];
				
				else if(args[i].compareTo(patentsIndexPattern)==0 && i+1<args.length)
					patentsIndexPath = args[++i];
				
				else if(args[i].compareTo(conceptsDBPathPattern)==0 && i+1<args.length)
					conceptsDBPath = args[++i];
			}
			if(!indexFields.isEmpty() && 
					!patentsIndexPath.isEmpty() && 
					!conceptsDBPath.isEmpty() && 
					parseTargetFields())
				result = true;
		}
		if(result==false)
			System.out.println(printUsage());
		return result;
	}
	private static boolean parseTargetFields() {
		
		boolean result = false;
		
		// make sure tuples format is correct 
		Pattern p = Pattern.compile("\\{(\\w+,*)+\\}");
		Matcher m = p.matcher(indexFields);
		if(m.find()==true && m.group().compareTo(indexFields)==0) {
			indexFieldsCol = new Vector<String>();
			indexFields = indexFields.replaceAll("\\{|\\}","");
			String[] fields = indexFields.split(",");
			for(int i=0; i<fields.length; i++)
				indexFieldsCol.add(fields[i]);
			if(indexFieldsCol.size()>0)
				result = true;
		}
		
		return result;
	}
	static String printUsage() {
		String usage = "Usage: java -jar concept_searcher.jar " + patentsIndexPattern + " \"path\" " + conceptsDBPathPattern + " \"path\" " +  conceptsTableNamePattern + " \"concepts_table\"" +  conceptsFieldNamePattern + " \"concepts_field\"" + targetTableNamePattern +  " \"target_table\" " + targetPatentFieldNamePattern + " \"target_patent_field\" " + targetConceptFieldNamePattern + " \"target_concept_field\" " +  "[" + analyzerPattern + " lucene-analyzer-class]" + indexFieldsPattern + " {IndexField1,IndexField2,...}";
		return usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("Started at: "+new Date().toString());
		
		if(parseArgs(args)==true) {
			System.out.println("patents source index path: "+patentsIndexPath);
			System.out.println("patents target DB path: "+conceptsDBPath);
			System.out.println("concepts table name: "+conceptsTableName);
			System.out.println("concepts field name: "+conceptsFieldName);			
			System.out.println("target table name: "+targetTableName);
			System.out.println("target patent field name: "+targetPatentFieldName);
			System.out.println("target concept name: "+targetConceptFieldName);
			System.out.println("analyzer: "+analyzer);
			
			// open the source lucene index
			LuceneIndexSearcher searcher = new LuceneIndexSearcher(patentsIndexPath);
			try {
				searcher.init(analyzer);
				
				// load concepts
				Concepts concepts = new Concepts(conceptsDBPath, conceptsTableName, conceptsFieldName, 
						targetTableName, targetConceptFieldName, targetPatentFieldName);
				
				concepts.load();
				System.out.println("loaded ("+concepts.size()+") concepts");
				
				// lookup concepts
				for(Concept c : concepts) {
					ArrayList<Document> hits = searcher.search(c.text, indexFieldsCol.iterator());
					if(hits!=null)
						concepts.writeConceptHits(c.id, hits);
				}
				searcher.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
		System.out.println("Finished at: "+new Date().toString());
	}	
}
