/*
package cecs429.indexes;

import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.Collections;

public class InvertedIndex implements Index{
    private HashMap<String, List<Posting>>map = new HashMap<String, List<Posting>>();

    @Override
    public List<Posting> getPostings(String term){
        //return list of postings in index
        //KEY might not exist
        if(map.containsKey(term)){
            return map.get(term);
        }
        else{
            return new ArrayList<Posting>();
        }
    }
    @Override
    public List<String> getVocabulary(){
        // Auto-generated method stub
        //return lists of strings in vocabulary the keys of Hasmap
        Set<String> keys = map.keySet();//not really a list, sort arraylist then return it
        List<String> vocabulary = new ArrayList<String>();
        for(String s: keys){
            vocabulary.add(s);
        }
        Collections.sort(vocabulary);
        return Collections.unmodifiableList(vocabulary);
        //return null;
    }

    public void addTerm(String term, int docId){
        //someone gives me "hello, docID 5"
        List<Posting> exists = map.get(term);//exists is posting list
        //exists.add(new Posting(docId)); //mutable list in HasMap is same List
        //Check if it exists first, if a term shows up more than once dont add it again
        // Hello -> 1 could show up 12 times.
        //if exists it could already contain id in list
        if(map.containsKey(term)){
            if(exists.get(exists.size()-1).getDocumentId() != docId){ //if not the last document 
                List<Posting> list = getPostings(term);
                list.add(new Posting(docId,new ArrayList<Integer>()));
                map.put(term,list);
            }
            else{
                List<Posting> list = getPostings(term);
                    Posting p = list.get(list.size() - 1);
                    //p.addPosition(position);
            }

        }
        else{
            List<Posting> list = new ArrayList<Posting>();
			//ArrayList<Integer> posList = new ArrayList<Integer>();
			//posList.add(position);
			list.add(new Posting(docId, new ArrayList<Integer>()));
			map.put(term, list);
        }
    }
}
*/