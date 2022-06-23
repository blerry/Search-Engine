package edu.csulb;

import cecs429.documents.DocumentCorpus;
import cecs429.indexes.Index;
import cecs429.indexes.Indexer;//search

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InExactRetrieval {
    private static Indexer search = new Indexer();

    public static void runQueries(String indexLocation, DocumentCorpus corpus, Index index, Boolean isBooleanQuery, Boolean testThroughput) {
        ArrayList<String> allQueries = new ArrayList<>();
        try {
            File queries = new File(indexLocation + "/relevance/queries");
            Scanner read = new Scanner(queries);
            while (read.hasNextLine()) {
                allQueries.add(read.nextLine());
            }
            read.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }    
    }
}
