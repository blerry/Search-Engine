package cecs429.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;

/**
 * An AndQuery composes other QueryComponents and merges their postings in an intersection-like operation.
 */
public class AndQuery implements QueryComponent {
	private List<QueryComponent> mComponents;

	
	public AndQuery(List<QueryComponent> components) {
		mComponents = new ArrayList<QueryComponent>(components);
	}
	@Override
	public List<Posting> getPostingsPositions(Index index) {
		return getPostings(index);
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		//List<Posting> result = null;
		
		//program the merge for an AndQuery, by gathering the postings of the composed QueryComponents and
		// intersecting the resulting postings.
		List<Posting> result = new ArrayList<Posting>();
		result =mComponents.get(0).getPostings(index); // set pList to first element

		if (mComponents.size() < 2) {//should be impossible to reach for and query
			System.out.println("How did you get in the And Query?");
		} {//if you only have to merge 2 postings

			//verify the both terms appear at least in one document
			if (mComponents.get(0).getPostings(index) != null &&
					mComponents.get(1).getPostings(index) != null) {
				//merge two postings together into result
				result = andMergePosting(mComponents.get(0).getPostings(index), mComponents.get(1).getPostings(index));
			}

			//iterate through the rest of the postings
			for (int i = 2; i < mComponents.size(); i++) {

				//verify the next posting appears in at least 1 document
				if (mComponents.get(i).getPostings(index) != null) {
					//merge previous result postings with new term postings
					result = andMergePosting(mComponents.get(i).getPostings(index), result);
				}

			}

		}

		return result;
	}

	/**
	 * merge two postings lists together based on the ANDing the document id's
	 * @param firstPostings first list of postings
	 * @param secondPostings second list of postings
	 * @return merged list of postings after ANDing the two postings together
	 */
	private List<Posting> andMergePosting(List<Posting> firstPostings, List<Posting> secondPostings) {

		List<Posting> result = new ArrayList<Posting>();

		//starting indices for both postings lists
		int i = 0;
		int j = 0;

		//iterate through both postings lists, end when one list has no more elements
		while (i < firstPostings.size() && j < secondPostings.size()) {

			//both lists have this document
			if (firstPostings.get(i).getDocumentId() == secondPostings.get(j).getDocumentId()) {
				result.add(firstPostings.get(i));//include it in merged list
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
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
	
}
