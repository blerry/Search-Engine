package cecs429.indexes;

import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class DiskIndexWriter {

    private void createIndexFolder(String indexLocation) {

        //create an index folder in the corpus
        File directory = new File(indexLocation + "/index");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

	public ArrayList<Long> writeIndex(Index index, String indexLocation) throws IOException {
        createIndexFolder(indexLocation); //create an index folder in the corpus
        //create B+ tree for terms and addresses
        DB db = DBMaker.fileDB(indexLocation + "/index/index.db").make();
        BTreeMap<String, Long> map = db.treeMap("map")
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.LONG)
                .counterEnable()
                .createOrOpen();
        //create postings.bin file to act as index on disk. max address is 8-bytes = 64-bits

        //Every file(4Bytes) Format: term 1 frequency / firstDocumentID / total positions / 1stPosition / 2ndPosition /...

        ArrayList<Long> wordAddresses = new ArrayList<>();
        List<String> words = index.getVocabulary(); //Retrieve the sorted vocabulary list from the index

        try (DataOutputStream dout = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(indexLocation+"/index/postings.bin")))) {
            for (int i = 0; i < words.size(); i++) {//iterate through vocabulary of index
                map.put(words.get(i), (long)dout.size()); //store term and address in B+ tree
                //get current position stored as address for term
                wordAddresses.add((long)dout.size());
                //make sure the term exists
                if (index.getPostings(words.get(i)) == null) {
                    dout.writeInt(0);//term appears in 0 documents
                } else {//psize = 15, tfreq = 231
                    int postingsSize = index.getPostings(words.get(i)).size();
                    dout.writeInt(postingsSize);
                    List<Posting> postings = index.getPostings(words.get(i));
                    int termFrequency = 0; //term frequency among documents
                    for (int j = 0; j < postings.size(); j++) {
                        termFrequency += postings.get(j).getPostions().size();
                    }
                    dout.writeInt(termFrequency);//store term frequency among documents
                    int prevDocumentId = 0;
                    for (int j = 0; j < postingsSize; j++) {//iterate through every document with this term
                        int documentId = index.getPostings(words.get(i)).get(j).getDocumentId();//gets document id
                        dout.writeInt(documentId - prevDocumentId);//store the gap between document id's
                        //term frequency within a document
                        int termDocumentFrequency = index.getPostings(words.get(i)).get(j).getPostions().size();
                        dout.writeInt(termDocumentFrequency);//store term frequency in document
                        int prevTermPosition = 0;
                        for (int k = 0; k < termDocumentFrequency; k++) {//iterate through all terms in a document
                            //gets a terms position within a document
                            int termPosition = index.getPostings(words.get(i)).get(j).getPostions().get(k);
                            dout.writeInt(termPosition - prevTermPosition);//store each gap between positions
                            prevTermPosition = termPosition;//store previous position
                        }
                        prevDocumentId = documentId;//store previous document
                    }//proceed to next document

                }

            }//proceed to next term

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        db.close();
        return wordAddresses;

    }

    public void writeDocumentWeights(ArrayList<Double> documentWeights, String indexLocation) {

        createIndexFolder(indexLocation);

        //create docWeights.bin file to act as index on disk
        try (DataOutputStream dout = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(indexLocation + "/index/docWeights.bin")))) {

            for (Double documentWeight : documentWeights) {//iterate through every document weight in doc id order

                dout.writeDouble(documentWeight);//write doc weight to disk (8-byte double)

            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
}
