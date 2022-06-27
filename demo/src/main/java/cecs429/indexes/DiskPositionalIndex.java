package cecs429.indexes;

import cecs429.text.AdvancedTokenProcessor;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
/**
 * Disk Positional Index will index a corpus from a disk read
 * where the disk is located in a index folder of the path "path/index"
 * Included are methods for retrieving term frequency and postings.
 */
public class DiskPositionalIndex implements Index{
        DB dIndex;
        String path;
        BTreeMap<String, Long> bTreeMap;
    /**
     * @param dir
     * required the location of the corpus with index folder
     */
    public DiskPositionalIndex(String dir) {
            dIndex = null;
            bTreeMap = null;
            path = dir + "/index";
            try {
                dIndex = DBMaker.fileDB(path + "/index.db").make(); //B+ Tree
                bTreeMap = dIndex.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.LONG).counterEnable().open();
            } catch (Exception e) {
                System.out.println("No B+ Tree");
                e.printStackTrace();
            }
        }
    public List<Posting> getTermPostings(long address, boolean withPositions) {
        List<Posting> postings = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(path + "/postings.bin", "r")) {
            raf.seek(address);//go to term address
            int postingsSize = raf.readInt();//Dft 
            int termFrequency = raf.readInt();//needs this read
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
                    //each position is 4 bytes so mulply by 4
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
        if (postings.size() == 0) {//no postings so null
            return null;
        }
        return postings;
    }
    public long getTermAddress(String term) {
        if (bTreeMap.get(term) == null) {
            return -1;
        } else {
            return bTreeMap.get(term);
        }
    }
    public double getDocumentWeight(int docId) {
        try (RandomAccessFile raf = new RandomAccessFile(path + "/docWeights.bin", "r")) {
            raf.seek(docId * 8);//double needs 8-byte offset
            //check if doc starts at 0
            return raf.readDouble();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return -1;//in case not found
    }
    @Override
    public List<Posting> getPostings(String term){//ranked
        List<Posting> result = new ArrayList<>();
        String stringTerm = AdvancedTokenProcessor.stemToken(term);
        if (getTermAddress(stringTerm) != -1) {//term doesn't exist
            if (getTermPostings(getTermAddress(stringTerm), false) != null) {
                result.addAll(getTermPostings(getTermAddress(stringTerm), false));
            }
        }
        return result;
    }
    @Override
    public List<Posting> getPostingsPositions(String term){//boolean
        List<Posting> result = new ArrayList<>();
        String stringTerm = AdvancedTokenProcessor.stemToken(term);
        if (getTermAddress(stringTerm) != -1) {//term doesn't exist
            if (getTermPostings(getTermAddress(stringTerm), true) != null) {
                result.addAll(getTermPostings(getTermAddress(stringTerm), true));
            }
        }
        return result;
    }
    @Override
    public List<String> getVocabulary(){
        List<String> vocab = new ArrayList<>();
        Iterator<String> it = bTreeMap.getKeys().iterator(); //use iterator for keys
        while (it.hasNext()) {
            vocab.add(it.next());
        }
        return vocab;
    }
    public int getTermFrequency(String term) {
        int termFrequency = -1;
//double wdt = -1;
        try (RandomAccessFile raf = new RandomAccessFile(path + "/postings.bin", "r")) {
//raf.seek(getwdt)? possible add? New Function?
            if (getTermAddress(term) == -1) {
                return termFrequency;
            } else {
                raf.seek(getTermAddress(term));
            }
            //raf.readDouble(); //Possible add. Read wdt before tf?
            raf.readInt();//consume postings size
            termFrequency = raf.readInt();//how many docs the term appears in
        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return termFrequency;

    }
    public int getDocumentFrequencyOfTerm(String term) {
        int df_t = -1;
        try (RandomAccessFile raf = new RandomAccessFile(path + "/postings.bin", "r")) {
            if (getTermAddress(term) == -1) {
                return df_t;
            } else {
                raf.seek(getTermAddress(term));
            }
            df_t = raf.readInt();//collect how many documents the term appears in
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return df_t;
    }
    //Attempted dsp
    //Get Weight Term Frequency of Document function here
    //pubic double getWDTFromDisk(String term)
    //double wdt = -1.0;
    //if(getKeyTermAddress(term) == -1 ){

    //}
    //else{
    //raf.seek(getKeyTermAddress(term));
    //double wdt = raf.readDouble()//save in posting for ranks
}
