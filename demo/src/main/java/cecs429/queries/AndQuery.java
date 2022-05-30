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
		mComponents = components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		//List<Posting> result = null;
		
		// TODO: program the merge for an AndQuery, by gathering the postings of the composed QueryComponents and
		// intersecting the resulting postings.
		List<Posting> result = new ArrayList<Posting>();
		result =mComponents.get(0).getPostings(index); // set pList to first element

		List<Posting> currList = new ArrayList<Posting>(); // current list of postings for the query

		for (int i = 1; i < mComponents.size(); i++) {
			List<Posting> tempList = new ArrayList<Posting>(); // templist to hold the merge between pList and curList
			currList = mComponents.get(i).getPostings(index);
			int a = 0, b = 0;
			Posting pA;
			Posting pB;
			int docA;
			int docB;
			Posting lastPosting = null;
			
			if (mComponents.get(0).isPositive() == false && i == 1 || mComponents.get(i).isPositive() == false) { // if the first or current element is negative
				
				pA = result.get(a); // current posting in pList
				pB = currList.get(b); // current posting in curList
																												 
				docA = pA.getDocumentId(); // docID counter for pList
				docB = pB.getDocumentId(); // docID counter for curList

				//last posting in tempList
				while (a < result.size() && b < currList.size()) { // while both the counters for both pList and
																			// curList do not exceed their size
					pA = result.get(a); 
					pB = currList.get(b);
					
					docA = pA.getDocumentId(); // docID_A = the "countA" docid in the postings list
																	// for pList
					docB = pB.getDocumentId(); // docID_B = the "countB" docid in the postings list
																	// for curList
					if (docA == docB) { // if docIDs match increment the counts but dont add either because we are
												// performing an AND NOT
						a++;
						b++;
					} // end of if
					else {// if docIDs do not match increment the smaller of the two
						if (docA < docB) { // add the posting from pList if it is positive
							if (mComponents.get(0).isPositive()) {
								tempList.add(pA);
							}
							a++;
						} else if (docB < docA) { // add the posting from curList if it is positive
							if (mComponents.get(i).isPositive()) {
								tempList.add(pB);
							}
							b++;
						}
					} // end of else
				} // end of while loop
				
				//once broken out of the loop add the remaining postings from the list if is positive
				if (a == result.size() && mComponents.get(i).isPositive() && docB != currList.size()) { 
					for (int j = b; j < currList.size(); j++) {
						tempList.add(currList.get(j));
					}
				} else if (b == currList.size() && mComponents.get(0).isPositive() && docA != result.size()) {
					for (int j = a; j < result.size(); j++) {
						tempList.add(result.get(j));
					}
				}
			} else {
				//perform regular AND operation
				while (a < result.size() && b < currList.size()) {
					
					pA = result.get(a); // current posting in pList
					pB = currList.get(b); // current posting in curList
					
					
					docA = pA.getDocumentId();
					docB = pB.getDocumentId();
					
					if (docA == docB) { // if docIDs match iterate through pos lists
						tempList.add(pA);
						lastPosting = tempList.get(tempList.size() - 1);
						for (int pos : pB.getPostions()) {
							lastPosting.addPosition(pos);
						}

						a++;
						b++;
					} // end of if
					else {// if docIDs do not match increment the smaller of the two
						if (docA <= docB) {
							a++;
						} else {
							b++;
						}
					} // end of else
				} // end of while loop
			}//end of else
			result = tempList; // set pList to tempList
		} // end of for loop
			// TODO: program the merge for an AndQuery, by gathering the postings of the
			// composed QueryComponents and
			// intersecting the resulting postings.

		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
	@Override
	public boolean isPositive() {
		return true;
	}
	public void setNegative() {
		//isPos = false;
	}
}
