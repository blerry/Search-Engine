package edu.csulb;

import org.junit.Test;

import modules.indexes.Index;
import modules.indexes.PositionalInvertedIndex;
import modules.indexes.Posting;
import modules.queries.*;
import modules.text.AdvancedTokenProcessor;

import org.junit.Before;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;


import static org.junit.Assert.assertNull;
public class TestQuery {
	AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
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
	//@Before
	public void parse(){
		BooleanQueryParser bp = new BooleanQueryParser();
		qc = bp.parseQuery(testQuery);
//		assertTrue();
		assertTrue("Query Exists", qc != null);
	}
	//@Before
	public void parseNull(){
		BooleanQueryParser bp = new BooleanQueryParser();
		qc = bp.parseQuery("");
//		assertTrue();
		assertNull("Query Exists", null);
	}
	//@Test
	public void andQueryTest(){
		String query = "apple banana";
		BooleanQueryParser bp = new BooleanQueryParser();
		
	}

	//@Test
	public void testSpaces() {
		String query = "dog              ";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}
	//@Test
	public void testFrontSpaces() {
		String query ="       dog             ";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}
	//@Test
	public void testCapitalizations() {
		String query ="DOG";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}
	//@Test
	public void testQuotations() {
		String query ="\"DOG\"";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}

	//@Test
	public void testTwoPhaseQueries() {
		String query = "\"cat dog\" \"fox make\"";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}
	//@Test
	public void testThreeQueries() {
		String query = "cat dog it";
		List<String> result = processor.processToken(query);
		assertEquals("dog", result.get(0));
	}
	public void getPostingsPositionQuery(){
		Index index = new PositionalInvertedIndex();
		List<Posting> emptyPostings = new ArrayList<>();
		assertEquals(emptyPostings,qc.getPostingsPositions(index));
	}
	//@Test
	public void getPostingsQuery(){
		Index index = new PositionalInvertedIndex();
		List<Posting> emptyPostings = new ArrayList<>();
		assertEquals(emptyPostings,qc.getPostings(index));
	}

	//private void 
	//@Test
	public void findNextLiteralTest(){
		//bp.findNextLiteral(testQuery,0);
		//assertEquals("", actual);
	}
	//@Test
	public void findNextSubQueryTest(){
		//assertEquals(expected, actual);
	}

}
