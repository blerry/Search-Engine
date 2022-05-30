package cecs429.queries;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import cecs429.indexes.Index;
import cecs429.indexes.Posting;
//import cecs429.queries.QueryComponent;

/**
 * Represents a phrase literal consisting of one or more terms that must occur in sequence.
 */
public class PhraseLiteral implements QueryComponent {
	// The list of individual terms in the phrase. Changed to Query
	//private List<String> mTerms = new ArrayList<>();
	private List<QueryComponent> mTerms = new ArrayList<>();
	private boolean isPos;
	/**
	 * Constructs a PhraseLiteral with the given individual phrase terms.
	 */
	//public PhraseLiteral(List<String> terms)
	public PhraseLiteral(List<QueryComponent> terms) {
		isPos = true; //flag true
		for(QueryComponent q:terms){ //Look through terms and add to children list (mterms)
			mTerms.add(q);
		}
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
		//Retrieve the postings for the individual terms in the phrase,
		// and positional merge them together.
		List<Posting> results = new ArrayList<Posting>();//post list
		results = mTerms.get(0).getPostings(index); //post list is 1st element
		
		List<Posting> currList = new ArrayList<Posting>();//only for current Postings

		//For loop + while + while loop
		for(int i = 1; i<mTerms.size(); i++){
			//using a and b as pointers for matching literals
			List<Posting> tempList = new ArrayList<Posting>();
			//currList will be used to find a match with other term
			currList = mTerms.get(i).getPostings(index);//Find postings for current index
			int a = 0, b = 0;//counts
			Posting pA = results.get(a); 
			Posting pB = currList.get(b);
			int docA = pA.getDocumentId();
			int docB =  pB.getDocumentId();

			while(a< results.size() && b <currList.size()){//Loop through both lists
				//We are looking for a match by moving both a and b
				pA = results.get(a);
				pB = currList.get(b);
				docA = pA.getDocumentId();
				docB = pB.getDocumentId();

				if(docA == docB){//iterate postings if match
					int p1 = 0; //position 1
					int p2 = 0; // 2
					//Find positions
					ArrayList<Integer> posA = pA.getPostions();
					ArrayList<Integer> posB = pB.getPostions();
					Posting lastPosting = null;

					while(p1 < posA.size() && p2 < posB.size()){
						if(!(tempList.isEmpty())){ //base case
							lastPosting = tempList.get(tempList.size()-1);
						}
						if(posB.get(p2) == posA.get(p1)+i){
							//if Position B = Position A + i, Then there are many
							//So we add Posting A to TempList Postings
							if(tempList.isEmpty())
								tempList.add(pA);
							else if(lastPosting.getDocumentId()==docA)//if already in tempList we add the position of Doc Id
								lastPosting.addPosition(a);
							else 
								tempList.add(pA);//otherwise add Posting A
								p1++;//Posting A position incremented
								p2++;//Posting B
						}
						//Increase the min of the 2 positions if not equal
						else if(posA.get(p1) <= posB.get(p2)){
							p1++;
						}
						else{
							p2++;
						}
					}
					a++;//continue the counting
					b++;
				}//end match check (if)
				//If not match increment minimum of counts
				else{
					if(docA<=docB) a++;
					else b++;
				}
			}//end loop
			results =tempList; //update the list for phrase for search
		}
		return results;
	}
	
	@Override
	public String toString() {
		//return "\"" + String.join(" ", mTerms) + "\"";
		return "";
	}
	@Override
	public boolean isPositive() {
		return isPos;
	}
	public void setNegative() {
		isPos = false;
	}
}
