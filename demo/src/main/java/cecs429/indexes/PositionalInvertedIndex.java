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
            return map.get(term);
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
    public void addTerm(String term, int docId, int position){
        //someone gives me "hello, docID 5"
        List<Posting> exists = map.get(term);//exists is posting list
        //exists.add(new Posting(docId)); //mutable list in HasMap is same List
        //Check if it exists first, if a term shows up more than once dont add it again
        // Hello -> 1 could show up 12 times.
        //if exists it could already contain id in list
        if(map.containsKey(term)){
            if(exists.get(exists.size()-1).getDocumentId() != docId){ //if not the last document 
                List<Posting> list = getPostings(term); //posting list
                ArrayList<Integer> posList = new ArrayList<>(); //position list
                posList.add(position);
                list.add(new Posting(docId,posList));
                map.put(term,list);
            }
            else{
                List<Posting> list = getPostings(term);
                    Posting p = list.get(list.size() - 1);
                    p.addPosition(position);
            }

        }
        else{
            List<Posting> list = new ArrayList<Posting>();
			ArrayList<Integer> posList = new ArrayList<Integer>();
			posList.add(position);
			list.add(new Posting(docId,posList));
			map.put(term, list);
        }
    }
    @Override
	public List<Posting> getPostingsPositions(String token) {
		//process token for valid characters
		AdvancedTokenProcessor processor = new AdvancedTokenProcessor();
		String stemmed = AdvancedTokenProcessor.stemToken(token);
		return this.map.get(stemmed);//index
	}
}