package cecs429.queries;

import java.util.List;
import java.util.ArrayList;
import cecs429.indexes.Index;
import cecs429.indexes.Posting;
import cecs429.text.AdvancedTokenProcessor;

/**
 * A TermLiteral represents a single term in a subquery.
 */
public class TermLiteral implements QueryComponent {
	private String mTerm;
	public TermLiteral(String term) {
		mTerm = term;

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
	public List<Posting> getPostingsPositions(Index index) {
		//process token for valid characters
		AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
		List<String> terms = processor.processToken(mTerm);
		for (int i = 0; i < terms.size(); i++) {
			terms.set(i, AdvancedTokenProcessor.stemToken(terms.get(i)));//stem the token
		}
		//collect the postings for the term
		List<Posting> result = new ArrayList<Posting>();
		for (String term: terms) {
			if (index.getPostingsPositions(term) != null) {
				result.addAll(index.getPostingsPositions(term));
			}
		}
		return result;
	}
	@Override
	public String toString() {
		return mTerm;
	}



}
