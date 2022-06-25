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
        ArrayList<int[]> relDocs = new ArrayList<>();
        try {
            File relevantDocs = new File(indexLocation + "/relevance/qrel");
            Scanner read = new Scanner(relevantDocs);
            int i = 0;
            while (read.hasNextLine()) {
                String data = read.nextLine();
                String[] stringDocIds = data.split(" ");
                int[] intDocIds = new int[stringDocIds.length];
                for (int j = 0; j < intDocIds.length; j++) {
                    intDocIds[j] = Integer.parseInt(stringDocIds[j]);
                }
                relDocs.add(intDocIds);
            }
            read.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        double sumAvgPrecision = 0;
        for (int i = 0; i < allQueries.size(); i++) {
            sumAvgPrecision += search.averagePrecision(corpus, index, allQueries.get(i), false, false, relDocs.get(i));
        }

        double meanAvgPrecision = ((double)1/allQueries.size()) * sumAvgPrecision;

        System.out.println("MAP: " + meanAvgPrecision);
    }
}
