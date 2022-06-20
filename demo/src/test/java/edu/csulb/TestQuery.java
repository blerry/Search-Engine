package edu.csulb;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import cecs429.queries.*;
public class TestQuery {
	@Test
	public void parse(){
		BooleanQueryParser bp = new BooleanQueryParser();
		bp.parseQuery("apple banana");
		assertTrue();
	}
}
