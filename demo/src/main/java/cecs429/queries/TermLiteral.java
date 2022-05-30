package cecs429.queries;

import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.AdvancedTokenProcessor;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements QueryComponent {
	private String mTerm;
	private boolean isPos;

	public TermLiteral(String term) {
		mTerm = term;
		isPos = true;//
	}
	
	public String getTerm() {
		return mTerm;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
		return index.getPostings(processor.processToken(mTerm).get(0));//process term literal token in first of posting
		//return index.getPostings(mTerm);
	}
	
	@Override
	public String toString() {
		return mTerm;
	}

	@Override
	public boolean isPositive(){
		return isPos;
	}
	@Override
	public void setNegative(){
		isPos = false;
	}
}
