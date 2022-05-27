package edu.csulb;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.Document;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.Index;
import cecs429.indexes.PositionalInvertedIndex;
import cecs429.indexes.Posting;
import cecs429.text.BasicTokenProcessor;
import cecs429.text.EnglishTokenStream;

import java.io.IOException;
import java.nio.file.Paths;
//import java.util.HashSet;
import java.util.Scanner;
    
public class PositionalInvertedIndexIndexer {
        
    public static void main(String[] args) {
        // Create a DocumentCorpus to load .txt documents from the project directory.
        DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get("/Users/berry/Desktop/CECS429/Search-Engine/demo/src/main/java/all-nps-sites-extracted").toAbsolutePath(), ".txt");
        //DocumentCorpus corpus = DirectoryCorpus.loadJsonDirectory(Paths.get("/Users/berry/Desktop/CECS429/SearchEngineProject/all-nps-sites-extracted").toAbsolutePath(), ".txt");
        // Index the documents of the corpus.
        long startTime = System.nanoTime();
        Index index = indexCorpus(corpus);
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Corpus indexed in: " + totalTime / 1000000000 + " seconds");
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter search query: ");
        //String query = "whale"; // hard-coded search for "whale"
            String query = scan.nextLine();
            scan.close();
            for (Posting p : index.getPostings(query)) {
                System.out.println("Document " + corpus.getDocument(p.getDocumentId()).getTitle());
            }
        }
        private static Index indexCorpus(DocumentCorpus corpus) {
            //HashSet<String> vocabulary = new HashSet<>();
            BasicTokenProcessor processor = new BasicTokenProcessor();	
            PositionalInvertedIndex  index = new PositionalInvertedIndex();
            int position = 0;
            for (Document d : corpus.getDocuments()) {
                EnglishTokenStream stream = new EnglishTokenStream(d.getContent());
                for(String token : stream.getTokens()){
                //get 1 token at a time
                //System.out.println(token);
                String term = processor.processToken(token);
                index.addTerm(term,d.getId(),position); //required for matrix because must know 
                position++;
                }
                try{
                    stream.close(); //close stream
                }catch(IOException exe){
                    exe.printStackTrace(); //in case of backlash
                    }
            position = 0; //reset position
            }
            return index;
        }
    }
    
