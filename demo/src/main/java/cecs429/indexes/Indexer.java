package cecs429.indexes;

import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.queries.Accumulator;
import cecs429.queries.BooleanQueryParser;
import cecs429.queries.TermLiteral;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
/**
 * Indexer class will create an index for disk or memory
 * @indexCorpus
 * used to index a corpus in memory
 * @indexDiskCorpus
 * used to index a corpus for disk
 * 
 * Indexer can also can search with given string queries
 * @rankedSearch
 * Use ranked retrieval with accumulators for scoring
 * @boolSearch
 * Use boolean retrieval with boolean query parsing
 */
public class Indexer {
    private static final int RANKED_RETURN = 10;//change later
        //change for inexact later, might stay on separate maybe
        public String webSearch(String query,DocumentCorpus corpus, Index index, Boolean isBooleanQuery, Boolean throughput){
            StringBuilder postingsRows = new StringBuilder();
            String result = "";
            System.out.println("Starting Query...");
            String[] terms = query.split(" ");
            int docCount = 0;
            if (isBooleanQuery) {//process a boolean query
                List<Posting> postings = boolSearch(query, corpus, index);//Run Boolean Search
                for(String term:terms){      
            for (Posting post : postings) {//include document titles for each returned posting
                    ArrayList<Integer> positions = new ArrayList<>();
                    String title = corpus.getDocument(post.getDocumentId()).getTitle();
                    String row = "    <tr>\n" +
                                "        <td>"+post.getDocumentId()+"</td>\n" +
                                "        <td><button id=\"" + post.getDocumentId() + "\" onClick=\"docClicked(this.id)\" >"+title+"</button></td>\n" +
                                "        <td>"+post.getPostions()+"</td>\n" +
                                "    </tr>\n";
                        postingsRows.append(row);
                        docCount++;
                    }
                }
                result = "<div><b>Query: </b>" + query +
                        "<div>Total Documents: " + postings.size() + "</div></div></br>" +
                        "<table style=\"width:100%\">\n" +
                        "    <tr>\n" +
                        "        <th>Document ID</th>\n" +
                        "        <th>Document Title</th>\n" +
                        "        <th>Positions</th>\n" + 
                        "    </tr>\n" +
                        postingsRows.toString() +
                        "</table>";
                return result;
                }else{//Ranked Query
                    PriorityQueue<Accumulator> pq;
                    pq = rankedSearch(corpus, index, query);
                    int pqSize = pq.size();
                while(!pq.isEmpty()){
                    Accumulator currAcc = pq.poll();
                    String title = corpus.getDocument(currAcc.getDocId()).getTitle();
                    int docId = currAcc.getDocId() + 1;
                    docId--;
                    double value = currAcc.getA_d();
                    System.out.println("Value" + value);
                    System.out.println(value);
                    String row = "    <tr>\n" +
                            "        <td>"+docId+"</td>\n" +
                            "        <td><button id=\"" + docId + "\" onClick=\"docClicked(this.id)\" >"+title+"</button></td>\n" +
                            "        <td>"+value+"</td>\n" +
                            "    </tr>\n";
                    postingsRows.insert(0,row);
                }
                result = "<div><b>Top " + RANKED_RETURN + " Results for: </b>" + query +
                        "<div>Total Documents: " + pqSize + "</div></div></br>" +
                        "<table style=\"width:100%\">\n" +
                        "    <tr>\n" +
                        "        <th>Document Id</th>\n" +
                        "        <th>Document Title</th>\n" +
                        "        <th>Score</th>\n" +
                        "    </tr>\n" +
                        postingsRows.toString() +
                        "</table>";
                    }

                    long stopTime = System.nanoTime();
                    queryRuntime = (double)(stopTime - startTime) / 1_000_000_000.0;
                    setQueryTime(queryTime + queryRuntime);
                    System.out.println("Query Time: " + queryRuntime + " seconds\n");
                }
                    return result;  
        }
        //Boolean search

        public static List<Posting> boolSearch(String queryi,DocumentCorpus corpus, Index index){
            List<Posting> postings = new ArrayList<>();
            BooleanQueryParser query = new BooleanQueryParser();  
            postings = query.parseQuery(queryi).getPostingsPositions(index);
        corpus.getDocuments();//corpus doesn't exist if we don't include this line. (I have no idea)
        //print each document associated with the query
        for (Posting posting : postings) {
            System.out.printf("Document ID: %-9s Title: %s", posting.getDocumentId(),
                    corpus.getDocument(posting.getDocumentId()).getTitle());
            System.out.println("");
            System.out.println("Positions: "+ posting.getPostions());
        }
        System.out.println("\nTotal Documents: " + postings.size());//print total documents found
        return postings;
            } 
        //ranked query

        public static PriorityQueue<Accumulator> rankedSearch(DocumentCorpus corpus, Index index, String queryInput) {
        //System.out.println("RUNS");
        double n = corpus.getCorpusSize();
        List<TermLiteral> termLiterals = new ArrayList<TermLiteral>();
        int counter = 0;
        List<Posting> postings = new ArrayList<Posting>();
        HashMap<Posting, Double> hm = new HashMap<>();
        PriorityQueue<Accumulator> pq = new PriorityQueue<>(RANKED_RETURN);
        String stemmedTerm = "";
        String[] terms = query.split(" ");
        for (String term : terms) { // for each term in query
            term = term.toLowerCase();
            stemmedTerm = AdvancedTokenProcessor.stemToken(term);
            termLiterals.add(new TermLiteral(stemmedTerm));
            int df_t = index.getDocumentFrequencyOfTerm(stemmedTerm);
            double w_qt;
            if(df_t == -1){
                w_qt = 1;
            }else{
             w_qt = Math.log(1.0 + (n/((double)df_t)));
              }  // calcul;ate wqt = ln(1 + N/dft)
            //System.out.println("w_qt = "+w_qt+" n: " + n + "/ "+ df_t);
;           //not as accurate, but saves us from thousands of disk reads
                    postings = termLiterals.get(counter).getPostings(index);
                    counter++;
                    //System.out.print("tf "+((double) index.getTermFrequency(stemmedTerm)) +"/" +"posting size "+ ((double) postings.size()));
                    //System.out.println("tf_td" +tf_td);
                    for(Posting p : postings){ // for each document in postings list
                        //double w_dt = 1.0 + Math.log(tf_td);
                        double w_dt = p.getWDT();
                        //System.out.println("WDT: " +w_dt);
                        double a_d = (w_dt * w_qt);
                        //System.out.println("Ad = " + a_d +"Wdt " + w_dt+ " x "+ " Wqt " + w_qt );
                            if (hm.get(p) != null) {
                                hm.put(p, hm.get(p) + a_d);
                            } else {
                                hm.put(p, a_d);
                            }
                        }
                    }
            List<Accumulator> accumulators = new ArrayList<Accumulator>();
            hm.forEach((key,value) -> 
                                        //{if(!accumulators.contains(accumulators))){
                                        accumulators.add(new Accumulator(key.getDocumentId(),value)));
                                        //});
            for (Accumulator acc : accumulators){
                // only retain the a certain amount of the top k results
                double value = acc.getA_d() / index.getDocumentWeight(acc.getDocId());
                //System.out.println("Score = " +value+ " Ad " + acc.getA_d() + "/" +" Ld "+index.getDocumentWeight(acc.getDocId() ));
                acc.setA_d(value);
                if(pq.size() < RANKED_RETURN || pq.peek().getA_d() < acc.getA_d()){
                    if(pq.size() == RANKED_RETURN){
                        pq.remove();
                    }
                    pq.add(acc);
                }
            }
        return pq;
        }       

        /**
         * Makes a Positial Inverted Index for a disk index as we return that index
         * @param corpus
         * @param indexLocation
         * @return
         * @throws IOException
         */

        
        public static Index indexDiskCorpus(DocumentCorpus corpus,String indexLocation) throws IOException {
            PositionalInvertedIndex index = new PositionalInvertedIndex();//create positional index
            AdvancedTokenProcessor processor = new AdvancedTokenProcessor();//create token processor
            DiskIndexWriter diskIndexWriter = new DiskIndexWriter();//used to write to index
            ArrayList<Double> documentWeight = new ArrayList<>();//used for doc weights
            Iterable<Document> documents = corpus.getDocuments(); //get documents in corpus to loop through
            for (Document docs : documents) {//iterate through every valid document found in the corpus
                int totalTerms = 0;
                HashMap<String, Integer> termFrequency = new HashMap<>();//map term frequency of terms in a document
                EnglishTokenStream stream = new EnglishTokenStream(docs.getContent());//tokenize with English token stream
                Iterable<String> tokens = stream.getTokens();//convert stream to tokens
                int wordPosition = 1;//maintain the position of the word throughout the document
                // Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
                for (String token : tokens) {
                    List<String> words = processor.processToken(token);//token will be indexable
                    for (int i = 0; i < words.size(); i++) {//get unstemmed tokens
                        words.set(i, AdvancedTokenProcessor.stemToken(words.get(i)));
                        if (termFrequency.containsKey(words.get(i))) {//if duplicate
                            int prevFrequency = termFrequency.get(words.get(i));
                            termFrequency.put(words.get(i), prevFrequency + 1);//increase counter
                        } else {
                            termFrequency.put(words.get(i), 1);//add new term to frequency map
                        }
                    }
                    index.addTerm(words, docs.getId(), wordPosition);//addTerm to index
                    wordPosition++;//increment word position
                    totalTerms = words.size();
                    //System.out.println(totalTerms);
                }            
                double sumTermWeights = 0.0;//add all term weights
                ArrayList<Integer> tf_d = new ArrayList<>(termFrequency.values());//term frequences in a document
                //System.out.println("tf_d"+tf_d);
                for (int i = 0; i < tf_d.size(); i++) {//loop term frequencies
                    double w_dt = 1 + Math.log(tf_d.get(i));//term weight in a document
                    w_dt = Math.pow(w_dt, 2.0);
                    sumTermWeights += w_dt;//summation of w_dt^2
                    //System.out.println("sumTermWeights sqrt " + sumTermWeights);
                }
                //do math to get L_d
                double l_d = Math.sqrt(sumTermWeights);//square root normalized w_dt's
                documentWeight.add(l_d);
            }
            //write document weights to disk
            diskIndexWriter.writeDocumentWeights(documentWeight, indexLocation);
            return index;
        }
        /**
         *  Used to make a Positional Inverted Index of a corpus in Memory "Slow"
         * @param corpus
         * @return index
         */
        public static Index indexCorpus(DocumentCorpus corpus) {
            AdvancedTokenProcessor processor = new AdvancedTokenProcessor();//to process a token	
            PositionalInvertedIndex  index = new PositionalInvertedIndex();//for indexing
            Iterable<Document> documents = corpus.getDocuments();//get corpus documents to iterate
            List<String> wordList = new ArrayList<String>();//words found
            int position = 0;
            int docCount = 0;
            for (Document d : documents) {
                docCount++;
                EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
                Iterable<String> tokens = stream.getTokens();//read streams into tokens
                for(String token : tokens){
                    //String term = processor.processToken(token); //get 1 token at a time
                    wordList = processor.processToken(token);
                    //for (int i = 0; i < words.size(); i++) {
                    index.addTerm(wordList,d.getId(),position); //required because must know 
                    position++;
                }
                try{
                    stream.close(); //close stream
                }catch(IOException exe){
                    exe.printStackTrace(); //in case of backlash
                    }
            position = 0; //reset position
            }
            System.out.println("Number of Documents: " + docCount);
            return index;
        }
        /*
         * build Index but using a timer to load and also for special query
         */
        public static Index buildIndex(DocumentCorpus corpus, String path){
            DiskIndexWriter diskIndexWriter = new DiskIndexWriter();
            long startTime = System.nanoTime();
            Index index = indexCorpus(corpus);
            long endTime = System.nanoTime();
            long totalTime = endTime - startTime;
            // Create a DocumentCorpus to load .txt documents from the project directory.
            System.out.println("Corpus indexed in: " + totalTime / 1000000000 + " seconds");
            try {
                diskIndexWriter.writeIndex(index, path);
            } catch (IOException e) {
                // Auto-generated catch block
                System.out.println("Something went wrong.");
                e.printStackTrace();
            }
            return index;
        }
        public String stemWord(String word){//get the stem of a word
            ArrayList<String> stemmedWord = new AdvancedTokenProcessor().processToken(word);
            return stemmedWord.get(0);
        }
        public DiskPositionalIndex buildDiskPositionalIndex(String dir) {
            return new DiskPositionalIndex(dir);
        }
        public double getQueryTime() {
            return queryTime;
        }
        public  void setQueryTime(double queryTime) {
            this.queryTime = queryTime;
        }
        public int getTEST_ITERATIONS() {
            return TEST_ITERATIONS;
        }

    }
    
