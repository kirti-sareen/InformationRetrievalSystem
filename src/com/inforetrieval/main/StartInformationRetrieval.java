package com.inforetrieval.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import com.inforetrieval.constants.Constants;
import com.inforetrieval.manager.IndexingManager;

public class StartInformationRetrieval {
	public static void main(String[] args) throws Exception {

		String filePath = args[0];
		String indexPath = args[1];
		String rankingModel = args[2];
		String query = args[3];
		
		if (args.length < 4) {
			System.out.println(Constants.HELP_MESSAGE);
			return;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				stringBuilder.append(args[i] + " ");
			}
			query = stringBuilder.toString();
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

	public static void executeTask(String filePath, String indexPath, String rankingModel, String query) {

		System.out.println("Indexing your files...\n");
		IndexingManager infoManager = new IndexingManager();
		Path path = infoManager.startIndexing(filePath,indexPath,rankingModel);
		System.out.println("Indexing Completed.");
		infoManager.retrieveParsedDocs(path);

	}
}
