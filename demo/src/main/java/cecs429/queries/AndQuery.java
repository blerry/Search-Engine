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
	private boolean isPos;
	
	public AndQuery(List<QueryComponent> components) {
		isPos = true;
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
				//perform regular AND operation
			while (a < result.size() && b < currList.size()) {

				pA = result.get(a); // current posting in post List
				pB = currList.get(b); // current posting in current List
				docA = pA.getDocumentId(); //id of doc a
				docB = pB.getDocumentId(); // id of doc b
					
				if (docA == docB) { // if docIDs match iterate through pos lists
					tempList.add(pA); //  temporarily add posting
					lastPosting = tempList.get(tempList.size() - 1); // get last posting
					for (int pos : pB.getPostions()) {
						lastPosting.addPosition(pos); //add the positions
					}
					a++;
					b++;//increment counts a,b
				} 
				else {// if no match increment the min of the 2
					if (docA <= docB) {
						a++;
					}
					else{ 
						b++;
						}
					}
				} // end of loop
			//}
			result = tempList; // set post List to tempList
		} // end of loop
		return result;
	}
	
	@Override
	public String toString() {
		return
		 String.join(" ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()));
	}
	
}
