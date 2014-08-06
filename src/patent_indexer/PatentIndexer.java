package patent_indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import commons.Patent;

/**
 * @author walid-shalaby
 *
 */
public class PatentIndexer {

	static String patentsSrcPath = "";
	static String colunmFieldTuples = "";
	static final String patentsSrcPattern = "--patents-src";
	static final String columnFieldTuplesPattern = "--DBcolunm-Indexfield-tuples";
	static String patentsIndxPath = "";
	static final String patentsIndxPattern = "--patents-index";
	
	static HashMap<String, String> columnFieldDic = null;
	
	static boolean parseArgs(String[] args) {
		boolean result = false;
		if(args.length==6) {
			for(int i=0; i<args.length; i++) {
				if(args[i].compareTo(columnFieldTuplesPattern)==0 && i+1<args.length)
					colunmFieldTuples = args[++i];
				
				if(args[i].compareTo(patentsSrcPattern)==0 && i+1<args.length)
					patentsSrcPath = args[++i];
				
				if(args[i].compareTo(patentsIndxPattern)==0 && i+1<args.length)
					patentsIndxPath = args[++i];
			}
			if(!colunmFieldTuples.isEmpty() && 
					!patentsSrcPath.isEmpty() && 
					parseColumnFieldTuples())
				result = true;
		}
		if(result==false)
			System.out.println(printUsage());
		return result;
	}
	private static boolean parseColumnFieldTuples() {
		
		boolean result = false;
		
		// make sure tuples format is correct 
		Pattern p = Pattern.compile("\\{(\\(\\w+:\\w+\\),*)+\\}");
		Matcher m = p.matcher(colunmFieldTuples);
		if(m.find()==true && m.group().compareTo(colunmFieldTuples)==0) {
			columnFieldDic = new HashMap<String,String>();
			colunmFieldTuples = colunmFieldTuples.replaceAll("\\{|\\(|\\)|\\}","");
			String[] tuples = colunmFieldTuples.split(",");
			for(int i=0; i<tuples.length; i++) {
				String[] tuple = tuples[i].split(":");
				columnFieldDic.put(tuple[0], tuple[1]);			
			}
			if(columnFieldDic.size()>0)
				result = true;
		}
		
		return result;
	}
	static String printUsage() {
		String usage = "Usage: java -jar patent_indexer.jar " + patentsSrcPattern + " \"path\" " + patentsIndxPattern + " \"path\" " + columnFieldTuplesPattern + " {(DBcolumn1:IndexField1),(DBcolumn2:IndexField2),...}";
		return usage;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(parseArgs(args)) {
			System.out.println("patents lookup path: "+patentsSrcPath);			
			System.out.println("patents index path: "+patentsIndxPath);
			
			// Initialize the index
			LuceneIndexer indexer = new LuceneIndexer(patentsIndxPath);
			try {
				indexer.init();
				
				// load patents from DB
				DBPatents patents = new DBPatents(patentsSrcPath);
				patents.load(columnFieldDic);
				
				// Dump columns to lucene index
				indexer.index(patents, columnFieldDic);
				indexer.commit();
				
			} catch (IOException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Done!");
		}
		
	}
}
