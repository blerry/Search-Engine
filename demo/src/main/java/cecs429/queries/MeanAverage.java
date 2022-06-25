package cecs429.queries;

import cecs429.documents.DocumentCorpus;
import cecs429.indexes.Index;
import cecs429.indexes.Indexer;//search

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * MeanAverage Class will use run Queries according to the relevance folder from the test collection
 */
public class MeanAverage {
    private static Indexer search = new Indexer();
    //grab path, the corpus of collection, disk, check boolean from old rank search function, and check to test throughput
    public static void runMAP(String path, DocumentCorpus corpus, Index index, Boolean isBooleanQuery, Boolean testThroughput) {
        ArrayList<String> queries = new ArrayList<>();//a list of all queries to be ran
        try {
            File fileQueries = new File(path + "/relevance/queries");//open the quieries to run
            Scanner scan = new Scanner(fileQueries);
            while (scan.hasNextLine()) {
                queries.add(scan.nextLine());//add each line as a query to queries
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }    
        ArrayList<int[]> relDocs = new ArrayList<>();//relevant documents in a list
        try {
            File relevantDocs = new File(path + "/relevance/qrel");//get them from here
            Scanner scan = new Scanner(relevantDocs); 
            while (scan.hasNextLine()) {//each line has doc ids relevant to the query line
                String data = scan.nextLine();
                String[] stringDocIds = data.split(" ");//split white space
                int[] intDocIds = new int[stringDocIds.length];//now add the numbers to an array
                for (int j = 0; j < intDocIds.length; j++) {
                    intDocIds[j] = Integer.parseInt(stringDocIds[j]);//dont forget to parse from String to Int
                }
                relDocs.add(intDocIds);
            }
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        double sumAvgPrecision = 0;//initialze
        for (int i = 0; i < queries.size(); i++) {//run the AP formula
            sumAvgPrecision += search.averagePrecision(corpus, index, queries.get(i), false, false, relDocs.get(i));
        }
        //Now we run the MAP forumla with that already calculated sum of AP
        double meanAvgPrecision = ((double)1/queries.size()) * sumAvgPrecision;
        System.out.println("MAP: " + meanAvgPrecision);
    }
}
