package edu.csulb;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.tartarus.snowball.ext.porterStemmer;

import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Index;
import cecs429.indexes.Indexer;
import cecs429.indexes.PositionalInvertedIndex;
import cecs429.indexes.Posting;
import cecs429.queries.BooleanQueryParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import cecs429.documents.*;
//import csulb.edu.Indexer;
//import 
/**
 * Unit test for simple App.
 */
public class TestIndex 
{
	String[] testDoc1 = {"apple","banana","dog","enjoi",
	"enjoy","in","is","known","mani","monument",
	"on","park","raid","seattl","the","there","wa",
	"washington","well","wellknown","went"};
	String[] testDoc2 = {"apple","banana","dog","enjoi",
	"enjoy","in","is","known","mani","monument",
	"on","park","raid","seattl","the","there","wa",
	"washington","well","wellknown","went"};
	ArrayList<Integer> positions = new ArrayList<>();
	public void  buildPositions(){
	for(int i=1; i<=21; i++){positions.add(i);}
	}
	BooleanQueryParser bp = new BooleanQueryParser();
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
	public void testCorpus() {
		//Has the indexer index the document
		Index index = new DiskPositionalIndex("/Users/berry/Desktop/CECS429/testCorpus2");
		for(int i = 1; i < 6; i++) {
			List <String> terms = index.getVocabulary();
		}
		
		
		
		Indexer indexer = new Indexer();
        int position = 0;

		}

        @Test
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
			String[] query = testQuery.split(" ");
			buildPositions();
			for (String term: query){
				postings.add(new Posting(docOne,positions));
			}
			return index;
		}
	/**
	 * Fills hand built index with terms by hand
	 * @param index Hand built index
	 * @param terms List of terms
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