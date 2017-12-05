package com.inforetrieval.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;

import com.inforetrieval.util.HTMLfileCheck;

public class SearchManager {
	/** Simple command-line based search demo. */
	public void initiateSearch(Path indexPath, String searchString, String rankingModel) throws Exception {

		String splChrs = "-/@#$%^&_+=()!{};.*,<>?':|";
		boolean found = searchString.matches("[" + splChrs + "]+");
		if (found) {
			System.out.println("Improper Search Query.");
			System.exit(0);
		}

		// String field = "contents";
		String field = "Content";
		String queries = null;
		int repeat = 0;
		String queryString = null;
		int hitsPerPage = 10;

		// IndexReader reader =
		// DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
		ClassicSimilarity classicSimilarity = new ClassicSimilarity();
		BM25Similarity bm25Similarity = new BM25Similarity();

		IndexSearcher searcher = new IndexSearcher(reader);
		if (rankingModel.equalsIgnoreCase("VS")) {
			searcher.setSimilarity(classicSimilarity);
		} else if (rankingModel.equalsIgnoreCase("OK")) {
			searcher.setSimilarity(bm25Similarity);
		}

		// English Analyzer used for both Indexing and Searching as it uses
		// Porter Stemmer
		Analyzer analyzer = new EnglishAnalyzer();

		QueryParser parser = new QueryParser(field, analyzer);

		Query query = parser.parse(searchString);
		searcher.search(query, 100);
		doPagingSearch(searcher, query, hitsPerPage);

		reader.close();
	}

	/**
	 * This demonstrates a typical paging search scenario, where the search
	 * engine presents pages of size n to the user. The user can then go to the
	 * next page if interested in the next hits.
	 *
	 * When the query is executed for the first time, then only enough results
	 * are collected to fill 5 result pages. If the user wants to page beyond
	 * this limit, then the query is executed another time and all hits are
	 * collected.
	 *
	 */
	public static void doPagingSearch(IndexSearcher searcher, Query query, int hitsPerPage) throws IOException {

		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		ScoreDoc[] hits = results.scoreDocs;

		int numTotalHits = (int) results.totalHits;
		System.out.println(numTotalHits + " total matching documents");
		hits = searcher.search(query, numTotalHits).scoreDocs;
		Document doc = new Document();
		 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for (ScoreDoc scoreDoc : hits) {
			int i = 1;
			doc = searcher.doc(scoreDoc.doc);
			String path = doc.get("FilePath");
			if (path != null) {
				System.out.println("Rank of the document is : " + i);
				System.out.println("Path of the document : " + doc.get("FilePath"));
				Date date = new Date(Long.parseLong(doc.get("LastModified")));
				System.out.println("Last Modification time:" + simpleDateFormat.format(date));
				System.out.println("Relevance score:" + scoreDoc.score);

				HTMLfileCheck.formatChecking(path);
			} else {
				System.out.println((i) + ". " + "No path for this document");
			}
			i++;

		}
	}
}
