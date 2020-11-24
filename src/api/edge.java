package api;

public class edge implements edge_data {
    private node_data src;
    private node_data dest;
    private double weight;
    private String info;
    private int tag;

    public edge(node_data s, node_data d, double w){
        this.src = s;
        this.dest = d;
        this.weight = w;
        this.info = "White";
        this.tag = -1;
    }

    @Override
    public int getSrc() {
        return this.src.getKey();
    }

    @Override
    public int getDest() {
        return this.dest.getKey();
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    @Override
    public String toString() {
        return src + "--" + this.weight + "-->" + dest;
    }
}
