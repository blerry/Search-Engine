package modules.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import modules.indexes.Index;
import modules.indexes.Posting;

/**
 * An OrQuery composes other QueryComponents and merges their postings with a union-type operation.
 */
public class OrQuery implements QueryComponent {
	// The components of the Or query.
	private List<QueryComponent> mComponents;//the children

	//public OrQuery(List<QueryComponent> components) {
	public OrQuery(Collection<QueryComponent> components){
		mComponents = new ArrayList<QueryComponent>(components);
	}
	@Override
	public List<Posting> getPostingsPositions(Index index) {
		return getPostings(index);
	}
	@Override
	public List<Posting> getPostings(Index index) {
		List<Posting> result = new ArrayList<Posting>();//post list
		result = mComponents.get(0).getPostings(index);//Set posting list to first element
		//List<Posting> currList = new ArrayList<Posting>();
		// program the merge for an OrQuery, by gathering the postings of the composed QueryComponents and
		// unioning the resulting postings.
		for(int i = 1; i< mComponents.size(); i++){
				//verify the next posting appears in at least 1 document
				//QueryComponent component = mComponents.get(i);
				//List<Posting> posting = component.getPostings(index);
				//int size = posting.size();
				if (mComponents.get(i).getPostings(index).size() != 0) {
					result = orMerge(mComponents.get(i).getPostings(index), result);
				}
			}
	
			// Done: program the merge for an OrQuery, by gathering the postings of the composed Query children and
			// unioning the resulting postings.
	
			return result;
		}
		private List<Posting> orMerge(List<Posting> firstPostings, List<Posting> secondPostings) {

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
					result.add(firstPostings.get(i));
					i++;//iterate first list
				} else {// second list docid is less than first lists docid
					result.add(secondPostings.get(j));
					j++;//iterate second list
				}
	
			}
	
			//include the rest of the first postings
			while (i < firstPostings.size()) {
				result.add(firstPostings.get(i));
				i++;
			}
	
			//include the rest of the second postings
			while (j < secondPostings.size()) {
				result.add(secondPostings.get(j));
				j++;
			}
	
			return result;
	
		}
	@Override
	public String toString() {
		// Returns a string of the form "[SUBQUERY] + [SUBQUERY] + [SUBQUERY]...n"
		return "(" +
		 String.join(" + ", mComponents.stream().map(c -> c.toString()).collect(Collectors.toList()))
		 + " )";
	}

}
