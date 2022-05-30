package cecs429.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;//the children
	private boolean isPos;

	//public OrQuery(List<QueryComponent> components) {
	public OrQuery(Iterable<QueryComponent> components){
		isPos = true;
		mComponents = (List<QueryComponent>) components;
	}
	
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = new ArrayList<Posting>();//post list
		result = mComponents.get(0).getPostings(index);//Set posting list to first element
		List<Posting> currList = new ArrayList<Posting>();
		// TODO: program the merge for an OrQuery, by gathering the postings of the composed QueryComponents and
		// unioning the resulting postings.
		for(int i = 1; i< mComponents.size(); i++){
			List<Posting> tempList = new ArrayList<Posting>();
			currList = mComponents.get(i).getPostings(index);
			int a =0, b=0;//counts
			Posting pA = result.get(a);
			Posting pB = result.get(b);
			int docA = pA.getDocumentId();
			int docB = pB.getDocumentId();

			while(a<result.size() && b < currList.size()){
				
			}
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]"
		return "(" +
		 String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}
	@Override
	public boolean isPositive() {
		return true;
	}
	public void setNegative() {
		//isPos = false;
	}
}
