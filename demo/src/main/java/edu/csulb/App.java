package edu.csulb;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.DirectoryCorpus;
import cecs429.indexes.*;
import edu.csulb.PositionalInvertedIndexIndexer; 

import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

import static java.util.stream.Collectors.joining;


/**
 * Hello world!
 *
 */
public class App 
{
    private static PositionalInvertedIndexIndexer indexer = new PositionalInvertedIndexIndexer();
    private static Index index = null;
    private static String dir = "";
    private static DocumentCorpus corpus = null;

    public static void main( String[] args )
    {
        Spark.staticFileLocation("template");
        System.out.println("http://localhost:4567/");
        Spark.get("/", (req, res) -> {
            HashMap<String, Object> model =  new HashMap<>();
            return new ThymeleafTemplateEngine().render(new ModelAndView(model, "index"));
        });
        // creates thymeleaf template for search-window.html at /search
        Spark.get("/search", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();
            return new ThymeleafTemplateEngine().render(new ModelAndView(model, "search-window"));
        });
        // posts directory, builds index
        Spark.post("/", (request, response) -> {
            dir = request.queryParams("directoryValue");
            System.out.println(dir);
            corpus = DirectoryCorpus.loadJsonDirectory(Paths.get(dir).toAbsolutePath(), ".txt");
            long startTime = System.nanoTime();
            index = PositionalInvertedIndexIndexer.indexCorpus(corpus);
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;//Timer
            return "<div style=\"font-size: 12px; position:\">Files Indexed From: " + dir + " </br>Time to Index: " + totalTime / 1000000000 +  " seconds</div></br>";
        });
        // posts query values based on query inputs from client (outputs as html table)
        Spark.post("/search", (request, response) -> {

            String queryValue = request.queryParams("queryValue");
            return indexer.webSearch(queryValue, corpus, index);
        });
        // posts document contents as a div

        Spark.post("/document", (request, response) -> {
            String docid = request.queryParams("docValue");
            int id = Integer.parseInt(docid);
            //corpus is index request directory ;
            corpus.getDocuments(); //this line is needed or else corpus has mDocuments = null ???
            Document doc = corpus.getDocument(id);
            Reader reader = doc.getContent();
            StringBuilder content = new StringBuilder();
            int readerCharValue;
            try {
                while ((readerCharValue = reader.read()) != -1) {//read each char from the reader
                    content.append((char)readerCharValue);//convert the value to a char, add to builder
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return "</br><div style=\"\"> " + content.toString() + " </div></br>";
        });
        
        Spark.post("/squery", (request, response) -> {
            String squeryValue = request.queryParams("queryValue");
            String stemmedTerm="";
            if (squeryValue.length() == 2 && squeryValue.substring(1, 2).equals("q")) {
                System.out.println("\nEnding program...");
                System.exit(-1);
                return "";
            } else if (squeryValue.length() >= 5 && squeryValue.substring(1, 5).equals("stem")) {
                stemmedTerm = indexer.stemWord(squeryValue.substring(6));
                //squeryValue = squeryValue.substring(6);
                System.out.printf("%s stemmed to: %s", "", stemmedTerm);
                System.out.println();
                return "</br><div style=\"font-size: 12px;\">"+ squeryValue.substring(6) + " stemmed to: " + stemmedTerm + "</div></br>";
                //build a new index from the given directory
            } else if (squeryValue.length() >= 6 && squeryValue.substring(1, 6).equals("index")) {
                System.out.println("Resetting the directory...");//re-build an in-memory index
                dir = squeryValue.substring(7);
                double buildTime = 0.0; 
                return "<div style=\"font-size: 12px\">New Files Indexed From: " + dir + "</div> </br> <div style=\"font-size: 10px\">Time to Index:"+ buildTime +  " seconds</div>";
                //print the first 1000 terms in the vocabulary
            } else if (squeryValue.length() == 6 && squeryValue.substring(1, 6).equals("vocab")) {
                List<String> vocabList = index.getVocabulary();//gather vocab list from any index
                List<String> subVocab = null;
                if (vocabList.size() >= 1000) { subVocab = vocabList.subList(0, 999); }
                else { subVocab = vocabList.subList(0, vocabList.size() - 1); }
                String formattedVocabList = subVocab.stream().map(item -> "" + item + "</br>").collect(joining(" "));
                return "<b style=\"font-size: 15px;\"># of vocab terms: " + vocabList.size() + "</b></div></br>" + " </br> <div style=\"font-size: 12px;\">"+ formattedVocabList + "</br>";
            } else {
                return "<div style=\"font-size: 12px;\">Not Valid Special Query</div></br>";
            }
        });
    }
}
