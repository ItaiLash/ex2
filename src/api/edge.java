package api;

import java.util.Objects;

/**
 * This class is an implementation of edge_data interface.
 * edge class implement a set of operations applicable on a
 * directional edge(src,dest) in a (directional) weighted graph.
 * @author itai.lashover&liav.weiss
 *
 */
public class edge implements edge_data {

    /**
     * Each edge contains few fields:
     * src: A variable that represent the id of the source node of this edge.
     * dest: A variable that represent the id of the destination node of this edge.
     * w: A variable represent this edge weight (positive value).
     * info: A variable represent this edge remark (meta data).
     * tag: A variable represent temporal data.
     */
    private int src;
    private int dest;
    private double w;
    private String info;
    private int tag;

    /**
     * Constructor by variables.
     * @param s - source node id.
     * @param d - destination  node id.
     * @param w - weight.
     */
    public edge(int s, int d, double w){
        this.src = s;
        this.dest = d;
        this.w = w;
        this.info = "White";
        this.tag = -1;
    }

    /**
     * Deep copy constructor.
     * @param other - edge_data.
     */
    public edge(edge_data other){
        edge e = (edge)other;
        this.src = e.src;
        this.dest = e.dest;
        this.w = other.getWeight();
        this.info = other.getInfo();
        this.tag = other.getTag();
    }

    /**
     * Returns the id of the source node of this edge.
     * @return source node id.
     */
    @Override
    public int getSrc() {
        return this.src;
    }

    /**
     * Returns the id of the destination node of this edge.
     * @return destination node id.
     */
    @Override
    public int getDest() {
        return this.dest;
    }

    /**
     * Returns the weight of this edge (positive value).
     * @return this edge weight.
     */
    @Override
    public double getWeight() {
        return this.w;
    }

    /**
     * Returns the remark (meta data) associated with this edge.
     * @return this edge info.
     */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Allows changing the remark (meta data) associated with this edge.
     * @param s - new info.
     */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     * @return this edge tag.
     */
    @Override
    public int getTag() {
        return this.tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an edge - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag.
     */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    /**
     * toString method.
     * @return edge as String.
     */
    @Override
    public String toString() {
        return  +src + "--" + this.w + "-->" + dest;
    }

    /**
     * Returns true if all of the arguments are equal to each other and false otherwise.
     * Consequently, if both arguments are null, true is returned
     * and if exactly one argument is null, false is returned.
     * Otherwise, equality is determined by comparing all the fields of the object.
     * @param o - an Object.
     * @return true if the arguments are equal to each other and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        edge edge = (edge) o;
        return Double.compare(edge.w, w) == 0 &&
                tag == edge.tag &&
                src == edge.src  &&
                dest == edge.dest &&
                info.equals(edge.info);
    }

    /**
     * Override hashcode because equals changed.
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(src, dest, w, info, tag);
    }
}
