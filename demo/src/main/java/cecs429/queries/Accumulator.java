package cecs429.queries;
/*
 * //Adding a Class Accumlator because we need this when
 * dealing with the doc id and the type of Ad accumulator
 */
public class Accumulator implements Comparable<Accumulator> {
    private int docId;//carry the doc ID the accumulator corresponds to
    private double A_d;//value of 1 accumulator after wqtxwdt
    public Accumulator(int id, double ad) {
        this.docId = id;
        this.A_d = ad;
    }
    public int getDocId() {
        return docId;
    }
    public double getA_d() {
        return A_d;
    }
    public void setA_d(double a_d) {
        A_d = a_d;
    }
    public void setDocId(int docId) {
        this.docId = docId;
    }
    @Override
    /*
     * Compare between accumulator values
     */
    public int compareTo(Accumulator acc) {
        if(this.A_d < acc.getA_d()){
            return -1;
        }
        else if (acc.getA_d() < this.A_d){
            return 1;
        }
        return 0;
    }
}
