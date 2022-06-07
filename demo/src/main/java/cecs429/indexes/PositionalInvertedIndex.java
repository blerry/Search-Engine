package cecs429.indexes;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;
import cecs429.text.AdvancedTokenProcessor;

public class PositionalInvertedIndex implements Index{
    /**
	 * Constructs an empty index with with given vocabulary set and corpus size.
	 * @param vocabulary a collection of all terms in the corpus vocabulary.
	 * @param corpuseSize the number of documents in the corpus.
	 */
    private HashMap<String, List<Posting>>map = new HashMap<String, List<Posting>>();

    @Override
    public List<Posting> getPostings(String term){
        //return list of postings in index
        //KEY might not exist
        if(map.containsKey(term)){
            //process token for valid characters
			AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
			String stemmed = processor.stemToken(term);
			return map.get(stemmed);
        }
        else{
            return new ArrayList<Posting>();//return empty list if no posting
        }
    }
    @Override
    public List<String> getVocabulary(){
        //return lists of strings in vocabulary the keys of Hasmap
        Set<String> keys = map.keySet();//not really a list, sort arraylist then return it
        List<String> vocabulary = new ArrayList<String>();
        for(String s: keys){
            //if(isStringAlphabetic(s)){
                vocabulary.add(s);
                //Sort alphabetically
            //vocabulary.add(s);
           // }
        }
        Collections.sort(vocabulary);
        return Collections.unmodifiableList(vocabulary);
        //return null;
    }
    public boolean isStringAlphabetic(String s){
        for(int i = 0; i < s.length(); i++){
            if(!Character.isLetter(s.charAt(i))){
                return false;
            };
        }
        return true;
    }
    public void addTerm(List<String> terms, int id, int position) {

		for (String term : terms) {//iterate through every term given

			List<Posting> postings = map.get(term);//find list of postings for the term

			//postings don't exist for term
			if (postings == null) {

				postings = createPosting(id, position);//create a new posting with docid, position
				map.put(term, postings);//add new posting and term to index

			} else {//build from existing posting list

				//previous document id within the postings list
				int prevDocId = postings.get(postings.size()-1).getDocumentId();
				//this document hasn't been recorded yet
				if (id > prevDocId) {
					Posting posting = new Posting(id);//add the new document id to the list
					posting.addPosition(position);
					postings.add(posting);//update postings with new posting
				//this document exists, add new position
				} else if (id == prevDocId) {
					postings.get(postings.size()-1).addPosition(position);//update postings with new position
				}

			}

		}

	}
    /**
	 * Create a new posting list object for the index
	 * @param id document id associated with the new posting
	 * @param position term position to store in the new posting
	 * @return a new posting list object
	 */
	private List<Posting> createPosting(int id, int position) {
		List<Posting> postings = new ArrayList<>();
		Posting posting = new Posting(id);//create a new posting
		posting.addPosition(position);
		postings.add(posting);
		return postings;
	}
    @Override
	public List<Posting> getPostingsPositions(String token) {
		//process token for valid characters
		AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
		String stemmed = processor.stemToken(token);
		return map.get(stemmed);//index
	}
}