package api;

import java.util.Objects;

/**
 * This class is an implementation of node_data interface.
 * node class implement set of operations applicable on a
 * node (vertex) in a (directional) weighted graph.
 * @author itai.lashover and liav.weiss
 *
 */
public class node implements node_data,Comparable<node_data> {
    /**
     * Each node contains few fields:
     * location: An object that represent the location of the node by 3d point.
     * weight: A variable that is used in later functions, by default Initialized to Integer.MAX_VALUE(infinite).
     * info: A variable that is used in later functions, by default Initialized to "White".
     * tag: A variable that is used in later functions, by default Initialized to -1.
     * key: A unique key that is used as each node's ID.
     */
    private int key;
    private geo_location location;
    private double weight = Double.MAX_VALUE;
    private String info = "White";
    private int tag = -1;
    private static int uniqueKey = 0;

    /**
     * Constructor.
     * @param l - geo_Location.
     */
    public node(geo_location l){
        this.key = uniqueKey++;
        this.location = l;
    }

    /**
     * Deep copy constructor.
     * @param other - node_gata.
     */
    public node(node_data other) {
        this.key = other.getKey();
        this.location = new geoLocation(other.getLocation());
        this.weight = other.getWeight();
        this.info = other.getInfo();
        this.tag = other.getTag();
    }

    /**
     * Constructor by variables.
     * NOTE:using this constructor will not give this node a unique key.
     * Used for reading a node from a JSON file.
     * @param key - id
     * @param location - point3d
     * @param weight - weight
     * @param info - metadata
     * @param tag - tag
     */
    public node(int key, geo_location location, double weight, String info, int tag) {
        this.key = key;
        this.location = location;
        this.weight = weight;
        this.info = info;
        this.tag = tag;

    }

    /**
     * Returns the key (id) associated with this node.
     * @return key
     */
    @Override
    public int getKey() {
        return this.key;
    }

    /** Returns the location of this node, if none return null.
     * @return the location of this node.
     */
    @Override
    public geo_location getLocation() {
        return this.location;
    }

    /** Allows changing this node's location.
     * @param p - new location (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        this.location = new geoLocation(p.x(),p.y(),p.z());
    }

    /**
     * Returns the weight associated with this node.
     * @return weight.
     */
    @Override
    public double getWeight() {
        return this.weight;
    }

    /**
     * Allows changing this node's weight.
     * @param w - the new weight.
     */
    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     * @return this node info.
     */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     * @param s - the new info.
     */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     * @return this node tag.
     */
    @Override
    public int getTag() {
        return this.tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    /**
     * toString method.
     * @return node as String.
     */
    @Override
    public String toString() {
        return "["+key+"]";
    }

    /**
     * This method override on compareTo in order to compare two nodes just by their weight.
     */
    @Override
    public int compareTo(node_data o) {
        node_data n = this;
        return Double.compare(n.getWeight(), o.getWeight());
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
        node node = (node) o;
        return key == node.key &&
                Double.compare(node.weight, weight) == 0 &&
                tag == node.tag &&
                location.equals(node.location) &&
                info.equals(node.info);
    }

    /**
     * Override hashcode because equals changed.
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key, location, weight, info, tag);
    }
}