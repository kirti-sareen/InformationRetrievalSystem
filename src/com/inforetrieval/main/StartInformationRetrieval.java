package com.inforetrieval.main;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.inforetrieval.constants.Constants;
import com.inforetrieval.manager.IndexingManager;

public class StartInformationRetrieval {
	public static void main(String[] args) throws Exception {

		String filePath = null;
		
		
		if(false) 
		{
			System.out.println(Constants.HELP_MESSAGE);
			return;
		}
		else{
			StringBuilder stringBuilder = new StringBuilder();
			for(String s : args) {
				stringBuilder.append(s);
			}
			filePath = stringBuilder.toString().trim();
			filePath="E://test_data";
			if(!Files.exists(Paths.get(filePath)))
			{
				System.out.println(Constants.PATH_NON_EXISTENT);
				System.out.println(Constants.HELP_MESSAGE);
				return;
			}
			else{
				System.out.println(Constants.WELCOME_MESSAGE);
				executeTask(filePath);
			}
		}
        
    }
	public static void executeTask(String filePath){
		Scanner scanner = new Scanner(System.in);
		do{
			System.out.println(Constants.MENU_OPTIONS);
        	String choice = "";
        		if(scanner.hasNext())
        			choice = scanner.next();
        	switch(choice){
        	case "1": {
        		System.out.println("Indexing your files...\n");
        		IndexingManager infoManager = new IndexingManager();
                infoManager.startIndexing(filePath);
                System.out.println("Indexing Completed.");
        		break;
        	}
        	case "2": {
        		System.out.println("Search for your query started...\n");
        		System.out.println("Please find search results below:\n");
        		break;
        	}
        	case "3": {
        		System.out.println("Exiting...\nThank You for using Information Retieval System !!!");
        		scanner.close();
        		System.exit(0);
        		break;
        	}
        	default:{
        		System.out.println("Sorry...This is not a valid choice.");
        	}
        		
        	}
        }while(true);
	}
}
