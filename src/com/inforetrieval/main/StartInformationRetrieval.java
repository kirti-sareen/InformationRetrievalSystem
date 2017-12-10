package com.inforetrieval.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.inforetrieval.manager.SearchManager;
import com.inforetrieval.util.Utils;
import com.inforetrieval.constants.Constants;
import com.inforetrieval.manager.IndexingManager;

public class StartInformationRetrieval {
	static Utils util = new Utils();

	public static void main(String[] args) throws Exception {

		// make sure all required parameters were entered
		if (args.length < 4) {
			System.out.println(Constants.HELP_MESSAGE);
			return;
		} else {

			// start setting data recieved from command line
			String filePath = args[0].trim();
			String indexPath = args[1].trim();
			String rankingModel = args[2].trim();

			String query = null;

			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 3; i < args.length; i++) {
				stringBuilder.append(args[i] + " ");
			}
			query = stringBuilder.toString().trim();
			// end setting data recieved from command line

			// check whether the path to document folder is valid
			if (!Files.exists(Paths.get(filePath))) {
				System.out.println(Constants.PATH_NON_EXISTENT);
				System.out.println(Constants.HELP_MESSAGE);
				System.exit(0); // Prompt the user to re-run if wrong path was
								// entered and exit.
			}
			if (!util.validRankingModel(rankingModel)) {
				System.out.println("Invalid ranking model");
			} else {
				System.out.println(Constants.WELCOME_MESSAGE);// exeute program
																// if valid
																// inputs were
																// recieved
				executeTask(filePath, indexPath, rankingModel, query);
			}
		}

	}

	/**
	 * Start execution of indexing files and searching given query based on the
	 * ranking model provided.
	 * 
	 * @param filePath
	 * @param indexPath
	 * @param rankingModel
	 * @param query
	 * @throws Exception
	 */
	private static void executeTask(String filePath, String indexPath, String rankingModel, String query)
			throws Exception {

		System.out.println(Constants.INDEXING_STARTED_MSG);
		util.insertNewLine();
		IndexingManager indexingManager = new IndexingManager();
		indexingManager.startIndexing(filePath, indexPath, rankingModel);
		System.out.println(Constants.INDEXING_COMPLETED_MSG);
		util.insertNewLine();
		Path path = Paths.get(indexPath);
		// IndexingManager.getParsedDocs(path);

		System.out.println(Constants.SEARCH_QUERY_MSG + query);
		SearchManager searchManager = new SearchManager();
		searchManager.initiateSearch(path, query, rankingModel);
		util.insertNewLine();
		System.out.println(Constants.EXITING);

	}
}
