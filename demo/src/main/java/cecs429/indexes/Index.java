package cecs429.indexes;

import java.util.List;

/**
 * An Index can retrieve postings for a term from a data structure associating terms and the documents
 * that contain them.
 */
public interface Index {
	/**
	 * Retrieves a list of Postings of documents that contain the given term.
	 * Without positions, will be required for rank
	 */
	List<Posting> getPostings(String term);
	/**
	 * A (sorted) list of all terms in the index vocabulary
	 */
	List<Posting> getPostingsPositions(String term);
	/**
	 * A (sorted) list of all terms in the index vocabulary with positions.
	 * Will be required for a boolean phrase
	 */
	List<String> getVocabulary();
	/**
	 * A (sorted) list of all terms in the index vocabulary.
	 */
	int getTermFrequency(String term);
	/**
	 * Get Term frequency a term in disk.
	 */
	double getDocumentWeight(int docId);
	/**
	 * Get the length of the document accoridng to rank retrieval
	 */
	int getDocumentFrequencyOfTerm(String term);
}
