/*
package cecs429.indexes;

import java.util.*;

//
// * Implements an Index using a term-document matrix. Requires knowing the full corpus vocabulary and number of documents
// * prior to construction.
 
public class TermDocumentIndex implements Index {
	private final boolean[][] mMatrix;
	private final List<String> mVocabulary;
	private int mCorpusSize;
	

	public TermDocumentIndex(Collection<String> vocabulary, int corpuseSize) {
		mMatrix = new boolean[vocabulary.size()][corpuseSize];
		mVocabulary = new ArrayList<String>();
		mVocabulary.addAll(vocabulary);
		mCorpusSize = corpuseSize/;
		
		Collections.sort(mVocabulary);

		
	}
	

	public void addTerm(String term, int documentId) {
		int vIndex = Collections.binarySearch(mVocabulary, term);
		if (vIndex >= 0) {
			mMatrix[vIndex][documentId] = true;
		}
	}
	
	@Override
	public List<Posting> getPostings(String term) {
		List<Posting> results = new ArrayList<>();
		int givenTerm = Collections.binarySearch(mVocabulary,term);
		for(int docID = 1; docID < mMatrix.length; docID++){
			if(mMatrix[givenTerm][docID] ==true){
				Posting post = new Posting(docID,new ArrayList<Integer>());
				results.add(post);
			}
		}
		// TODO: implement this method.
		// Binary search the mVocabulary array for the given term.
		// Walk down the mMatrix row for the term and collect the document IDs (column indices)
		// of the "true" entries.
		
		return results;
	}
	
	public List<String> getVocabulary() {
		return Collections.unmodifiableList(mVocabulary);
	}
}
*/