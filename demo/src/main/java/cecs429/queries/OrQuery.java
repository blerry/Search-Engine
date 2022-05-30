package cecs429.queries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.util.ElementScanner14;
import javax.xml.transform.Templates;

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
				// curList do not exceed their size
				pA = result.get(a); 
				pB = currList.get(b);											
				docA = pA.getDocumentId(); // docA = the "A" count docid in the postings list																						// for pList
				docB = pB.getDocumentId(); // docB = the "B" count docid in the postings list																// for curList
				Posting lastPosting = null;
				if(!(tempList.isEmpty())) {
					lastPosting = tempList.get(tempList.size()-1);
				}
				if (docA == docB) { // if docIDs match add both to posting list
					tempList.add(pA);
					lastPosting = tempList.get(tempList.size()-1);
					for(int position: pB.getPostions()){
						lastPosting.addPosition(position);
					}
					//increment counters
					a++;
					b++;
				}
				else if(docA < docB){//if Doc A id is less than B add postings from result to temp list
					if(tempList.isEmpty())
						tempList.add(pA);
					else if (lastPosting.getDocumentId() == docA){
						 for(int position: pA.getPostions()){
							 lastPosting.addPosition(position);
						 }
					}
					else
						tempList.add(pA);
					a++;
				} else if(docB < docA){//id Doc B id is less than A add postings from result to temp list
					if(tempList.isEmpty()){
						tempList.add(pB);
					}
					else if(lastPosting.getDocumentId() == docB){
						for(int position: pB.getPostions()){
							lastPosting.addPosition(position);
						}
					}
					else{
						tempList.add(pB);
					}
					b++;
				}
			} // end of loop
													
			//once broken out of the loop add the remaining postings from the list if is positive(flag)
			if (a == result.size() && docB == currList.size()) { 
			}
			else if(a == result.size()){
				for (int j = b; j < currList.size(); j++) {
					tempList.add(currList.get(j));
				}
			}
			else if (b == currList.size()) {
				for (int j = a; j < result.size(); j++) {
					tempList.add(result.get(j));
				}
			}	
			result = tempList;				
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
