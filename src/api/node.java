package api;

import java.util.Objects;

/**
 * This class is an implementation of node_data interface.
 * node class implement set of operations applicable on a
 * node (vertex) in a (directional) weighted graph.
 * @author itai.lashover & liav.weiss
 *
 */
public class node implements node_data,Comparable<node> {
    /**
     * Each node contains few fields:
     * location: An object that represent the location of the node by 3d point.
     * ///weight:
     * ///info: A variable that is used in later functions, by default Initialized to "Blue".
     * ///tag: A variable that is used in later functions, by default Initialized to Integer.MAX_VALUE(infinite).
     * key: A unique key that is used as each node's ID.
     * dis: A variable that is used in later functions, by default Initialized to Integer.MAX_VALUE(infinite).
     * pre: A variable that is used in later functions, by default Initialized to null.
     */
    private int key;
    private geo_location location;
    private double weight;
    private String info;
    private int tag;
    private static int uniqueKey = 0;
    private double dis = Double.MAX_VALUE;
    private node_data pre = null;

    public node(geo_location l,double w){
        this.key = uniqueKey++;
        this.location = l;
        this.weight = w;
        this.tag = -1;
        this.info = "White";
    }

    public node(node_data other) {
        this.key = other.getKey();
        this.location = new geoLocation(other.getLocation());
        this.weight = other.getWeight();
        this.info = other.getInfo();
        this.tag = other.getTag();
    }

    public node(int key, geo_location location, double weight, String info, int tag, double dis, node_data pre) {
        this.key = key;
        this.location = location;
        this.weight = weight;
        this.info = info;
        this.tag = tag;
        this.dis = dis;
        this.pre = pre;
    }

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public geo_location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(geo_location p) {
        this.location = new geoLocation(p.x(),p.y(),p.z());
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;
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
        return "["+key+"]";
    }

    public double getDis() {
        return dis;
    }

    public void setDis(double dis) {
        this.dis = dis;
    }

    public node_data getPre() {
        return pre;
    }

    public void setPre(node_data pre) {
        this.pre = pre;
    }

    @Override
    public int compareTo(node o) {
        return Double.compare(this.getDis(), o.getDis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        node node = (node) o;
        return key == node.key &&
                Double.compare(node.weight, weight) == 0 &&
                tag == node.tag &&
                Double.compare(node.dis, dis) == 0 &&
                location.equals(node.location) &&
                info.equals(node.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, location, weight, info, tag, dis, pre);
    }
}
