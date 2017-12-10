package com.inforetrieval.constants;

/**
 * All Static Data Captured Here
 *
 */
public class Constants {

	// RANKING MODEL CONSTANTS
	public static final String RANKING_MODEL_VS = "VS";
	public static final String RANKING_MODEL_OK = "OK";

	// INDEXING CONSTANTS
	public static final String INDEX = "Index";
	public static final String INDEXING_STARTED_MSG = "Indexing your files...";
	public static final String INDEXING_MSG = "Indexing started for:\t";
	public static final String INDEXING_COMPLETED_MSG = "Indexing Completed.";
	public static final String ADD_INDEX = "Adding index file:\t";
	public static final String UPDATE_INDEX = "Updating index file:\t";

	public static final String FIELD_PATH = "FilePath";
	public static final String FIELD_LAST_UPDATED = "LastModified";
	public static final String FIELD_CONTENT = "Content";

	// SEARCHING CONSTANTS
	public static final String SEARCH_QUERY_MSG = "Searching for query:\t";
	public static final int MAX_SEARCH_RESULTS = 10;
	public static final String ALL_RESULTS = "Overall matching documents:\t";
	public static final String MOST_REL_MSG = "\tmost relevant documents:\t";
	public static final String DATE_FORMAT = "MM/dd/yyyy HH:mm:ss";

	public static final String DOC_RANK_MSG = "Document Rank:\t";
	public static final String DOC_PATH_MSG = "Document Path:\t";
	public static final String DOC_LAST_UPDATED_MSG = "Document Last Modified:\t";
	public static final String DOC_REL_SCORE_MSG = "Document Relevance score:\t";

	public static final String DOC_TITLE_MSG = "Document Title:\t";
	public static final String DOC_SUMMARY_MSG = "Document Summary:\t";
	public static final String DOC_NO_TITLE = "No Title Element present for the HTML document";
	public static final String DOC_NO_SUMMARY = "No Summary Element present for the HTML document";

	// WARNING MESSAGES
	public static final String HELP_MESSAGE = "You must enter all valid arguments.Please try again.";
	public static final String PATH_NON_EXISTENT = "The entered file path is not valid";
	public static final String UNABLE_TO_READ_FILE = "Unable to read file from the path:\t";
	public static final String IMPROPER_SEARCH_QUERY = "Improper Search Query.";
	public static final String NO_PATH = "No Path field available at searcch result:\t";

	// PROGRAM START AND END MESSGAGES
	public static final String WELCOME_MESSAGE = "====\tWelcome To Information Retieval System\t====";
	public static final String EXITING = "====\tEXECUTION COMPLETED.\t====";

	// SPECIAL CHARS
	public static final String SPL_CHARS = "-/@#$%^&_+=()!{};.*,<>?':|";
}
