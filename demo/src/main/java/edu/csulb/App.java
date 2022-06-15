package edu.csulb;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.documents.DirectoryCorpus;
import cecs429.indexes.*;

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
    private static Indexer indexer = new Indexer();
    private static Index index = null;
    private static String dir = "";
    private static DocumentCorpus corpus = null;
    private static DiskIndexWriter diskIndexWriter = new DiskIndexWriter();
    public static void main( String[] args )
    {
        Spark.port(4000); //http://localhost:4000/
        Spark.staticFileLocation("resources"); //folder for web files
        Spark.get("/", (req, res) -> { //path to http get request from
            HashMap<String, Object> model =  new HashMap<>(); //model for page
            return new ThymeleafTemplateEngine().render(new ModelAndView(model, "index"));//get index.html
        });
        // posting the directory from web to index
        Spark.post("/", (request, response) -> {//same / path
            dir = request.queryParams("directory"); //value from post
            System.out.println(dir); 
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            long startTime = System.nanoTime();
            index = Indexer.indexCorpus(corpus); //index the corpus with method
            long endTime = System.nanoTime(); 
            long totalTime = endTime - startTime;//Timer
            return "<div style=\"font-size: 12px; margin-left:25rem;\">Files Indexed From: " + dir + " </br> Time Indexed: " + totalTime / 1000000000 +  " seconds</div></br>";
        });
        Spark.post("/", (request, response) -> {//same / path
            dir = request.queryParams("directory"); //value from post
            System.out.println(dir); 
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            long startTime = System.nanoTime();
            index = Indexer.indexDiskCorpus(corpus,dir); //index the corpus with method
            long endTime = System.nanoTime(); 
            long totalTime = endTime - startTime;//Timer
            return "<div style=\"font-size: 12px; margin-left:25rem;\">Files Indexed From: " + dir + " </br> Time Indexed: " + totalTime / 1000000000 +  " seconds</div></br>";
        });
        //path /search to differ from / post path
        Spark.post("/search", (request, response) -> {
            String query = request.queryParams("query");//get query from web
            return indexer.webSearch(query, corpus, index,true); //do a web search this time with query from indexer
        });
        // post ranked query values based on query inputs from client (outputs as html table)

        Spark.post("/ranked-search", (request, response) -> {
            String query = request.queryParams("query");
            //String thestring = 
            return indexer.webSearch(query,corpus, index, false);
            //System.out.println(thestring);
            //return thestring;
        });
        // posts document contents as a div

        Spark.post("/document", (request, response) -> {
            String docid = request.queryParams("docId");//get doc id from web
            int id = Integer.parseInt(docid);
            //corpus is index request directory ;
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
            } else if (squery.length() >= 5 && squery.substring(1, 5).equals("stem")) {
                stemmedTerm = indexer.stemWord(squery.substring(6));
                //squeryValue = squeryValue.substring(6);
                System.out.printf("%s stemmed to: %s", "", stemmedTerm);
                System.out.println();
                return "</br><div style=\"font-size: 12px;\">"+ squery.substring(6) + " stemmed to: " + stemmedTerm + "</div></br>";
                //build a new index from the given directory
            } else if (squery.length() >= 6 && squery.substring(1, 6).equals("index")) {
                System.out.println("Resetting the directory...");//re-build an in-memory index
                dir = squery.substring(7);
                corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());
                long startTime = System.nanoTime();
                index = Indexer.indexCorpus(corpus);
                long endTime = System.nanoTime();
                long totalTime = endTime - startTime;//Timer
                return "<div style=\"color:white; font-size: 12px\">New Files Indexed From: " + dir + "</div> </br> <div style=\"font-size: 10px\">Time to Index:"+ totalTime +  " seconds</div>";
                //print the first 1000 terms in the vocabulary
            } else if (squery.length() == 6 && squery.substring(1, 6).equals("vocab")) {
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
    private static long timeToBuildIndex(String dir, boolean isDiskIndex) throws IOException {

        System.out.println("Starting to build index...");
        //measure how long it takes to build the index
        long startTime = System.nanoTime();

        if (isDiskIndex) {//create index from disk
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            index = indexer.buildDiskPositionalIndex(dir);//builds positional index 
        } else {//create in memory index
            corpus = DirectoryCorpus.loadTextDirectory(Paths.get(dir).toAbsolutePath());//load text corpus "files"
            index = Indexer.indexCorpus(corpus); //index the corpus with method
            diskIndexWriter.writeIndex(index, dir);//calls the writer of index to disk
        }

       // long stopTime = System.nanoTime();
        long endTime = System.nanoTime(); 
        long totalTime = endTime - startTime;//Timer
        //double indexSeconds = (double)(stopTime - startTime) / 1_000_000_000.0;
        System.out.println("Done!\n");
        System.out.println("Time to build index: " + totalTime/1000000000 + " seconds");
        
        return totalTime;

    }

}

