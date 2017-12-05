package com.inforetrieval.util;
/*
 * Here we are checking for HTML type extension to the file so that HTML Title and Summary can be displayed
 * This uses Jericho to identify the whether it is a HTML file or not
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class HTMLfileCheck {

	public static void formatChecking(String path) throws IOException 	
	{
		/*
		 * Checks whether extension is ".html"
		 */
		try {
			String fileextension = "";
			int htmlcheck = path.lastIndexOf('.');
	    if (htmlcheck > 0) {
	        fileextension = path.substring(htmlcheck+1);
	    }

	    if(fileextension.equalsIgnoreCase("html")) {
	    	String contents = "";
	    	BufferedReader read = new BufferedReader(new FileReader(path));
	    	String data;
	    	while ((data = read.readLine()) != null) 
	    		contents +=data;
	    		read.close();
	    		Source source = new Source(contents);
	    		source.fullSequentialParse();
	    
	    		HTMLfileCheck.getTitle(source);
	    		HTMLfileCheck.getSummary(source);     
	    	} 
		}
	    	catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public static void getTitle(Source source) 
	{
		Element title=source.getFirstElement(HTMLElementName.TITLE);	//checks for title tag
		
		if (title!=null) {
			System.out.println("Document title : "+ title.getContent());
		}
		else {
			System.out.println("No title present for the document");
		}
	}

	public static void getSummary(Source source)
	{
		Element summary=source.getFirstElement(HTMLElementName.SUMMARY);
		
		if (summary!=null) {
		 System.out.println("Document Summary : "+ summary.getContent());
		}
		else {
			System.out.println("No summary present for the document");
		}
	}
}
