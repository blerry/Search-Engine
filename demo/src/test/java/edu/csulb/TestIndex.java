package edu.csulb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.tartarus.snowball.ext.porterStemmer;

import modules.documents.*;
import modules.indexes.DiskPositionalIndex;
import modules.indexes.Index;
import modules.indexes.Indexer;
import modules.indexes.PositionalInvertedIndex;
import modules.indexes.Posting;
import modules.queries.BooleanQueryParser;
import modules.text.AdvancedTokenProcessor;
import modules.text.EnglishTokenStream;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
//import csulb.edu.Indexer;
//import 
/**
 * Unit test for simple App.
 */
public class TestIndex 
{
	String testDoc1 = "apple banana dog enjoi enjoy in is known mani monument on park raid seattl the there wa washington well wellknown went";
	String testDoc2 = "apple banana dog enjoi enjoy in is known mani monument on park raid seattl the there wa washington well wellknown went";
	String[] testCorpus = {testDoc1,testDoc2};
	ArrayList<Integer> positions = new ArrayList<>();
	public void  buildPositions(){
	for(int i=1; i<=21; i++){positions.add(i);}
	}
	BooleanQueryParser bp = new BooleanQueryParser();
    /**
     * Rigorous Test :-)
     */
    //@Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    //@Test
	public void testCorpus() {
		//Has the indexer index the document
		Index index = new DiskPositionalIndex("/Users/berry/Desktop/CECS429/testCorpus");
		for(int i = 1; i < 6; i++) {
			List <String> terms = index.getVocabulary();
		}
		
		Indexer indexer = new Indexer();
        int position = 0;

		assertTrue("Vocab exists",index.getVocabulary().size()>0);
		assertEquals(-1, index.getDocumentFrequencyOfTerm("sites"));
		assertEquals(-1, index.getDocumentFrequencyOfTerm("effective"));
		assertEquals(0, index.getPostingsPositions("effective").size());
		assertEquals(0, index.getPostings("town").size());
		}

		//@Test
		public void smallIndex(){
			//DocumentCorpus corpus = 
			PositionalInvertedIndex index = new PositionalInvertedIndex();
			Indexer indexer = new Indexer();
			
			AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
			List<String> wordList = new ArrayList<String>();
			StringReader reader1 = new StringReader(testDoc1);
			StringReader reader2 = new StringReader(testDoc2);
			StringReader[] testCorpus = {reader1,reader2};
            
            for (int doc = 0; doc < testCorpus.length; doc++) {
				int position =0;
                EnglishTokenStream stream = new EnglishTokenStream(testCorpus[doc]);
                Iterable<String> tokens = stream.getTokens();//convert read data into tokens
                for(String token : tokens){
					List<String> words = processor.processToken(token);//convert a token to indexable terms
                    //indexer.indexCorpus(corpus);
                    index.addTerm(words,doc,position); //required because must know 
                    position++;
                }
			}
			List<String> vocab =index.getVocabulary();
			assertEquals(2, testCorpus.length);
			assertTrue("vocab exists", vocab.size()>0);
			assertTrue("postings exist",index.getPostings("apple")!=null);
			assertEquals(0, index.getPostings("apple").size());
			assertEquals(21, vocab.size());
			assertEquals(0, index.getTermFrequency("apple"));
			assertEquals(0.0, index.getDocumentFrequencyOfTerm("apple"),0.0);
		
		}

        //@Test
        public void PrepIndex(){
		List<Posting> postings = new ArrayList<Posting>();
		//System.out.println(index);
        //assertEquals(21, index.getTermCount());
		String [] terms = {"ar","british","dog","enjoi",
				"enjoy","in","is","known","mani","monument",
				"on","park","raid","seattl","the","there","wa",
				"washington","well","wellknown","went"};
		
		Index handBuilt = new DiskPositionalIndex("/Users/berry/Desktop/CECS429/testCorpus2");
        for (String term: terms){
            for(Posting p: handBuilt.getPostings(term))
            postings.add(p);
        }
		fillHandBuilt(handBuilt, postings);
		
		// Runs through each term and each positional posting to see if the handbuilt index matches the index
		for(int i = 0; i < terms.length; i ++) {
			for(int j = 0; j < handBuilt.getPostings(terms[i]).size(); j++) {
				System.out.println(terms[i]);
				//System.out.println("Hand built: " +handBuilt.getPostings(terms[i]).get(j).getDocId());
				//System.out.println("index: " + index.getPostings(terms[i]).get(j).getDocId());
				//assertEquals(handBuilt.getPostings(terms[i]).get(j), index.getPostings(terms[i]).get(j));
			    }
		    }
	    }

		public Index indexForQuery(String[] terms){//test index for Query
			String testQuery = "apple banana";
			Index index = new PositionalInvertedIndex();
			List<Posting> postings = new ArrayList<>();
			int docOne = 1;
			int docTwo = 2;
			String[] query = testQuery.split(" ");
			buildPositions();
			postings.add(new Posting(docOne,positions));
			postings.add(new Posting(docTwo,positions));
			return index;
		}
	/**
	 * Fills hand built index with terms by hand
	 * @param index Hand built index
	 */
	public void fillHandBuilt(Index index, List<Posting> postings) {
		int pos = 1;
        for(Posting p:postings){
		p.addPosition(pos); // ar Term, docID, pos
		pos++;
		//index.addPosition(terms[20], 2, 6); // went
        }
	}
}