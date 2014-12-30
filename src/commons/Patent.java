package commons;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

/**
 * @author wshalaby
 *
 */

/**
 * A placeholder for patent in index/DB matching a sustainability topic 
 */
public class Patent {
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
	

	/*
	 *  initialize patent from DB record
	 */
	public Patent(final ResultSet rs, int topic_id) {
		this(rs, topic_id,0.0f);
	}
	/* 
	 * initialize patent from DB record
	 */
	public Patent(final ResultSet rs, int topic_id, float score) {
		String temp;
		try {
			temp = rs.getString("id");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		id = temp;
		
		try {
			temp = rs.getString("title");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		title = temp;
			
		try {
			temp = rs.getString("abstract");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		abstract_text = temp.replaceAll("\\x1f", "");
			
		try {
			temp = rs.getString("description");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		description = temp.replaceAll("\\x1f", "");
			
		try {
			temp = rs.getString("claims");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		claims = temp.replaceAll("\\x1e|\\x1d", "");
		
		try {
			temp = rs.getString("uspc");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			temp = "";
			e.printStackTrace();
		}
		// parse uspc classification
		if(temp.length()==0)
			main_class = subclass = further_classification = "";
		else {
			String[] codes = temp.split("\\x1f");
			int index = codes[0].indexOf("/");
			if(index!=-1) {
				main_class = codes[0].substring(0,codes[0].indexOf("/"));
				subclass = codes[0].substring(codes[0].indexOf("/")+1,codes[0].length());				
			}
			else {
				main_class = codes[0];
				subclass = "";
			}
			temp = "";
			if(codes.length>1) {
				temp = codes[1];
				for(int i=2; i<codes.length; i++)
					temp = temp + "," + codes[i];
			}
			
			further_classification = temp;				
		}
		
		main_classification = "";
			
		claims_statement = "";
			
		kind = "";
		
		type = "";
		
		state = "";
		
		assignee = "";
				
		publication_date = "";
		
		city = "";
		
		this.topic_id = topic_id;
		this.score = score;
	}
	/*
	 * Initialize patent from DB record
	 */
	public Patent(final ResultSet rs) {
		
		id = getColumn(rs, "id");	
		title = getColumn(rs, "title");
		abstract_text = getColumn(rs, "abstract");
		description = getColumn(rs, "description");
		claims = getColumn(rs, "claims");
		main_classification = getColumn(rs, "main_classification");
		main_class = getColumn(rs, "class");
		subclass = getColumn(rs, "subclass");
		further_classification = getColumn(rs, "further_classification");
		kind = getColumn(rs, "kind");
		type = getColumn(rs, "type");
		state = getColumn(rs, "state");
		assignee = getColumn(rs, "assignee");
		publication_date = getColumn(rs, "publication_date");
		claims_statement = getColumn(rs, "claims_statement");			
		city = getColumn(rs, "city");
		topic_id = -1;
		score = 0;
	}
	private String getColumn(ResultSet rs, String name) {
		
		String result = "";
		try {
			result = rs.getString(name);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	/*
	 *  initialize patent from lucene index document
	 */
	public Patent(int topic_id, Document d, float score) {
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
