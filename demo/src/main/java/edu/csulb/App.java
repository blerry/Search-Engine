package edu.csulb;

import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import modules.documents.DirectoryCorpus;
import modules.documents.Document;
import modules.documents.DocumentCorpus;
import modules.indexes.DiskIndexWriter;
import modules.indexes.DiskPositionalIndex;
import modules.indexes.Indexer;
import modules.queries.CalculatePrecision;

import java.io.IOException;
import java.io.Reader;


import static java.util.stream.Collectors.joining;


/**
 * Server
 *
 */
public class App 
{
    private static Indexer indexer = new Indexer();
    //private static Index index = null;
    private static DiskPositionalIndex index = null;
    private static String dir = "";
    private static DocumentCorpus corpus = null;
    private static DiskIndexWriter diskIndexWriter = new DiskIndexWriter();
    public static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
    public static void main( String[] args )
    {
        //Spark.port(4000); //http://localhost:4000
        Spark.staticFileLocation("resources"); //folder for web files
        Spark.port(getHerokuAssignedPort());
        
        //Spark.get("/",(req,res)->{return ;});
        Spark.get("/", (req, res) -> { //path to http get request from
            HashMap<String, Object> model =  new HashMap<>(); //model for page
            return new ThymeleafTemplateEngine().render(new ModelAndView(model, "index"));//get index.html
        });

        Spark.post("/", (request, response) -> {//same / path
            dir = request.queryParams("directory"); //value from post
            System.out.println(dir); 
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            long startTime = System.nanoTime();
            //index = Indexer.indexDiskCorpus(corpus,dir); //index the corpus with method
            corpus.getDocuments();
            index = new DiskPositionalIndex(dir);
            long endTime = System.nanoTime(); 
            long totalTime = endTime - startTime;//Timer
            return "<div style=\"font-size: 12px; margin-left:25rem;\">Files Indexed From: " + dir + " </br> Time Indexed: " + totalTime / 1000000000 +  " seconds</div></br>";
        });
        Spark.post("/build", (request, response) -> {//same / path
            dir = request.queryParams("directory"); //value from post
            System.out.println(dir); 
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            long startTime = System.nanoTime();
            Indexer.buildIndex(corpus,dir); //index the corpus with method
            long endTime = System.nanoTime(); 
            long totalTime = endTime - startTime;//Timer
            return "<div style=\"font-size: 12px; margin-left:25rem;\">1. Put the same path in input field. 2. Press Query this Time</div></br>";
        });
        //path /search to differ from / post path
        Spark.post("/search", (request, response) -> {
            String query = request.queryParams("query");//get query from web
            return indexer.webSearch(query, corpus, index,true,false); //do a web search this time with query from indexer
        });
        // post ranked query values based on query inputs from client (outputs as html table)

        Spark.post("/ranked-search", (request, response) -> {
            String query = request.queryParams("query");
            //String thestring = 
            return indexer.webSearch(query,corpus, index, false,false);
            //System.out.println(thestring);
            //return thestring;
        });
        // posts document contents as a div
        Spark.post("/ranked-search-test", (request, response) -> {
            String query = request.queryParams("query");
            indexer.setQueryTime(0.0);
            indexer.webSearch(query, corpus, index, false, true);
            double time = indexer.getQueryTime();
            int testIterations = indexer.getTEST_ITERATIONS();
            double meanResponseTime = time/testIterations;
            double throughput = 1/meanResponseTime;
            return "<div style=\"font-size: 36px;\">Total Time: "+ testIterations+ " iterations: " + time + " seconds</div>" +
                    "<div style=\"font-size: 36px;\">Mean Response Time: " + meanResponseTime + " seconds</div>" +
                    "<div style=\"font-size: 36px;\">Throughput: " + throughput + " queries/second</div>" +
                    "<br>";
        });
        Spark.post("/document", (request, response) -> {
            String docid = request.queryParams("docId");//get doc id from web
            int id = Integer.parseInt(docid);
            //corpus is index request directory
            corpus.getDocuments(); //this line is needed or else corpus has mDocuments = null ???
            Document doc = corpus.getDocument(id);
            Reader reader = doc.getContent();//get content
            StringBuilder content = new StringBuilder();
            int readerCharValue;
            try {
                while ((readerCharValue = reader.read()) != -1) {//read each char from the reader
                    content.append((char)readerCharValue);//convert the value to a char, add to builder
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return "</br><div style=margin:10px 15px;\"\"> " + content.toString() + " </div></br>";
        });
        
        //similar functionality to query except for special queries
        Spark.post("/squery", (request, response) -> {
            String squery = request.queryParams("query");
            String stemmedTerm="";
            if (squery.length() == 2 && squery.substring(1, 2).equals("q")) {
                System.out.println("\nEnding program...");
                System.exit(-1);
                return "";
            }
             else if (squery.length() >= 5 && squery.substring(0, 5).equals(":test")) {
                ArrayList<String> queries= CalculatePrecision.getQueries(dir);
                for(String q: queries){
                    q = "<br>"+q+"</br>";
                }
                double meanAvgPrecision = CalculatePrecision.meanAveragePrecision(dir, corpus, index);
                return "</br><div style=\"font-size: 12px;\">Running test queries</div></br>"+
                "<div style=\"font-size: 12px;\">"+queries+"</div>"+
                        "</br><div style=\"font-size: 36px;\">"+"Mean Average Precision  "+meanAvgPrecision+"</div></br>";
             }
            else if (squery.length() >= 5 && squery.substring(0, 5).equals(":stem")) {
                stemmedTerm = indexer.stemWord(squery.substring(5));
                System.out.printf("%s stemmed to: %s", "", stemmedTerm);
                System.out.println();
                return "</br><div style=\"font-size: 12px;\">"+ squery.substring(6) + " stemmed to: " + stemmedTerm + "</div></br>";
                //build a new index from the given directory
            } else if (squery.length() >= 6 && squery.substring(1, 6).equals(":index")) {
                System.out.println("Resetting the directory...");//re-build an in-memory index
                dir = squery.substring(7);
                corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());
                long startTime = System.nanoTime();
                //index = DiskPositionalIndex.writeIndex();
                long endTime = System.nanoTime();
                long totalTime = endTime - startTime;//Timer
                return "<div style=\"color:white; font-size: 12px\">New Files Indexed From: " + dir + "</div> </br> <div style=\"font-size: 10px\">Time to Index:"+ totalTime +  " seconds</div>";
                //print the first 1000 terms in the vocabulary
            } else if (squery.length() == 6 && squery.substring(0, 6).equals(":vocab")) {
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

