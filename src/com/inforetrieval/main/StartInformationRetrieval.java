package com.inforetrieval.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.inforetrieval.manager.SearchManager;

import com.inforetrieval.constants.Constants;
import com.inforetrieval.manager.IndexingManager;

public class StartInformationRetrieval {
	public static void main(String[] args) throws Exception {


		
		if (args.length < 4 /*&& false*/) {
			System.out.println(Constants.HELP_MESSAGE);
			return;
		} else {
			String filePath = args[0].trim();
			String indexPath = args[1].trim();
			String rankingModel = args[2].trim();
			
			String query = args[3];
//			String filePath = "e://test_data";
//			String indexPath = "e//index";
//			String rankingModel = "OK";
//			String query = "kirti";
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				stringBuilder.append(args[i] + " ");
			}
			//query = stringBuilder.toString();
			query = query.trim();
			if (!Files.exists(Paths.get(filePath))) {
				System.out.println(Constants.PATH_NON_EXISTENT);
				System.out.println(Constants.HELP_MESSAGE);
				return;
			} else {
				System.out.println(Constants.WELCOME_MESSAGE);
				executeTask(filePath, indexPath, rankingModel, query);
			}
		}

	}

	public static void executeTask(String filePath, String indexPath, String rankingModel, String query) throws Exception {

		System.out.println("Indexing your files...\n");
		IndexingManager indexingManager = new IndexingManager();
		Path path = indexingManager.startIndexing(filePath,indexPath,rankingModel);
		System.out.println("Indexing Completed.\n");
		indexingManager.retrieveParsedDocs(path);
		System.out.println("\nSearching for query:\t"+query);
		SearchManager searchManager = new SearchManager();
		searchManager.initiateSearch(path,query,rankingModel);

	}
}
