package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskIndexWriter;
import cecs429.indexes.Index;
import cecs429.indexes.PositionalInvertedIndex;
import cecs429.indexes.Posting;
import cecs429.queries.BooleanQueryParser;
import cecs429.text.AdvancedTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
    
public class Indexer {
        
    public static void main(String[] args) throws IOException {
        Index index;
        Scanner scan = new Scanner(System.in);
		System.out.println("What is the path of the directory you would like to index: ");
		String s = scan.nextLine();
        //"/Users/berry/Desktop/CECS429/all-nps-sites-extracted"
        // /Users/berry/Desktop/CECS429/testCorpus
        // /Users/berry/Desktop/cor
        // Create a DocumentCorpus to load .txt documents from the project directory.
        DocumentCorpus corpus = DirectoryCorpus.loadTextDirectory(Paths.get(s).toAbsolutePath());
        index = buildIndex(corpus, s);

        /**************************************
        *                   MENU
        **************************************/
        while (true){
        System.out.println("Enter search query: ");
        String query = "whale"; // hard-coded search for "whale"
        query = scan.nextLine();
        
        switch(query){
            case "q":
                 System.out.println("Shut down...");
                 scan.close();
                 return;//end program 
            case "stem":
                AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
                System.out.print("Enter word:");
                //ArrayList<String> word = processor.processToken(scan.next());
                System.out.print(processor.processToken(scan.next()));
                //System.out.println(word.get(1));
                System.out.println();
                scan.nextLine();
                break;
            case "vocab":
                List<String> vocabList = index.getVocabulary(); //make a temp vocab list from vocab
                if(vocabList.size() >= 1000){ //check if vocab has at least 1000 words
                    for(int i = 0; i< 1000; i++){
                        System.out.println(vocabList.get(i)); //output the list
                    }
                }
                else{
                    for(int i = 0; i < vocabList.size(); i++){
                        System.out.println(vocabList.get(i));//output  the list if less than 100 words
                    }
                }
                System.out.println("Total vocabulary words: "+ vocabList.size());
                break;
            case "index":
                index = buildIndex(corpus,s);
                break;
            default:
                search(query,corpus,index);
                System.out.println("Enter Document ID number to view contents or -1 to continue: ");
                int docID = scan.nextInt();
                scan.nextLine();
                openDocument(docID,corpus);
                break;
                }
            }//end while
        }
        //Move this to another class later on
        public String webSearch(String query,DocumentCorpus corpus, Index index){
            List<Posting> postings = search(query, corpus, index);//Run Boolean Search
            StringBuilder postingsRows = new StringBuilder();
            String result = "";
            for (Posting post : postings) {//include document titles for each returned posting

            String title = corpus.getDocument(post.getDocumentId()).getTitle();
            String row = "    <tr>\n" +
                        "        <td>"+post.getDocumentId()+"</td>\n" +
                        "        <td><button id=\"" + post.getDocumentId() + "\" onClick=\"docClicked(this.id)\" >"+title+"</button></td>\n" +
                        "        <td>"+post.getPostions()+"</td>\n" +
                        "    </tr>\n";
                postingsRows.append(row);

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
        }
        //Boolean search
        public static List<Posting> search(String query,DocumentCorpus corpus, Index index){
            BooleanQueryParser parser = new BooleanQueryParser(); //boolean for terms
            List<Posting> postings = parser.parseQuery(query).getPostings(index);
                int docCount = 0; //doc counter
                //get the postings of the query after parsing  using index
                for(Posting p: postings){
                    System.out.println(p.getDocumentId() + ". " + corpus.getDocument(p.getDocumentId()).getTitle());
                    docCount++;
                    System.out.println(p.getPostions());
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
    }
    
