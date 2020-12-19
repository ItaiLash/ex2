package api;

import java.util.*;

/**
 * This class is an implementation of directed_weighted_graph interface.
 * DWGraph_DS class implement an directional weighted graph.
 * It support a large number of nodes (over 100,000).
 * This implementation based on HashMap data structure.
 * @author itai.lashover and liav.weiss
 */
public class DWGraph_DS implements directed_weighted_graph {

    /**
     * Each DWGraph_DS contains few fields:
     * nodes: HashMap data structure that represents the group of nodes of this graph.
     * edges: HashMap data structure that represents each node group of directed edges in this graph.
     * numOfNode: A variable that stored the amount of nodes in this graph.
     * numOfEdge: A variable that stored the amount of edges in this graph.
     * mc: Mode Count a variable that stored the amount of changes
     * (add node, remove node, add edge, remove edge) made in this graph.
     */
    private HashMap<Integer, node_data> nodes;
    private HashMap<Integer, HashMap<Integer, edge_data>> edges;
    private int numOfNodes;
    private int numOfEdges;
    private int modeCounter;

    /**
     * Default constructor.
     */
    public DWGraph_DS() {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.numOfEdges = 0;
        this.numOfNodes = 0;
        this.modeCounter = 0;
    }

    /**
     * Deep copy constructor.
     * It's build a new DWGraph_DS with the same numOfEdge and numOfNode.
     * Note: This constructor build new HashMaps but does not initialize them.
     * The constructor calls another methods "edgesDeepCopy" and "nodesDeepCopy" that initialize those HashMaps.
     * @param other - other graph that you want to duplicate.
     */
    public DWGraph_DS(directed_weighted_graph other) {
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        nodesDeepCopy(other, this.nodes);
        edgesDeepCopy(other, this.edges);
        this.numOfEdges = other.edgeSize();
        this.numOfNodes = other.nodeSize();
    }

    /**
     * This private method gets a graph and empty HashMap and initialize the HashMap to be a duplicate of his HashMap.
     * The method add a deep copy nodes to the new HashMap in the right keys.
     * Note: At the end of this method you get a graph with group of nodes but without edges.
     * Complexity: O(n) , |V|=n.
     * @param other - other graph that you want to duplicate his HashMap.
     * @param nodes - an empty HashMap.
     * @return nodes HashMap initialized.
     */
    private HashMap<Integer, node_data> nodesDeepCopy(directed_weighted_graph other, HashMap nodes) {
        HashMap<Integer, node_data> h = nodes;
        for (node_data n : other.getV()) {
            this.addNode(n);
        }
        return h;
    }

    /**
     * This private method gets a graph and empty HashMap and adds to this HashMap the same directed weighted edges.
     * Note: The method will used only after we have used the previous "nodesDeepCopy" method.
     * Thus in the beginning of the method we already have a HashMap with nodes.
     * All that is left is to connect the right nodes.
     * The method check which nodes connected in the other graph and connect them in this graph.
     * Complexity: O(n^2) , |V|=n.
     * @param other other graph that you want to duplicate his HashMap.
     * @param edges - an empty HashMap.
     * @return edges HashMap initialized.
     */
    private HashMap<Integer, HashMap<Integer, edge_data>> edgesDeepCopy(directed_weighted_graph other, HashMap edges) {
        HashMap<Integer, HashMap<Integer, edge_data>> h = edges;
        int key;
        for (node_data n : this.getV()) {
            key = n.getKey();
            for (edge_data e : other.getE(key)) {
                edge_data edge = new edge(e);
                this.edges.get(e.getSrc()).put(e.getDest(), edge);
            }
        }
        return h;
    }

    /**
     * Returns the node_data by the node_id,
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return this.nodes.get(key);
    }

    /**
     * Returns the edge (src,dest) between two nodes, null if none.
     * Complexity: this method run in O(1) time.
     * @param src - source node id.
     * @param dest - destination node id.
     * @return an egge, null if none.
     */
    @Override
    public edge_data getEdge(int src, int dest) {
        if (this.nodes.containsKey(src) && this.nodes.containsKey(dest)) {
            return this.edges.get(src).get(dest);
        }
        return null;
    }

    /**
     * Adds a new node to the graph with the given node_data.
     * This method simply do nothing if this graph already contains this node_data.
     * Complexity: this method run in O(1) time.
     * @param n - node_data
     */
    @Override
    public void addNode(node_data n) {
        if (nodes.containsKey(n.getKey())) {
            return;
        }
        nodes.put(n.getKey(), n);
        edges.put(n.getKey(), new HashMap<>());
        modeCounter++;
        numOfNodes++;
    }

    /**
     * Connects an edge with positive weight w between node src to node dest.
     * Complexity: this method run in O(1) time.
     * NOTE1: If there is already an edge between node src to node dest in this graph,
     * the method will update the weight according to the weight obtained.
     * NOTE2: This method simply do nothing if node src and node dest are the same node.
     * @param src - the source id's of the edge.
     * @param dest - the destination id's of the edge.
     * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
     */
    @Override
    public void connect(int src, int dest, double w) {
        if (w < 0) {
            throw new RuntimeException("The weight must be positive");
        }
        if (src == dest) {
            return;
        }

        if (this.getEdge(src, dest) != null && edges.get(src).get(dest).getWeight() != w) {
            edge_data e = new edge(src, dest, w);
            edges.get(src).put(dest, e);
            modeCounter++;
        } else if (nodes.containsKey(src) && nodes.containsKey(dest) && this.getEdge(src, dest) == null) {
            edge_data e = new edge(src, dest, w);
            edges.get(src).put(dest, e);
            modeCounter++;
            numOfEdges++;
        }
    }

    /**
     * Returns a pointer (shallow copy) for the collection representing all the nodes in the graph.
     * Complexity: this method run in O(1).
     * @return Collection of node_data
     */
    @Override
    public Collection<node_data> getV() {
        return this.nodes.values();
    }

    /**
     * Returns a pointer (shallow copy) for the collection representing all the edges getting out of
     * the given node (all the edges starting (source) at the given node).
     * Complexity: this method run in O(1) time.
     * @return Collection of edge_data
     */
    @Override
    public Collection<edge_data> getE(int node_id) {
        return this.edges.get(node_id).values();
    }

    /**
     * Deletes the node (with the given ID) from the graph -
     * and removes all edges which starts or ends at this node.
     * Complexity: this method run in O(k) time , V.degree=k.
     * @return the data of the removed node (null if none).
     * @param key - the deleted node id's.
     */
    @Override
    public node_data removeNode(int key) {
        if(!this.nodes.containsKey(key)){
            return null;
        }
        int size = edges.get(key).size();
        edges.remove(key);
        numOfEdges -= size;
        modeCounter += size;
        Collection<Integer> c = edges.keySet();
        for (int i : c) {
            if (edges.get(i).containsKey(key)) {
                edges.get(i).remove(key);
                modeCounter++;
                numOfEdges--;
            }
        }
        node_data n = nodes.remove(key);
        if (n != null) {
            numOfNodes--;
            modeCounter++;
        }
        return n;
    }

    /**
     * Deletes the edge from the graph,
     * Complexity: this method run in O(1) time.
     * Note: if the edge does not exists in the graph - the method simply does nothing.
     * @param src - edge source node id's.
     * @param dest - edge destination node id's.
     * @return the data of the removed edge (null if none).
     */
    @Override
    public edge_data removeEdge(int src, int dest) {
        edge_data e = edges.get(src).remove(dest);
        if(e != null){
            numOfEdges--;
        }
        return e;
    }

    /**
     * Returns the number of vertices (nodes) in the graph.
     * Complexity: this method run in O(1) time.
     * @return number of nodes in this graph.
     */
    @Override
    public int nodeSize() {
        return this.numOfNodes;
    }

    /**
     * Returns the number of edges (assume directional graph).
     * Complexity: this method run in O(1) time.
     * @return number of edges in this graph.
     */
    @Override
    public int edgeSize() {
        return this.numOfEdges;
    }

    /**
     * Returns the Mode Count - for testing changes in the graph.
     * Complexity: this method run in O(1) time.
     * @return number of changes in this graph.
     */
    @Override
    public int getMC() {
        return this.modeCounter;
    }

    /**
     * Returns true if the arguments are equal to each other and false otherwise.
     * Consequently, if both arguments are null, true is returned
     * and if exactly one argument is null, false is returned.
     * Otherwise, equality is determined by comparing all the fields of the object.
     * Note: The method uses "graphNodesEquals" and "graphEdgesEquals" method that compares HashMaps.
     * @param o - an object
     * @return true if the arguments are equal to each other and false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if(!(o instanceof directed_weighted_graph)) return false;
        DWGraph_DS that = (DWGraph_DS) o;
        return numOfNodes == that.numOfNodes &&
                numOfEdges == that.numOfEdges &&
                this.graphNodesEquals(that.nodes) &&
                this.graphEdgesEquals(that.edges);
    }

    /**
     * This private method returns true if the two HashMaps are equal to each other and false otherwise.
     * Equality is determined by comparing the keys and values of the two Hashmaps.
     * Note: The method uses "equals" method that compares each pair of nodes.
     * @param other - an HashMap<Integer,node_info>
     * @return true if the arguments are equal to each other and false otherwise
     */
    private boolean graphNodesEquals(HashMap<Integer, node_data> other) {
        if (!this.nodes.keySet().equals(other.keySet())) {
            return false;
        }
        Collection<node_data> c1 = this.nodes.values();
        Iterator<node_data> iterator1 = c1.iterator();
        Collection<node_data> c2 = other.values();
        Iterator<node_data> iterator2 = c2.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            node_data n1 = iterator1.next();
            node_data n2 = iterator2.next();
            if (!n1.equals(n2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This private method returns true if the two HashMaps(in HashMaps) are equal to each other and false otherwise.
     * Equality is determined by comparing the keys and values of the two Hashmaps.
     * Note: The method uses "equals" method that compares each pair of nodes.
     * @param other - an HashMap<Integer,node_info>
     * @return true if the arguments are equal to each other and false otherwise
     */
    private boolean graphEdgesEquals(HashMap<Integer, HashMap<Integer,edge_data>> other) {
        if (!this.edges.keySet().equals(other.keySet())) {
            return false;
        }
        Collection<HashMap<Integer,edge_data>> c1 = this.edges.values();
        Iterator<HashMap<Integer,edge_data>> iterator1 = c1.iterator();
        Collection<HashMap<Integer,edge_data>> c2 = other.values();
        Iterator<HashMap<Integer,edge_data>> iterator2 = c2.iterator();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            HashMap<Integer,edge_data> h1 = iterator1.next();
            HashMap<Integer,edge_data> h2 = iterator2.next();
            if(!h1.keySet().equals(h2.keySet())){
                return false;
            }
            Iterator<edge_data> edge1 = h1.values().iterator();
            Iterator<edge_data> edge2 = h2.values().iterator();
            while(edge1.hasNext() && edge2.hasNext()){
                edge_data e1 = edge1.next();
                edge_data e2 = edge2.next();
                if(!e1.equals(e2)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Override hashcode because equals changed.
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges, numOfNodes, numOfEdges, modeCounter);
    }

    /**
     * toString method.
     * @return directed weighted graph as String.
     */
    @Override
    public String toString() {
        String nodesValue = "{";
        for (node_data n : nodes.values()) {
            nodesValue += n;
        }
        nodesValue += "}";

        String edgesValue = "{";
        for (HashMap h : edges.values()) {
            if (!h.values().isEmpty()) {
                edgesValue += "{ ";
                for (Object o : h.values()) {
                    edge_data e = (edge_data) o;
                    edgesValue += e + " ";

                }
                edgesValue += "}";
            }
        }
        edgesValue += "}";

        return "DWGraph_DS{" +
                "nodes=" + nodesValue +
                ", edges=" + edgesValue +
                '}';
    }
}
