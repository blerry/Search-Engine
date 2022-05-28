package cecs429.text;

import java.util.ArrayList;

public class AdvancedTokenProcessor implements TokenProcessor{
    @Override
    public ArrayList<String> processToken(String token){
        ArrayList<String> list = new ArrayList<String>();
        token = token.replaceAll("^\\W+|\\W+$", ""); //start-end delete non-alphanum characters
        return list;
    }
}
