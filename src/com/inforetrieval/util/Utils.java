package com.inforetrieval.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;

import com.inforetrieval.constants.Constants;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

/**
 * This class is used to create utility methods for this application
 *
 */
public class Utils {
	
	
	/**Checks whether the input ranking model is a valid one.
	 * @param rankingModel
	 * @return
	 */
	public boolean validRankingModel(String rankingModel){
		if(rankingModel.equalsIgnoreCase(Constants.RANKING_MODEL_VS) || rankingModel.equalsIgnoreCase(Constants.RANKING_MODEL_OK))
			return true;
		else
			return false;
	}
	
	
	/**Checks whether the file at the given path is a valid html
	 * @param path
	 * @return
	 */
	public boolean isHTMLFile(String path) {
		String fileType = null;
		int fileExtn = path.lastIndexOf('.');
		if (fileExtn > 0) {
			fileType = path.substring(fileExtn + 1);
		}
		if (fileType.equalsIgnoreCase("html") || fileType.equalsIgnoreCase("htm")) {
			return true;
		} else {
			return false;
		}
	}

	/**Reads the file from the provided path and generates Source object
	 * @param path
	 * @return Source
	 */
	public Source readHTMLFile(String path) {
		String contents = "";
		BufferedReader read;
		try {
			read = new BufferedReader(new FileReader(path));
			String data;
			while ((data = read.readLine()) != null)
				contents += data;
			read.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Source source = new Source(contents);//construct a Source object from the data in given file
		source.fullSequentialParse();//Parses all of the tags in this source document sequentially from beginning to end.
		return source;

	}
	
	/**Prints content in the Title Element of HTML file source
	 * @param source
	 */
	public void printTitle(Source source) 
	{
		Element title = source.getFirstElement(HTMLElementName.TITLE);	//checks for title tag
		
		if (title!=null) {
			System.out.println(Constants.DOC_TITLE_MSG+ title.getContent());
		}
		else {
			System.out.println(Constants.DOC_NO_TITLE);
		}
	}
	
	/**Prints content in the Summary Element of HTML file source
	 * @param source
	 */
	public void printSummary(Source source)
	{
		Element summary = source.getFirstElement(HTMLElementName.SUMMARY); //checks for summary tag
		
		if (summary!=null) {
		 System.out.println(Constants.DOC_SUMMARY_MSG+ summary.getContent());
		}
		else {
			System.out.println(Constants.DOC_NO_SUMMARY);
		}
	}
	
	/**Print an empty line
	 * 
	 */
	public void insertNewLine(){
		System.out.println("\n");
	}
	
	
	/**Return the similarity to be set for indexing and searching as per the user input
	 * @param rankingModel
	 * @return Similarity
	 */
	public Similarity getSimilarity(String rankingModel){
		Similarity classicSimilarity = new ClassicSimilarity();
		Similarity bm25Similarity = new BM25Similarity();
		if(rankingModel.equalsIgnoreCase(Constants.RANKING_MODEL_VS)){
			return classicSimilarity;
		}
		else if(rankingModel.equalsIgnoreCase(Constants.RANKING_MODEL_OK)){
			return bm25Similarity;
		}
		else{
			return classicSimilarity;
		}
		
	}

}
