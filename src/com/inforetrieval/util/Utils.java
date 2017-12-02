package com.inforetrieval.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
	String currentPath = "";
	
	public File createFile(String fileName) throws IOException{
		return new File(getPath(fileName));
	}
	public String getPath(String fileName) throws IOException{
		return Paths.get(getCurrentPath(),fileName).toString();
	}
	
	private String getCurrentPath() throws IOException{
		return new File(".").getCanonicalPath();
	}
}
