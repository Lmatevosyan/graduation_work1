package com;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.rau.codeduplication.Automaton;
import com.rau.codeduplication.Duplication;

 
public class ListFiles 
{
	public static ArrayList <String> javaFilesPath = new ArrayList<String>();
	
	public static void ListOfFiles(String path, String n) throws IOException
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		String suffix = ".java";
		
		for(int i = 0; i < listOfFiles.length; ++i) {
				if(listOfFiles[i].isDirectory()) {
					ListOfFiles(listOfFiles[i].getAbsolutePath(), n);
				}
				if(listOfFiles[i].isFile()) {
					if(listOfFiles[i].getAbsolutePath().endsWith(suffix)) {
						javaFilesPath.add(listOfFiles[i].getAbsolutePath());
						System.out.println(listOfFiles[i].getAbsolutePath());
					}	
				}
		}
		try {
            System.out.println("Started work");
            Automaton automaton = new Automaton();
            for(int i = 0; i < javaFilesPath.size(); ++i) {
            	automaton.analyze(javaFilesPath.get(i));
            }
            Duplication duplication = new Duplication();
            for(int i = 0; i < javaFilesPath.size(); ++i) {
            	duplication.searchDuplication(javaFilesPath.get(i) + ".out", Integer.parseInt(n));
            }
            System.out.println("Ended work");
        }
        catch (Exception e) {
            System.out.println(String.format("Error: %s", e.getMessage()));
        }
	}
}

