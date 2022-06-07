package cecs429.text;

import org.tartarus.snowball.ext.englishStemmer;

import java.util.ArrayList;

public class AdvancedTokenProcessor implements TokenProcessor{
    
    @Override
    public ArrayList<String> processToken(String token){
        englishStemmer stemmer = new englishStemmer();
        ArrayList<String> list = new ArrayList<String>();
        token = token.replaceAll("^\\W+|\\W+$", ""); //start-end delete non-alphanum characters
        token = token.replaceAll("\"", ""); //replaces quotes
        token = token.replaceAll("'", "").toLowerCase();//remove,apostrophes, then lowercase 
        
        if (token.contains("-")){
            String[] tokenList = token.split("-");//in hyphens add term, split by hyphen and full word without
            for(String s: tokenList){ //loop tokenslist hyphens
                if(s.length()>0){
                     list.add(processToken(s).get(0));//recusively add modified token 
                }
            }
            token.replaceAll("-", ""); //replace hyphens 
        }
        list.add(token);//finally add modified token
        for(int i = 0; i < list.size(); i++){ //stem tokens in list
            stemmer.setCurrent(list.get(i)); //curr stem
            stemmer.stem(); //stem process
            list.set(i,stemmer.getCurrent()); //set i in list to stemmed
            list.set(i,list.get(i)); //update
        }
        return list;
    }
    /**
     * stem a single token using the Porter2stemmer method
     * @param token the token to be stemmed
     * @return the stemmed token
     */
    public static String stemToken(String token) {

        String stemmedTerm = "";

        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(token);
        if (stemmer.stem()) {
            stemmedTerm = stemmer.getCurrent();
        }

        return stemmedTerm;

    }
}
