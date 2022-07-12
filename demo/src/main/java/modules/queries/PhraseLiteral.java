package modules.queries;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import modules.indexes.Index;
import modules.indexes.Posting;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase. Changed to Query
	//private List<String> mTerms = new ArrayList<>();
	private List<QueryComponent> mTerms = new ArrayList<>();
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	//public PhraseLiteral(List<String> terms)
	public PhraseLiteral(List<QueryComponent> terms) {
		mTerms.addAll(terms);
		//for(QueryComponent q:terms){ //Look through terms and add to children list (mterms)
			//mTerms.add(q);}
		//mTerms.addAll(terms);
	}
	
	/**
	 * Constructs a PhraseLiteral given a string with one or more individual terms separated by spaces.
	 /
	public PhraseLiteral(String terms) {
		mTerms.addAll(Arrays.asList(terms.split(" ")));
	}
	*/
	
	@Override
	public List<Posting> getPostings(Index index) {
		return getPostingsPositions(index);
	}

	@Override
	public List<Posting> getPostingsPositions(Index index) {
		List<Posting> result = new ArrayList<>();
		// Done: program this method. Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
		int distance = 1;//maintain the distance required between phrases

		if (mTerms.size() < 2) {//one child denotes a term literal
			if (mTerms.get(0) != null) {
				result = mTerms.get(0).getPostingsPositions(index);
			}
		} else  {//multiple terms to merge

			//verify that both terms appear at least in one document
			if (mTerms.get(0).getPostingsPositions(index) != null &&
				mTerms.get(1).getPostingsPositions(index) != null) {

				//merge the first 2 terms postings together
				result = andMerge(mTerms.get(0).getPostingsPositions(index),
						mTerms.get(1).getPostingsPositions(index), distance);

			}

			//if there are more terms in the phrase, iterate through the rest of the term postings
			for (int i = 2; i < mTerms.size(); i++) {

				distance++;//increase the distance between terms
				//verify the next posting appears in at least 1 document
				if (mTerms.get(i).getPostingsPositions(index) != null) {
					//merge previous result postings with new term postings
					result = andMerge(result, mTerms.get(i).getPostingsPositions(index), distance);
				}

			}

		}

		return result;
	}

	/**
	 * merge two postings lists together based on the ANDing the document id's, and that the first term is some
	 * distance before the second term
	 * @param firstPostings first list of postings
	 * @param secondPostings second list of postings
	 * @param distance positional space between the two terms
	 * @return merged list of postings after ANDing the two postings together
	 */

	 //O(logn)
	private List<Posting> andMerge(List<Posting> firstPostings, List<Posting> secondPostings, int distance) {

		List<Posting> result = new ArrayList<Posting>();

		//starting indices for both postings lists
		int i = 0;
		int j = 0;

		//iterate through both postings lists, end when one list has no more elements
		while (i < firstPostings.size() && j < secondPostings.size()) {

			//both lists have this document
			if (firstPostings.get(i).getDocumentId() == secondPostings.get(j).getDocumentId()) {
				//gather the positions of the phrase terms
				Posting newPosting = positionalMergePosting(firstPostings.get(i), secondPostings.get(j), distance);
				if (newPosting != null) {//if the phrase actually exists
					result.add(newPosting);//include it in merged list
				}
				i++;//iterate through in both lists
				j++;
				//first list docid is less than second lists docid
			} else if (firstPostings.get(i).getDocumentId() < secondPostings.get(j).getDocumentId()) {
				i++;//iterate first list
			} else {// second list docid is less than first lists docid
				j++;//iterate second list
			}

		}

		return result;

	}

	/**
	 * determine whether the first posting is some positional distance away from the second posting
	 * @param firstPosting doc id should match second term
	 * @param secondPosting doc id should match first term
	 * @param distance positional space between both terms
	 * @return valid postings based on positional distance
	 */
	private Posting positionalMergePosting(Posting firstPosting, Posting secondPosting, int distance) {

		Posting posting = null;//postings that are considered a phrase

		//positional indices
		int a = 0;
		int b = 0;

		//iterate through position list of both terms, until one runs out
		while (a < firstPosting.getPostions().size() &&
				b < secondPosting.getPostions().size()) {

			//check the different terms are in sequence
			//terms are in sequence
			if (firstPosting.getPostions().get(a) == (secondPosting.getPostions().get(b) - distance)) {
				if (posting == null) {
					posting = new Posting(firstPosting.getDocumentId(), firstPosting.getPostions());
					posting.addPosition(firstPosting.getPostions().get(a));
				} else {
					posting.addPosition(firstPosting.getPostions().get(a));
				}
				a++;
				b++;
				//first term is before the second
			} else if (firstPosting.getPostions().get(a) < (secondPosting.getPostions().get(b) - distance)) {
				a++;
				//second term is before the first
			} else {
				b++;
			}

		}

		return posting;

	}
	//Important for dinstinguishing phrases in string
	@Override
	public String toString() {
		return String.join(" ", mTerms.stream().map(c -> c.toString()).collect(Collectors.toList()));
		//return "\"" + String.join(" ", mTerms) + "\"";
		//return "";
	}
	
}
