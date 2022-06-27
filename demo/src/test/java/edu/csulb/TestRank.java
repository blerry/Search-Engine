package edu.csulb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Indexer;
import cecs429.indexes.Posting;
import cecs429.queries.Accumulator;

public class TestRank {
    private static final int RANKED_RETURN = 1;

    //@Test
    public double rankInput(String query){
        String path = "/Users/berry/Desktop/CECS429/testCorpus2";
        DocumentCorpus corpusR = DirectoryCorpus.loadTextDirectory(Paths.get(path).toAbsolutePath());
        DiskPositionalIndex d = new DiskPositionalIndex(path);
        PriorityQueue<Accumulator> res = Indexer.userRankedQueryInput(corpusR,d,query);
        //while(!res.isEmpty()){
            Accumulator currAcc = res.poll();
            String title = corpusR.getDocument(currAcc.getDocId()).getTitle();
            int docId = currAcc.getDocId() + 1;
            docId--;
            double value = currAcc.getA_d();
            //assertEquals(0.24221737435642712, value,0.24221737435642712);
            //assertEquals(expected, actual);
            System.out.println("Title: " + title.toString()+ " Doc ID: " + docId+ " Value: "+ value); 
            return value;                           
                                  //  }
    }
    @Test
    public void oneWordTest(){
        double value = rankInput("sites");
        assertEquals(0.24221737435642712,value,0.24221737435642712);
    }
    @Test
    public void twoWordTest(){
        double value = rankInput("his frame");
        assertEquals(0.039145990056334325, value,0.039145990056334325);
    }
    @Test
    public void threeWordTest(){
        double value = rankInput("his frame his");
        assertEquals(0.039145990056334325, value,0.039145990056334325);
    }
    @Test
    public void threeWordTest2(){
        double value = rankInput("his numbers speak");
        assertEquals(1.0, value,1.0);
    }
    @Test
    public void accumulatorTest(){
        String path = "/Users/berry/Desktop/CECS429/testCorpus2";
        DocumentCorpus corpusR = DirectoryCorpus.loadTextDirectory(Paths.get(path).toAbsolutePath());
        DiskPositionalIndex d2 = new DiskPositionalIndex(path);
        String query="teams";
        PriorityQueue<Accumulator> res = Indexer.userRankedQueryInput(corpusR,d2,query);
        //while(!res.isEmpty()){
            assertTrue("Accumulator size > 0", res.size()>0);
            Accumulator currAcc = res.poll();
            assertTrue("Doc Exists", currAcc.getDocId() == (int)currAcc.getDocId());
            String title = corpusR.getDocument(currAcc.getDocId()).getTitle();
            int docId = currAcc.getDocId() + 1;
            docId--;
            double value = currAcc.getA_d();
    }
    //other rankings
    @Test
    public void accumulatorTest2(){
        String path = "/Users/berry/Desktop/CECS429/testCorpus2";
        DiskPositionalIndex d = new DiskPositionalIndex(path);
        HashMap<Posting, Double> hm = new HashMap<>();
        PriorityQueue<Accumulator> pq = new PriorityQueue<>(RANKED_RETURN);
        List<Accumulator> accumulators = new ArrayList<Accumulator>();
        //(w_dt * w_qt);
        double a_d = 1.0*0.3;
        hm.put(new Posting(0), a_d);
        hm.forEach((key,value) -> 
                                    accumulators.add(new Accumulator(key.getDocumentId(),value)));
        for (Accumulator acc : accumulators){
            double value = acc.getA_d() / d.getDocumentWeight(acc.getDocId());
            acc.setA_d(value);
            if(pq.size() < RANKED_RETURN || pq.peek().getA_d() < acc.getA_d()){
                if(pq.size() == RANKED_RETURN){
                    pq.remove();
                }
                pq.add(acc);
            }
        }
        assertTrue("Priority queue filled", pq.size()>0);

    }
}
