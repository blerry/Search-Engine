package edu.csulb;

import org.junit.Test;

import cecs429.queries.*;
public class TestQuery {
	@Test
	public void parse(){
		BooleanQueryParser bp = new BooleanQueryParser();
		
	}









    /* 
    @Test
	public void testControl() {
		QueryComponent query = new QueryComponent("dog");
		List<String> result = query.getTokens();
		assertEquals("dog", result.get(0));
	}
	
	@Test
	public void testSpaces() {
		Query query = new Query("dog              ");
		List<String> result = query.getTokens();
		assertEquals("dog", result.get(0));
	}
	
	@Test
	public void testFrontSpaces() {
		//Currently, the tokens expected only have spaces following the word
		Query query = new Query("       dog             ");
		List<String> result = query.getTokens();
		assertEquals("dog", result.get(0));
	}
	
	@Test
	public void testCapitalizations() {
		Query query = new Query("DOG");
		List<String> result = query.getTokens();
		assertEquals("dog", result.get(0));
	}
	
	@Test
	public void testQuotations() {
		Query query = new Query("\"DOG\"");
		List<String> result = query.getTokens();
		assertEquals("dog", result.get(0));
	}
	
	@Test
	public void testApostropes() {
		Query query = new Query("DoG\'S");
		List<String> result = query.getTokens();
		assertEquals("dogs", result.get(0));
	}
	
	@Test
	public void testTwoQueries() {
		Query query = new Query("cat dog");
		List<String> result = query.getTokens();
		assertEquals("cat", result.get(0));
		assertEquals("dog", result.get(1));
	}
	
	@Test
	public void testThreeQueries() {
		Query query = new Query("cat dog it");
		List<String> result = query.getTokens();
		assertEquals("cat", result.get(0));
		assertEquals("dog", result.get(1));
		assertEquals("it", result.get(2));
	}
	
	@Test
	public void testOnePhaseQuery() {
		Query query = new Query("\"cat dog it\"");
		List<String> result = query.getTokens();
		assertEquals("cat dog it", result.get(0));
	}
	
	@Test
	public void testTwoPhaseQueries() {
		Query query = new Query("\"cat dog\" \"fox make\"");
		List<String> result = query.getTokens();
		assertEquals("cat dog", result.get(0));
		assertEquals("fox make", result.get(1));
	}
	
	@Test
	public void testPuncuation() {
		Query query = new Query("d.o.g.");
		List<String> result = query.getTokens();
		assertEquals("d.o.g", result.get(0));
	}
    */
}
