package edu.csulb;

import static org.junit.Assert.assertEquals;

import java.nio.file.Paths;

import org.junit.Test;

import java.util.PriorityQueue;

import cecs429.documents.DirectoryCorpus;
import cecs429.documents.DocumentCorpus;
import cecs429.indexes.DiskPositionalIndex;
import cecs429.indexes.Indexer;
import cecs429.queries.Accumulator;

public class TestRank {

    //@Test
    public double rankInput(String query){
        String path = "/Users/berry/Desktop/CECS429/testCorpus2";
        DocumentCorpus corpusR = DirectoryCorpus.loadTextDirectory(Paths.get(path).toAbsolutePath());
        DiskPositionalIndex d2 = new DiskPositionalIndex(path);
        PriorityQueue<Accumulator> res = Indexer.userRankedQueryInput(corpusR,d2,query);
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
        rankInput("sites");
        //assertEquals(expected, actual);
    }
    @Test
    public void threeWordTest(){
        rankInput("sites");
        //assertEquals(expected, actual);
    }
    //test size accumulator >0
    //test other rankings
}
