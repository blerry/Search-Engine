package edu.csulb;

import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.Position;

import static org.junit.Assert.assertNull;

import cecs429.indexes.Index;
import cecs429.indexes.PositionalInvertedIndex;
import cecs429.indexes.Posting;
import cecs429.queries.*;
public class TestQuery {
	String testQuery = "apple banana";
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
	QueryComponent qc;
	@Before
	public void parse(){
		BooleanQueryParser bp = new BooleanQueryParser();
		qc = bp.parseQuery(testQuery);
//		assertTrue();
		assertTrue("Query Exists", qc != null);
	}

	public Index indexForQuery(String[] terms){//test index for Query
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

	@Test
	public void getPostingsPositionQuery(){
		Index index = new PositionalInvertedIndex();
		List<Posting> emptyPostings = new ArrayList<>();
		assertEquals(emptyPostings,qc.getPostingsPositions(index));
	}
	@Test
	public void getPostingsQuery(){
		Index index = new PositionalInvertedIndex();
		List<Posting> emptyPostings = new ArrayList<>();
		assertEquals(emptyPostings,qc.getPostings(index));
	}

	//private void 
	@Test
	public void findNextLiteralTest(){
		//bp.findNextLiteral(testQuery,0);
		//assertEquals("", actual);
	}
	@Test
	public void findNextSubQueryTest(){
		//assertEquals(expected, actual);
	}

}
