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
        System.out.println("http://localhost:4000/");
        Spark.staticFileLocation("public_html");
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
    }
}
