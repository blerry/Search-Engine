package cecs429.indexes;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.util.ArrayList;

import cecs429.text.AdvancedTokenProcessor;
import cecs429.indexes.Index;
import cecs429.indexes.Posting;

public class DiskPositionalIndex implements Index{
    
        String indexLocation;
        DB diskIndex = null;
        BTreeMap<String, Long> map = null;
        public DiskPositionalIndex(String dir) {
            indexLocation = dir + "/index";
            try {
                //B+ Tree
                diskIndex = DBMaker.fileDB(indexLocation + "/index.db").make();
                map = diskIndex.treeMap("map")
                    .keySerializer(Serializer.STRING)
                    .valueSerializer(Serializer.LONG)
                    .counterEnable()
                    .open();
            } catch (Exception e) {
                System.out.println("Could not find B+ Tree on disk...");
                e.printStackTrace();
            }
        }
    
    public List<Posting> accessTermData(long address, boolean withPositions) {

        List<Posting> postings = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/postings.bin", "r")) {

            raf.seek(address);//skip to the terms address
            int postingsSize = raf.readInt();//Dft collect how many documents the term appears in
            int termFrequency = raf.readInt();
            int docId = 0;
            for (int i = 0; i < postingsSize; i++) {//iterate through every document associated with the term
                docId += raf.readInt();//collect next docId
                int totalPositions = raf.readInt();//collect term frequency in the document
                Posting post = null;//store a posting
                if (withPositions) {//create posting with Positions included
                    int position = 0;
                    for (int j = 0; j < totalPositions; j++) {//iterate through term frequency in the document
                        position += raf.readInt();//read single position
                        if (post == null) {//if posting doesn't exist yet
                            post = new Posting(docId,new ArrayList<Integer>());//create new posting, position
                            post.addPosition(position);//add position to posting
                        } else {
                            post.addPosition(position);//add position to posting
                        }
                    }
                } else {//create posting without positions
                    //each position represents 4 bytes so (* 4) to account for this offset
//DSP?
                    raf.seek(raf.getFilePointer() + (totalPositions * 4));//skip positions bytes
                    post = new Posting(docId);//create new posting
                    double tf_td = (double)totalPositions;//tf_td
                    //double wdt = getWDTFromDisk(term);//call
                    //post.setWDT(wdt); //If written on disk already
                    double wdt = 1.0 + Math.log(tf_td);//save in posting for ranks
                    post.setWDT(wdt);//keep this line
                }

                postings.add(post);//add new post to postings list
                
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        if (postings.size() == 0) {
            return null;
        }
        return postings;
    }
    public long getKeyTermAddress(String term) {
        if (map.get(term) == null) {
            return -1;
        } else {
            return map.get(term);
        }
    }
    public double getDocumentWeight(int docId) {

        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/docWeights.bin", "r")) {

            raf.seek(docId * 8);//double needs 8-byte offset
            //check if doc starts at 0
            return raf.readDouble();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return -1;

    }
    @Override
    public List<Posting> getPostings(String term){//ranked
        List<Posting> result = new ArrayList<>();
        String stemmedTerm = AdvancedTokenProcessor.stemToken(term);

        if (getKeyTermAddress(stemmedTerm) != -1) {//term doesn't exist
            if (accessTermData(getKeyTermAddress(stemmedTerm), false) != null) {
                result.addAll(accessTermData(getKeyTermAddress(stemmedTerm), false));
            }
        }

        return result;
    }
    @Override
    public List<Posting> getPostingsPositions(String term){//boolean
        List<Posting> result = new ArrayList<>();
        String stemmedTerm = AdvancedTokenProcessor.stemToken(term);

        if (getKeyTermAddress(stemmedTerm) != -1) {//term doesn't exist
            if (accessTermData(getKeyTermAddress(stemmedTerm), true) != null) {
                result.addAll(accessTermData(getKeyTermAddress(stemmedTerm), true));
            }
        }

        return result;
    }
    @Override
    public List<String> getVocabulary(){
        Iterator<String> iterator = map.getKeys().iterator();
        List<String> vocab = new ArrayList<>();
        while (iterator.hasNext()) {
            vocab.add(iterator.next());
        }
        return vocab;
    }
    public int getTermFrequency(String term) {

        int termFrequency = -1;
//double wdt = -1;

        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/postings.bin", "r")) {
//raf.seek(getwdt)? possible add? New Function?
            if (getKeyTermAddress(term) == -1) {
                return termFrequency;
            } else {
                raf.seek(getKeyTermAddress(term));
            }
            //raf.readDouble(); //Possible add. Read wdt before tf?
            raf.readInt();//consume postings size

            termFrequency = raf.readInt();//collect how many documents the term appears in

        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return termFrequency;

    }
    public int getDocumentFrequencyOfTerm(String term) {

        int df_t = -1;
        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/postings.bin", "r")) {

            if (getKeyTermAddress(term) == -1) {
                return df_t;
            } else {
                raf.seek(getKeyTermAddress(term));
            }

            df_t = raf.readInt();//collect how many documents the term appears in

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return df_t;

    }

    //Get Weight Term Frequency of Document function here
    //pubic double getWDTFromDisk(String term)
    //double wdt = -1.0;
    //if(getKeyTermAddress(term) == -1 ){

    //}
    //else{
    //raf.seek(getKeyTermAddress(term));
    //double wdt = raf.readDouble()//save in posting for ranks
}
