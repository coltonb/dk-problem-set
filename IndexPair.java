public class IndexPair {
    private int l;
    private int r;

    public IndexPair(int _l, int _r) {
        l = _l;
        r = _r;
    }

    public int getStart() {
        return l;
    }

    public int getEnd() {
        return r;
    }

    public void setStart(int _l) {
        l = _l;
    }
    
    public void setEnd(int _r) {
        r = _r;
    }

    public String toString() {
        return "(" + l + "," + r + ")";
    }
}