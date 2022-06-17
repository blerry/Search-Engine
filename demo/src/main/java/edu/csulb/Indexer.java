package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskIndexWriter;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.PositionalInvertedIndex;
import cecs429.indexes.Posting;
import cecs429.queries.Accumulator;
import cecs429.queries.BooleanQueryParser;
import cecs429.queries.TermLiteral;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
    
public class Indexer {
    private static final int RANKED_RETURN = 10;//change
    
        public String webSearch(String query,DocumentCorpus corpus, Index index, Boolean isBooleanQuery){
            StringBuilder postingsRows = new StringBuilder();
            String result = "";
            System.out.println("Starting Query...");
            String[] terms = query.split(" ");
            int docCount = 0;
            if (isBooleanQuery) {//process a boolean query
                List<Posting> postings = search(query, corpus, index);//Run Boolean Search
                for(String term:terms){
                    term = term.toLowerCase();
                    List<Posting> tempPostingList = index.getPostingsPositions(term);
                    //postings.get(docCount).getPostions()
                
            for (Posting post : postings) {//include document titles for each returned posting
                    String title = corpus.getDocument(post.getDocumentId()).getTitle();
                    String row = "    <tr>\n" +
                                "        <td>"+post.getDocumentId()+"</td>\n" +
                                "        <td><button id=\"" + post.getDocumentId() + "\" onClick=\"docClicked(this.id)\" >"+title+"</button></td>\n" +
                                "        <td>"+tempPostingList.get(docCount).getPostions()+"</td>\n" +
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
                }else{
                    PriorityQueue<Accumulator> pq;
                    pq = userRankedQueryInput(corpus, index, query);
                    int pqSize = pq.size();
                //System.out.println("psSize" + pq.size());

                while(!pq.isEmpty()){
                    //System.out.println("RUNS 2");
                    Accumulator currAcc = pq.poll();
                    String title = corpus.getDocument(currAcc.getDocId()).getTitle();
                    int docId = currAcc.getDocId() + 1;
                    docId--;
                    double value = currAcc.getA_d();
                    System.out.println("Value" + value);
                    //BigDecimal sValue = new BigDecimal(value);
                    //MathContext m = new MathContext(5);
                    //sValue.round(m);
                    //System.out.println("New Value "+ sValue);

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

                    return result;
                }
        }
        //Boolean search
        public static List<Posting> search(String query,DocumentCorpus corpus, Index index){
            BooleanQueryParser parser = new BooleanQueryParser(); //boolean for terms
            List<Posting> postings = parser.parseQuery(query).getPostings(index);
            //List<Posting> postingPositions = new ArrayList<Posting>();
            String[] terms = query.split(" ");  
            //int boolCounter = 0;
            int docCount = 0; //doc counter
            for (String term : terms) { // for each term in query
                term = term.toLowerCase();
                List<Posting> tempPostingList = index.getPostingsPositions(term);
                //get the postings of the query after parsing  using index
                for(Posting p: postings){
                    System.out.println(p.getDocumentId() + ". " + corpus.getDocument(p.getDocumentId()).getTitle());
                    System.out.println(tempPostingList.get(docCount).getPostions());//print positions
                    docCount++;
                }
            //boolCounter++;
            }
            System.out.println("Number of Documents: " + docCount);
                return postings;
            }
        public static void openDocument(int docID, DocumentCorpus corpus) throws IOException{
                //Get document contents the user wants
                if(docID>=0){
                    //Get contents of Document user asked for
                    BufferedReader bufferedReader = new BufferedReader(corpus.getDocument(docID).getContent());
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    //use bufferedReader to read each single character in line
					while ((line = bufferedReader.readLine()) != null) {
						stringBuilder.append(line); //building the string
					}
                    String str = stringBuilder.toString(); //the string results
                    System.out.println(str); //display
                    bufferedReader.close(); //close reader
                    //break;
                }
                else{
                    System.out.println("Bad Input");
                    return;
                }
            //return query;
        }
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
        public String stemWord(String word){
            ArrayList<String> stemmedWord = new AdvancedTokenProcessor().processToken(word);
            return stemmedWord.get(0);
        }
        //ranked query
    public static PriorityQueue<Accumulator> userRankedQueryInput(DocumentCorpus corpus, Index index, String queryInput) {
        System.out.println("RUNS");
        double n = corpus.getCorpusSize();
        List<TermLiteral> termLiterals = new ArrayList<TermLiteral>();
        int counter = 0;
        List<Posting> postings = new ArrayList<Posting>();
        HashMap<Posting, Double> hm = new HashMap<>();
        PriorityQueue<Accumulator> pq = new PriorityQueue<>(RANKED_RETURN);

        String[] terms = queryInput.split(" ");
        for (String term : terms) { // for each term in query
            term = term.toLowerCase();
            String stemmedTerm = AdvancedTokenProcessor.stemToken(term);
            termLiterals.add(new TermLiteral(stemmedTerm));

            int df_t = index.getDocumentFrequencyOfTerm(stemmedTerm);
            double w_qt = Math.log(1 + (n/df_t));  // calculate wqt = ln(1 + N/dft)
            //not as accurate, but saves us from thousands of disk reads
                postings = termLiterals.get(counter).getPostings(index);
                counter++;
                double tf_td = (double) index.getTermFrequency(stemmedTerm) / (double) postings.size();
                for(Posting p : postings){ // for each document in postings list
                    double w_dt = 1 + Math.log(tf_td);
                    //print do not truncate
                    double a_d = (w_dt * w_qt);
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
            // only retain the a certain amount of the top results
            double value = acc.getA_d() / index.getDocumentWeight(acc.getDocId());
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
        public static Index indexDiskCorpus(DocumentCorpus corpus,String indexLocation) throws IOException {
            PositionalInvertedIndex index = new PositionalInvertedIndex();//create positional index
            AdvancedTokenProcessor processor = new AdvancedTokenProcessor();//create token processor
            DiskIndexWriter diskIndexWriter = new DiskIndexWriter();
            ArrayList<Double> documentWeight = new ArrayList<>();
            // Get all the documents in the corpus by calling GetDocuments().
            Iterable<Document> documents = corpus.getDocuments();
            
            for (Document docs : documents) {//iterate through every valid document found in the corpus
                int totalTerms = 0;
    
                HashMap<String, Integer> termFrequency = new HashMap<>();//term frequency of every term in a document
                // Tokenize the document's content by constructing an EnglishTokenStream around the document's content.
                EnglishTokenStream stream = new EnglishTokenStream(docs.getContent());
                Iterable<String> tokens = stream.getTokens();//convert read data into tokens
                int wordPosition = 1;//maintain the position of the word throughout the document
                // Iterate through the tokens in the document, processing them using a BasicTokenProcessor,
                for (String token : tokens) {
                    List<String> words = processor.processToken(token);//convert a token to indexable terms
                    for (int i = 0; i < words.size(); i++) {//iterate through all unstemmed tokens
                        words.set(i, AdvancedTokenProcessor.stemToken(words.get(i)));
                        if (termFrequency.containsKey(words.get(i))) {//if term is duplicate
                            int prevFrequency = termFrequency.get(words.get(i));
                            termFrequency.put(words.get(i), prevFrequency + 1);//increment term frequency counter
                        } else {
                            termFrequency.put(words.get(i), 1);//add new term to frequency counter
                    }
                }
                index.addTerm(words, docs.getId(), wordPosition);//add word data to index
                wordPosition++;//increment word position
                totalTerms = words.size();
                System.out.println(totalTerms);
            }

            
            /* */

            double sumTermWeights = 0;//sum of term weights
            ArrayList<Integer> tf_d = new ArrayList<>(termFrequency.values());//every term frequency in the document

            for (int i = 0; i < tf_d.size(); i++) {//iterate through all term frequencies
                double w_dt = 1 + Math.log((double)tf_d.get(i));//weight of specific term in a document
                w_dt = Math.pow(w_dt, 2);
                sumTermWeights += w_dt;//summation of w_dt^2
            }
            //do math to get L_d
            double l_d = Math.sqrt(sumTermWeights);//square root normalized w_dt's
            documentWeight.add(l_d);

        }
        //write document weights to disk
        diskIndexWriter.writeDocumentWeights(documentWeight, indexLocation);
        return index;
        }

        public static Index indexCorpus(DocumentCorpus corpus) {
            //HashSet<String> vocabulary = new HashSet<>();
            AdvancedTokenProcessor processor = new AdvancedTokenProcessor();	
            PositionalInvertedIndex  index = new PositionalInvertedIndex();

            // Get all the documents in the corpus by calling GetDocuments().
            Iterable<Document> documents = corpus.getDocuments();
            List<String> wordList = new ArrayList<String>();
            int position = 0;
            int docCount = 0;
            for (Document d : documents) {
                docCount++;
                EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
                Iterable<String> tokens = stream.getTokens();//convert read data into tokens
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
        public DiskPositionalIndex buildDiskPositionalIndex(String dir) {
            return new DiskPositionalIndex(dir);
        }
    }
    
