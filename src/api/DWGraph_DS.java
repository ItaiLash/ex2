package api;

import jdk.swing.interop.DispatcherWrapper;

import java.util.*;

public class DWGraph_DS implements directed_weighted_graph{
    private HashMap<Integer,node_data> nodes;
    private HashMap<Integer,HashMap<Integer,edge_data>> edges;
    private int numOfNodes;
    private int numOfEdges;
    private int modeCounter;

    public DWGraph_DS(){
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        this.numOfEdges = 0;
        this.numOfNodes = 0;
        this.modeCounter = 0;
    }
    public DWGraph_DS(directed_weighted_graph other){
        this.nodes = new HashMap<>();
        this.edges = new HashMap<>();
        nodesDeepCopy(other,this.nodes);
        edgesDeepCopy(other,this.edges);
        this.numOfEdges = other.edgeSize();
        this.numOfNodes = other.nodeSize();
    }

    private HashMap<Integer, node_data> nodesDeepCopy(directed_weighted_graph other, HashMap nodes) {
        HashMap<Integer, node_data> h = nodes;
        for (node_data n : other.getV()) {
            this.addNode(n);
        }
        return h;
    }
    private HashMap<Integer, HashMap<Integer, edge_data>> edgesDeepCopy(directed_weighted_graph other, HashMap edges) {
        HashMap<Integer, HashMap<Integer, edge_data>> h = edges;
        int key;
        for(node_data n : this.getV()){
            key = n.getKey();
            for (edge_data e : other.getE(key)) {
                edge_data edge = new edge(e);
                this.edges.get(e.getSrc()).put(e.getDest(), edge);
            }
        }
        return h;
    }


    @Override
    public node_data getNode(int key) {
        return this.nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if(this.nodes.containsKey(src) && this.nodes.containsKey(dest)) {
            return this.edges.get(src).get(dest);
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        if(nodes.containsKey(n.getKey())){
            return;
        }
        nodes.put(n.getKey(),n);
        edges.put(n.getKey(),new HashMap<>());
        modeCounter++;
        numOfNodes++;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if(w < 0){ throw new RuntimeException("The weight must be positive"); }
        if(src == dest){ return; }

        if(this.getEdge(src,dest)!=null && edges.get(src).get(dest).getWeight() != w) {
            edge_data e = new edge(getNode(src), getNode(dest), w);
            edges.get(src).put(dest,e);
            modeCounter++;
        }
        else if(nodes.containsKey(src) && nodes.containsKey(dest) && this.getEdge(src,dest) == null){
            edge_data e = new edge(getNode(src), getNode(dest), w);
            edges.get(src).put(dest, e);
            modeCounter++;
            numOfEdges++;
        }
    }

    @Override
    public Collection<node_data> getV() {
        return this.nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        return this.edges.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        int size = edges.get(key).size();
        //edges.get(key).clear();
        edges.remove(key);
        numOfEdges-=size;
        modeCounter+=size;
        Collection<Integer> c = edges.keySet();
        for(int i : c){
            if(edges.get(i).containsKey(key)){
                edges.get(i).remove(key);
                modeCounter++;
                numOfEdges--;
            }
        }
       node_data n = nodes.remove(key);
        if(n!=null) {
            numOfNodes--;
            modeCounter++;
        }
        return n;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        return edges.get(src).remove(dest);
    }

    @Override
    public int nodeSize() {
        return this.numOfNodes;
    }

    @Override
    public int edgeSize() {
        return this.numOfEdges;
    }

    @Override
    public int getMC() {
        return this.modeCounter;
    }

///////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////need to fix those methods/////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_DS that = (DWGraph_DS) o;
        return numOfNodes == that.numOfNodes &&
                numOfEdges == that.numOfEdges &&
                modeCounter == that.modeCounter &&
                Objects.equals(nodes, that.nodes) &&
                Objects.equals(edges, that.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, edges, numOfNodes, numOfEdges, modeCounter);
    }

    @Override
    public String toString() {
        String nodesValue = "{";
        for(node_data n : nodes.values()){
            nodesValue += n;
        }
        nodesValue += "}";

        String edgesValue = "{";
        for(HashMap h : edges.values()){
            edgesValue+="{";
            for(Object o : h.values()){
                edge_data e = (edge_data)o;
                edgesValue+=e;
            }
            edgesValue+="}";
        }
        edgesValue+="}";

            return "DWGraph_DS{" +
                "nodes=" + nodesValue +
                ", edges=" + edgesValue+
                '}';
    }

    public static void main(String[] args) {
        directed_weighted_graph g = new DWGraph_DS();
        geo_location l0 = new geoLocation(0,0,0);
        geo_location l1 = new geoLocation(1,1,1);
        geo_location l2 = new geoLocation(2,2,2);
        geo_location l3 = new geoLocation(3,3,3);

        node_data n0 = new node(l0,0);   //0
        node_data n1 = new node(l1,1);   //1
        node_data n2 = new node(l2,2);   //2
        node_data n3 = new node(l3,3);   //3
        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.connect(0,1,3.5);
        g.connect(1,0,2);
        g.connect(1,1,1);
        g.connect(1,2,0.5);
        g.connect(3,2,1.2);
        System.out.println(g);
        directed_weighted_graph g2 = new DWGraph_DS(g);
        System.out.println(g2);
        g2.removeNode(0);
        System.out.println("After deletion:");
        System.out.println(g);
        System.out.println(g2);





    }

}
