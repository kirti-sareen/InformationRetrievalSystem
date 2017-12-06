package com.inforetrieval.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.inforetrieval.constants.Constants;
import com.inforetrieval.util.Utils;

/**
 * Handles indexing of files
 *
 */
public class IndexingManager {

	/**
	 * Start indexing of documents in the provided document path
	 * 
	 * @param folderPath
	 * @param indexPath
	 * @param rankingModel
	 */
	public void startIndexing(String folderPath, String indexPath, String rankingModel) {

		final Path docPath = Paths.get(folderPath);

		Path indexFilePath = Paths.get(indexPath);

		if (!Files.isReadable(docPath)) {
			System.out.println(Constants.UNABLE_TO_READ_FILE + docPath.toAbsolutePath());
			System.exit(0);
		}

		try {
			System.out.println(Constants.INDEXING_MSG + indexFilePath);

			Directory directory;

			/**
			 * EnglishAnalyzer implements PorterStemmer Algorithm using
			 * PorterStemFilter
			 */
			Analyzer analyzer = new EnglishAnalyzer();

			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			/*
			 * Creating index in directory
			 */
			if (Files.notExists(indexFilePath)) {
				// Create a new index in the directory, removing any previously
				// indexed documents:
				indexWriterConfig.setOpenMode(OpenMode.CREATE);
				directory = FSDirectory.open(indexFilePath);
			} else {
				// Add new documents to an existing index:
				indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
				directory = FSDirectory.open(indexFilePath);
			}
			Utils util = new Utils();
			indexWriterConfig.setSimilarity(util.getSimilarity(rankingModel));
			IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
			indexFiles(indexWriter, docPath);
			indexWriter.close();
			

			IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexFilePath));
			System.out.println("Total indexed file: " + indexReader.numDocs());
			indexReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Indexes the given file using the given writer, or if a directory is
	 * given, recurses over files and directories found under the given
	 * directory.
	 *
	 *
	 * @param writer
	 *            Creates/updates indexes during the indexing process.
	 * @param path
	 *            The path of files to be indexed.
	 * @throws IOException
	 */
	private void indexFiles(final IndexWriter indexWriter, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttr) throws IOException {
					try {
						if (Files.isReadable(filePath)) {
							indexFile(indexWriter, filePath, fileAttr.lastModifiedTime().toMillis());
						} else {
							System.out.println(Constants.UNABLE_TO_READ_FILE + filePath.toAbsolutePath());
						}
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			if (Files.isReadable(path)) {
				indexFile(indexWriter, path, Files.getLastModifiedTime(path).toMillis());
			} else {
				System.out.println(Constants.UNABLE_TO_READ_FILE + path.toAbsolutePath());
			}
		}
	}

	/** Index each document */
	private void indexFile(IndexWriter indexWriter, Path filePath, long lastModifiedDate) throws IOException {
		try (InputStream stream = Files.newInputStream(filePath)) {
			// document represents a virtual document with Fields
			Document document = new Document();

			// Field represents the key value pair relationship where a key is
			// used to identify the value to be indexed.

			// Create fields in the document.
			Field filePathData = new StringField(Constants.FIELD_PATH, filePath.toString(), Field.Store.YES);
			Field lastModifiedData = new StringField(Constants.FIELD_LAST_UPDATED, ((Long) lastModifiedDate).toString(),
					Field.Store.YES);
			Field content = new TextField(Constants.FIELD_CONTENT,
					new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));

			// Adding created fields to the document.
			document.add(filePathData);
			document.add(lastModifiedData);
			document.add(content);

			if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, adding new document:
				System.out.println(Constants.ADD_INDEX + filePath);
				indexWriter.addDocument(document);
			} else if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE_OR_APPEND) {
				System.out.println(Constants.UPDATE_INDEX + filePath);
				indexWriter.updateDocument(new Term(Constants.FIELD_PATH, filePath.toString()), document);// Delete
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getParsedDocs(Path indexPath)
	{
		try {
			IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
			Document tempDoc = null;
			for(int i=0;i<reader.numDocs();i++)
			{
				tempDoc = reader.document(i);
				System.out.println((i+1)+". "+tempDoc.get(Constants.FIELD_PATH));
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
