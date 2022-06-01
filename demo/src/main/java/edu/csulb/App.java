package edu.csulb;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.*;
import edu.csulb.PositionalInvertedIndexIndexer; 

import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

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
    private static PositionalInvertedIndex index = null;
    private static String dir = "";
    private static DocumentCorpus corpus = null;

    public static void main( String[] args )
    {
        Spark.staticFileLocation("public_html");
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
            //index = indexer.in
            double buildTime = 0.0;
            return "<div style=\"font-size: 12px; position:\">Files Indexed From: " + dir + " </br>Time to Index: " + buildTime +  " seconds</div></br>";
        });


        Spark.post("/squery", (request, response) -> {
            String squeryValue = request.queryParams("squeryValue");
            String stemmedTerm="";
            if (squeryValue.length() == 2 && squeryValue.substring(1, 2).equals("q")) {
                System.out.println("\nEnding program...");
                System.exit(-1);
                return "";
            } else if (squeryValue.length() >= 5 && squeryValue.substring(1, 5).equals("stem")) {
                //stemmedTerm = indexer.userSQueryStem(squeryValue);
                System.out.printf("%s stemmed to: %s", squeryValue.substring(6), stemmedTerm);
                System.out.println();
                return "</br><div style=\"font-size: 12px;\">"+ squeryValue + " stemmed to: " + stemmedTerm + "</div></br>";
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
