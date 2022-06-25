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

	public ArrayList<Long> writeIndex(Index index, String path) throws IOException {
        createFolder(path); //create an index folder in the corpus
        //create B+ tree for terms and addresses
        DB db = DBMaker.fileDB(path + "/index/index.db").make();
        BTreeMap<String, Long> bTreeMap = db.treeMap("map").keySerializer(Serializer.STRING).valueSerializer(Serializer.LONG).counterEnable().createOrOpen();
        //making a postings binary file 
        //It is an index on disk with a maximum address of 8-bytes = 64-bits
        //Every file(4Bytes) Format: term 1 frequency / firstDocumentID / total positions / 1stPosition / 2ndPosition /...

        ArrayList<Long> wordAddresses = new ArrayList<>();
        List<String> words = index.getVocabulary(); //Retrieve the sorted vocabulary list from the index

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

    public void writeLD(ArrayList<Double> documentWeights, String indexLocation) {
        createFolder(indexLocation);
        //create docWeights.bin file to act as index on disk
        try (DataOutputStream dout = new DataOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(indexLocation + "/index/docWeights.bin")))) {
            for (Double documentWeight : documentWeights) {//iterate through every document weight in doc id order
                dout.writeDouble(documentWeight);//8-byte double written to disk as weight.
            }

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void createFolder(String indexLocation) {
        //create folder called index, if it doesnt exist make one.
        File directory = new File(indexLocation + "/index");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
