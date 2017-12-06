package com.inforetrieval.manager;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import com.inforetrieval.constants.Constants;
import com.inforetrieval.util.Utils;
import net.htmlparser.jericho.Source;

/**
 * Handles searching of entered query.
 *
 */
public class SearchManager {
	static Utils util = new Utils();
	
	public void initiateSearch(Path indexPath, String searchString, String rankingModel) throws Exception {

		String splChrs = Constants.SPL_CHARS;
		boolean found = searchString.matches("[" + splChrs + "]+");
		if (found) {
			System.out.println(Constants.IMPROPER_SEARCH_QUERY);
			System.exit(0);
		}

		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexPath));
		
		IndexSearcher searcher = new IndexSearcher(indexReader);
		searcher.setSimilarity(util.getSimilarity(rankingModel));

		// English Analyzer used for both Indexing and Searching as it uses
		// Porter Stemmer
		Analyzer analyzer = new EnglishAnalyzer();
		QueryParser parser = new QueryParser(Constants.FIELD_CONTENT, analyzer);
		Query query = parser.parse(searchString);
		search(searcher, query);
		indexReader.close();
	}


	/**
	 * Makes a search using indexSearcher by passing the query to the searcher
	 * @param indexSearcher
	 * @param query
	 * @throws IOException
	 */
	private void search(IndexSearcher indexSearcher, Query query) throws IOException {

		TopDocs topDocs = indexSearcher.search(query,Constants.MAX_SEARCH_RESULTS); //TopDocs points to the top N search results which matches the search criteria.
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		System.out.println(Constants.ALL_RESULTS+ topDocs.totalHits);
		if(topDocs.totalHits > Constants.MAX_SEARCH_RESULTS)//show the top most n relevant results required 
		System.out.println(Constants.MAX_SEARCH_RESULTS +Constants.MOST_REL_MSG);
		Document doc = new Document();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		Source source = null;
		int i = 1;
		
		//print required information of the results
		for (ScoreDoc scoreDoc : scoreDocs) {
			doc = indexSearcher.doc(scoreDoc.doc);
			String path = doc.get(Constants.FIELD_PATH);
			if (path != null) {
				System.out.println(Constants.DOC_RANK_MSG + i);
				System.out.println(Constants.DOC_PATH_MSG + doc.get(Constants.FIELD_PATH));
				Date date = new Date(Long.parseLong(doc.get(Constants.FIELD_LAST_UPDATED)));
				System.out.println(Constants.DOC_LAST_UPDATED_MSG + simpleDateFormat.format(date));
				System.out.println(Constants.DOC_REL_SCORE_MSG + scoreDoc.score);
				
				if(util.isHTMLFile(path)){//check if the file is an html file
					source = util.readHTMLFile(path);
					if(source != null){//print the data from source for title and summary tags
						util.printTitle(source);
						util.printSummary(source);
					}
				}
			} else {
				System.out.println(Constants.NO_PATH+i);
			}
			util.insertNewLine();
			i++;
		}
	}
}
