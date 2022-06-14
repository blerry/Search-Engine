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
            int postingsSize = raf.readInt();//collect how many documents the term appears in
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
                            post = new Posting(docId);//create new posting
                            post.addPosition(position);//add position to posting
                        } else {
                            post.addPosition(position);//add position to posting
                        }
                    }
                } else {//create posting without positions
                    //each position represents 4 bytes so (* 4) to account for this offset
                    raf.seek(raf.getFilePointer() + (totalPositions * 4));//skip positions bytes
                    post = new Posting(docId);//create new posting
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

        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/index/docWeights.bin", "r")) {

            raf.seek(docId * 8);//double needs 8-byte offset
            return raf.readDouble();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return -1;

    }
    @Override
    public List<Posting> getPostings(String term){
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
    public List<Posting> getPostingsPositions(String term){
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

        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/index/postings.bin", "r")) {

            if (getKeyTermAddress(term) == -1) {
                return termFrequency;
            } else {
                raf.seek(getKeyTermAddress(term));
            }

            raf.readInt();//consume postings size
            termFrequency = raf.readInt();//collect how many documents the term appears in

        }  catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return termFrequency;

    }
    public int getDocumentFrequencyOfTerm(String term) {

        int df_t = -1;
        try (RandomAccessFile raf = new RandomAccessFile(indexLocation + "/index/postings.bin", "r")) {

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
}
