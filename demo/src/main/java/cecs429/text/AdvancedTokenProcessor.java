package cecs429.text;

import org.tartarus.snowball.ext.englishStemmer;

import java.util.ArrayList;
import java.util.List;

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
    /**
     * determine if the character is an alpha-numeric character
     * @param c the character to inspect
     * @return whether the character is alpha-numeric or not
     */
    private static boolean isAlphanumeric(char c) {
        //checks chars exist within the range of letters and numbers via ascii
        if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a){
            return false;
        } else {
            return true;
        }
    }
}
