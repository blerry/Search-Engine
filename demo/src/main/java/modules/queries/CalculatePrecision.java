package modules.queries;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

import modules.documents.DocumentCorpus;
import modules.indexes.Index;
import modules.indexes.Indexer;
/**
 * CalculatePrecision Class will use run Queries according to the relevance folder from the test collection rank mode
 * @runMAP
 * Will get the Mean Average Precision
 * @averagePrecision
 * Will collect average Precision
 */
public class CalculatePrecision {
    private static double queryTime = 0.0; //timing purposes
    //This calculates average precision
    public static double averagePrecision(DocumentCorpus corpus, Index index, String query, int[] relDocs) {
        double relevantSum = 0;
        int relevantIndex = 0;
        int totalRelevantDocs = 0;
        double queryRuntime;
        long startTime = System.nanoTime();
        System.out.println("Loading Query...");//calculate how long it takes to execute
        PriorityQueue<Accumulator> pq = Indexer.rankedSearch(corpus, index, query);
        System.out.println("Query: " + query.substring(0, query.length()-2));
        while(!pq.isEmpty()){
            Accumulator currAcc = pq.poll();
            relevantIndex++;//increase @i
            int docId = currAcc.getDocId() + 1;
            for (int i = 0; i < relDocs.length; i++) {
                if (relDocs[i] == docId) {//match
                    System.out.print(docId + ", ");
                    totalRelevantDocs++;
                    relevantSum += ((double) totalRelevantDocs / relevantIndex);
                    //System.out.println("Relevant Sum" + relevantSum +"="+totalRelevantDocs+"/"+relevantIndex);
                    break;//loop
                }
            }
        }
        double avgPrecision = ((double)1/relDocs.length) * relevantSum;
        System.out.println();
        long stopTime = System.nanoTime();
        queryRuntime = (double)(stopTime - startTime) / 1_000_000_000.0;
        queryTime = queryTime+queryRuntime;
        System.out.println("Query Time: " + queryRuntime + " seconds");
        System.out.println("Average Precision: " + avgPrecision + "\n");
        return avgPrecision;
    }
    //grab path, the corpus of collection, disk, check boolean from old rank search function, and check to test throughput
    public static double meanAveragePrecision(String path, DocumentCorpus corpus, Index index) {
        ArrayList<String> queries = getQueries(path);//a list of all queries to be ran
          
        ArrayList<int[]> relDocs = getRelevantDocs(path);//relevant documents in a list
        double sumAvgPrecision = 0;//initialze
        for (int i = 0; i < queries.size(); i++) {//run the AP formula
            sumAvgPrecision += averagePrecision(corpus, index, queries.get(i), relDocs.get(i));
        }
        //Now we run the MAP forumla with that already calculated sum of AP
        double meanAvgPrecision = ((double)1/queries.size()) * sumAvgPrecision;
        System.out.println("MAP: " + meanAvgPrecision);
        return meanAvgPrecision;
    }
    public static ArrayList<String> getQueries(String path){
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
        return queries;
    }
    private static ArrayList<int[]> getRelevantDocs(String path){
        ArrayList<int[]> relDocs=new ArrayList<>();
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
        return relDocs;
    }
}
