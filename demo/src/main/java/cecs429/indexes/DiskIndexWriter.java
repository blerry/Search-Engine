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
/**
 * Disk IndexWriter will write an index to disk at the corpusPath/index folder
 * With the following files, postings.bin, docWeights.bin, index.db
 * @param index
 *  Includes the index that will need to be written for (positional inverted index)
 */
public class DiskIndexWriter {
    /**
     * Max address for index on disk is 8 bytes
     * Each file 4 bytes in the format:
     * term 1 frequency / firstDocumentID / total positions / 1stPosition / 2ndPosition /...
     */
	public ArrayList<Long> writeIndex(Index index, String path) throws IOException {
        File directory = new File(path + "/index");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //create B+ tree for terms and addresses
        DB db = DBMaker.fileDB(path + "/index/index.db").make();
        BTreeMap<String, Long> bTreeMap = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.LONG).counterEnable().createOrOpen();
        //create a postings binary file 
        ArrayList<Long> wordAddresses = new ArrayList<>();
        List<String> words = index.getVocabulary(); //get the sorted vocabulary list from the index

        try (DataOutputStream dout = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(path+"/index/postings.bin")))) {
            for (int i = 0; i < words.size(); i++) {//iterate through words in index
                bTreeMap.put(words.get(i), (long)dout.size()); //save in B+ tree
                wordAddresses.add((long)dout.size());
                if (index.getPostings(words.get(i)) == null) {//check term exists
                    dout.writeInt(0);//term is in no documents
                } else {//
                    int postingsSize = index.getPostings(words.get(i)).size();
                    dout.writeInt(postingsSize); //Dft
                    List<Posting> postings = index.getPostings(words.get(i));
                    int termFrequency = 0; //term frequency among documents
                    for (int j = 0; j < postings.size(); j++)  {
                        termFrequency += postings.get(j).getPostions().size();
                    }
                    dout.writeInt(termFrequency);//store term frequency among documents
                    int prevDocId = 0;
                    for (int j = 0; j < postingsSize; j++) {//iterate through every document with this term
                        int currDocId = index.getPostings(words.get(i)).get(j).getDocumentId();//gets document id
                        dout.writeInt(currDocId - prevDocId);//store the gap between document id's
                        //term frequency within a document
                        int Dft = index.getPostings(words.get(i)).get(j).getPostions().size();
                        //DSP?
                        //dout.writeDouble(postings.getWDT(words.get(i)));
                        //double w_dt = 1.0 + Math.Log((double)termDocumentFrequency);
                        //dout.writeDouble(w_dt);
                        dout.writeInt(Dft);//tf_td store term frequency in document
                        int prevTermPosition = 0;
                        for (int k = 0; k < Dft; k++) {//loop through all terms in doc
                            int termPosition = index.getPostings(words.get(i)).get(j).getPostions().get(k);//get term pos in doc
                            dout.writeInt(termPosition - prevTermPosition);//store each gap between positions
                            prevTermPosition = termPosition;//store previous position
                        }
                        prevDocId = currDocId;//store previous document
                    }//proceed to next document

                }

            }//proceed to next term

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        db.close();
        return wordAddresses;

    }

    public void writeDocumentWeights(ArrayList<Double> documentWeights, String path) {
        File directory = new File(path + "/index");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        //create docWeights.bin file to act as index on disk
        try (DataOutputStream dout = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(path + "/index/docWeights.bin")))) {
            for (Double documentWeight : documentWeights) {//iterate through every document weight in doc id order
                dout.writeDouble(documentWeight);//8-byte double written to disk as weight.
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
