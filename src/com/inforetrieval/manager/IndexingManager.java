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
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.inforetrieval.constants.Constants;

public class IndexingManager {
	public Path startIndexing(String folderPath){

	final Path docPath = Paths.get(folderPath);
		
		Path indexFilePath = null;
	
		indexFilePath = Paths.get(docPath.getParent().toString(),Constants.INDEX);
		
		if (!Files.isReadable(docPath)) {
			System.out.println("Unable to read file from the path: "+docPath.toAbsolutePath());
		}
		
		try {
			System.out.println("Indexing started for: '" + indexFilePath +"'");
	
			Directory directory = FSDirectory.open(indexFilePath);
	
			/**
			 * EnglishAnalyzer implements PorterStemmer Algorithm using PorterStemFilter
			 */
			Analyzer analyzer = new EnglishAnalyzer();	
	
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
			
			/*
			 * Creating index in directory
			 */
			
			// Create a new index in the directory, removing any previously indexed documents:
			indexWriterConfig.setOpenMode(OpenMode.CREATE);
	
			IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
			indexFiles(indexWriter, docPath);
	
			indexWriter.close();
	
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
				e.printStackTrace();
		}
		
		return indexFilePath;
	}
	/**
	* Indexes the given file using the given writer, or if a directory is given,
	* recurses over files and directories found under the given directory.
	*
	* NOTE: This method indexes one document per input file.  This is slow.  For good
	* throughput, put multiple documents into your input file(s).  An example of this is
	* in the benchmark module, which can create "line doc" files, one document per line,
	* using the
	* <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
	* >WriteLineDocTask</a>.
	*
	* @param writer Writer to the index where the given file/dir info will be stored
	* @param path The file to index, or the directory to recurse into to find files to index
	* @throws IOException If there is a low-level I/O error
	*/
	static void indexFiles(final IndexWriter indexWriter, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path filePath, BasicFileAttributes fileAttr) throws IOException {
					try {
						if(Files.isReadable(filePath)){
							indexFile(indexWriter, filePath, fileAttr.lastModifiedTime().toMillis());
						}
						else{
							System.out.println("Unable to read file from the path: "+filePath.toAbsolutePath());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} else {
			if(Files.isReadable(path)){
				indexFile(indexWriter, path, Files.getLastModifiedTime(path).toMillis());
			}
			else{
				System.out.println("Unable to read file from the path: "+path.toAbsolutePath());
			}
		}
		}
	
		/** Indexes each document */
		static void indexFile(IndexWriter indexWriter, Path filePath, long lastModifiedDate) throws IOException {
			try (InputStream stream = Files.newInputStream(filePath)) {
				// make a new, empty document
				Document document = new Document();
	
				// Add the path of the file as a field named "FilePath".  Use a
				// field that is indexed (i.e. searchable), but don't tokenize
				// the field into separate words and don't index term frequency
				// or positional information:
				Field filePathData = new StringField("FilePath", filePath.toString(), Field.Store.YES);
				document.add(filePathData);
	
				// Add the last modified date of the file a field named "LastModified".
				// Use a LongPoint that is indexed (i.e. efficiently filterable with
				// PointRangeQuery).This indexes to milli-second resolution, which
				// is often too fine.  You could instead create a number based on
				// year/month/day/hour/minutes/seconds, down the resolution you require.
				// For example the long value 2011021714 would mean
				// February 17, 2011, 2-3 PM.
	
				Field lastModifiedData = new StringField("LastModified", new Long(lastModifiedDate).toString(), Field.Store.YES);
				
				document.add(lastModifiedData);
	
				// Adding the contents of file to a field named "content".
				// Specify a Reader,so that the text of the file is tokenized and indexed, but not stored.
				
				//Adding file content by changing to UTF-8 encoding.
				//This enables searching for special characters.
				document.add(new TextField("Content", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
	
				if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
					// New index, adding new document:
					System.out.println("Indexing file:  " + filePath);
					indexWriter.addDocument(document);
				}
			}
		}//end of indexDoc

		/**
		 * @param path
		 * 
		 * This method is used to return list of parsed documents
		 */
		public void retrieveParsedDocs(Path path)
		{
			try {
				IndexReader indexReader = DirectoryReader.open(FSDirectory.open(path));
				Document document = null;
				if(indexReader.numDocs()>0){
					System.out.println("Please find below list of indexed files:");
				}
				for(int i=1;i<indexReader.numDocs()+1;i++)
				{
					document = indexReader.document(i-1);
					System.out.println((i)+") "+document.get("FilePath"));
				}
				indexReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
	
		}
}
