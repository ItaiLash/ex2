package api;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.util.*;

/**
 * This class is an implementation of dw_graph_algorithms interface.
 * DWGraph_Algo class represents a Directed (positive) Weighted Graph and implement Theory Algorithms including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected(); // strongly (all ordered pais connected)
 * 3. double shortestPathDist(int src, int dest);
 * 4. List of node_data shortestPath(int src, int dest);
 * 5. Save(file); // JSON file
 * 6. Load(file); // JSON file
 * @author itai.lashover and liav.weiss
 *
 */
public class DWGraph_Algo implements dw_graph_algorithms {
    /**
     * The only field in the class is a directed weighted graph on which we want to perform the methods.
     */
    private directed_weighted_graph graph;

    /**
     * Default constructor
     */
    public DWGraph_Algo() {
        this.graph = new DWGraph_DS();
    }

    /**
     * Init the graph on which this set of algorithms operates.
     * @param g - a weighted graph
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }

    /**
     * Return the underlying graph of which this class works.
     * @return a directed weighted graph.
     */
    @Override
    public directed_weighted_graph getGraph() {
        return graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     * The method does this by using the deep copy constructor in DWGraph_DS.
     * @return identical graph.
     */
    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.graph);
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node.
     * The method uses BFS algorithm.
     * NOTE: BFS method changes the value of each node's tag.
     * Thus the method calls resetInfo function that resets the info that changed.
     * Complexity: O(|V|*(|V|+|E|)), |V|=number of nodes, |E|=number of edges.
     * @return true if strongly connected, false otherwise.
     */
    @Override
    public boolean isConnected() {
        if (this.graph.nodeSize() == 0) {
            return true;
        }
        for (node_data n : this.graph.getV()) {
            boolean b = this.bfs(n);
            resetTag();
            if (!b) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the length of the shortest path between src to dest.
     * Note: if no such path --> returns -1
     * The method uses a combination of BFS and Dijkstra's algorithms.
     * Note2: Dijkstra method changes the value of each node's tag, info and weight.
     * Thus the method calls resetTag, resetInfo and resetPre functions that resets the tag ,the info and the weight
     * that changed.
     * Complexity: O((|V|+|E|)log|V|), |V|=number of nodes, |E|=number of edges.
     * @param src  - start node
     * @param dest - end (target) node
     * @return the length of the shortest path between src to dest, -1 if there is no path.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (this.graph.getV().contains(src) || this.graph.getV().contains(dest)) {
            throw new RuntimeException("One or more of your keys does not exist in the graph!");
        }
        if(src == dest){
            return 0;
        }
        resetInfo();
        resetTag();
        resetWeight();
        double d = Dijkstra(this.graph.getNode(src), this.graph.getNode(dest));
        resetInfo();
        resetTag();
        resetWeight();
        if (d == Integer.MAX_VALUE) {
            return -1;
        }
        return d;

    }

    /**
     * Returns the shortest path between src to dest - as an ordered List of nodes:
     * src --> n1 --> n2 --> ... --> dest.
     * Note: if no such path --> null.
     * The method uses a combination of BFS and Dijkstra's algorithms.
     * Note2: Dijkstra method changes the value of each node's tag, info and weight.
     * Thus the method calls resetTag, resetInfo and resetPre functions that resets the tag ,the info and the weight
     * that changed.
     * The method uses Dijkstra algorithm to build a List od nodes: dest --> ... -->src
     * Thus the method need to reverse the list later.
     * Complexity: O((|V|+|E|)log|V|), |V|=number of nodes, |E|=number of edges.
     * @param src  - start node
     * @param dest - end (target) node
     * @return List of nodes.
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        List<node_data> list = new LinkedList<>();
        if (this.graph.getNode(src) == null) {
            throw new RuntimeException("This graph does not contain key " + src);
        }
        if (this.graph.getNode(dest) == null) {
            throw new RuntimeException("This graph does not contain key " + dest);
        }
        if (shortestPathDist(src, dest) == -1) {
            return null;
        }
        if (src == dest) {
            list.add(this.graph.getNode(dest));
            return list;
        }
        Dijkstra(this.graph.getNode(src), this.graph.getNode(dest));
        node_data src2 = this.graph.getNode(src);
        node_data dest2 = this.graph.getNode(dest);
        List<node_data> reverseList = new LinkedList<>();
        node_data temp = dest2;
        while (temp.getTag() != -1) {
            reverseList.add(temp);
            temp = this.graph.getNode(temp.getTag());
        }
        node_data[] arr = reverseList.toArray(node_data[]::new);
        list.add(src2);
        for (int i = arr.length - 1; i >= 0; i--) {
            list.add(arr[i]);
        }
        resetInfo();
        resetTag();
        resetWeight();
        return list;
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     * @param file - the file name.
     * @return true - iff the file was successfully saved.
     */
    @Override
    public boolean save(String file) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONObject node;
        JSONObject edge;
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();
        for (node_data n : this.graph.getV()) {
            node = new JSONObject();
            node.put("pos", n.getLocation());
            node.put("id", n.getKey());
            node.put("weight", n.getWeight());
            nodes.put(node);
            for (edge_data e : this.graph.getE(n.getKey())) {
                edge = new JSONObject();
                edge.put("src", e.getSrc());
                edge.put("w", e.getWeight());
                edge.put("dest", e.getDest());
                edges.put(edge);
            }
        }
        jsonObject.put("Nodes", nodes);
        jsonObject.put("Edges", edges);

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonObject.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph remain "as is".
     * NOTE: this method use "graphJsonDeserializer" class
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DWGraph_DS.class, new graphJsonDeserializer());
        Gson gson = builder.create();
        try {
            FileReader reader = new FileReader(file);
            directed_weighted_graph graph = gson.fromJson(reader, DWGraph_DS.class);
            init(graph);
            System.out.println(this.getGraph());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This private method based on breadth-first search.
     * BFS is an algorithm for traversing or searching graph data structures.
     * The method checks whether or not the graph is linked,
     * in other words it checks whether there is a path between node to each other node.
     * The method use counter that count the number of nodes that connected to the source node.
     * If counter value equal to the number of nodes in this graph that means that the source node connected.
     * To check if the whole graph is strongly connected needs to run the method on all the nodes in the graph.
     * The method stored a queue of the visited nodes:
     * Pop the first node from the queue.
     * Gets a collection of this node edges.
     * Goes through all the nodes that have an edge from the pop node.
     * Check if the node has already been visited, if so skip it(tag = 1 -> visited, tag = -1 -> not visited).
     * Otherwise mark it as visited (update his own tag) and add the node to the queue.
     * Add this node's neighbors to the queue and repeat these steps
     * The method use counter that count the number of nodes that connected to the source node.
     * After the queue is empty check if the counter value equal to the number of nodes in this graph
     * that means that the source node connected.
     * If so the method will return true, Otherwise false.
     * Note: The method change the tag values.
     * Complexity: O(|V|+|E|), |V|=number of nodes, |E|=number of edges.
     *
     * @param n - the source node.
     * @return true if all the nodes in the graph are marked as visited, false otherwise
     */
    private boolean bfs(node_data n) {
        Queue<node_data> queue = new LinkedList<>();
        n.setTag(1);
        int counter = 1;
        queue.add(n);
        while (!queue.isEmpty()) {
            node_data temp = queue.poll();
            Collection<edge_data> edges = this.graph.getE(temp.getKey());
            for (edge_data next : edges) {
                node_data dest = this.graph.getNode(next.getDest());
                if (dest.getTag() == -1) {
                    dest.setTag(1);
                    queue.add(dest);
                    counter++;
                }
            }
        }
        return (counter == this.graph.nodeSize());
    }

    /**
     * This private method based on Dijkstra's algorithm.
     * Dijkstra's algorithm is an algorithm for finding the shortest paths between nodes in a graph.
     * In other words it finds the shortest paths between the source node and the destination node.
     * The method uses the weight of each node to update his current distance from the source node.
     * The method stored a priority queue(priority is determined by the weight) of the visited nodes:
     * Pop the first node from the queue.
     * Visit each one of this nodes neighbors:
     * Check if the node has already been visited, if so skip it(tag = Black -> visited, tag = White -> not visited).
     * Updates his weight to be the distance between the node and the source node.
     * Updates his tag To be the node's id from which he came to.
     * Add this node to the queue.
     * After going through all the neighbors of the node,
     * updates that we visited this node by change his info to "Black" and therefore will not visit it again.
     * Repeat these steps until the queue is empty or has reached the destination node.
     * If the queue is empty it means it did not reach the destination node (the graph is not connected), return infinity.
     * Otherwise returns the tag of the destination node
     * Note: The method change the info, tag and pre values.
     * Complexity: O((|V|+|E|)log|V|), |V|=number of nodes, |E|=number of edges.
     *
     * @param src  - the source node_info
     * @param dest - the destination node_info
     * @return the shortest path between the two nodes and infinity(Integer.MAX_VALUE) if there is no path like this.
     */
    private double Dijkstra(node_data src, node_data dest) {
        double shortest = Integer.MAX_VALUE;
        PriorityQueue<node_data> pq = new PriorityQueue<>(this.graph.nodeSize(), new Comparator<node_data>() {
            @Override
            public int compare(node_data o1, node_data o2) {
                return Double.compare(o1.getWeight(),o2.getWeight());
            }
        });
        src.setWeight(0.0);
        pq.add(src);
        while (!pq.isEmpty()) {
            node_data temp = pq.poll();
            for (edge_data e : this.graph.getE(temp.getKey())) {
                node_data n = this.graph.getNode(e.getDest());
                if (n.getInfo() == "White") {
                    if (n.getWeight() > temp.getWeight() + e.getWeight()) {
                        n.setWeight(Math.min(n.getWeight(), temp.getWeight() + e.getWeight()));
                        n.setTag(temp.getKey());
                    }
                    pq.add(n);
                }
            }
            temp.setInfo("Black");
            if (temp.getKey() == dest.getKey()) {
                return temp.getWeight();
            }
        }
        return shortest;
    }

    /**
     * This private method resets the values of all the tags of the nodes in the graph.
     * Reset the value = change it back to default value: -1.
     */
    private void resetTag() {
        for (node_data n : this.graph.getV()) {
            n.setTag(-1);
        }
    }
    /**
     * This private method resets the value of weight in each node in the graph.
     * Reset the value = change it back to default value: Double.MAX_VALUE (infinity).
     */
    private void resetWeight() {
        for (node_data n : this.graph.getV()) {
            n.setWeight(Double.MAX_VALUE);
        }
    }

    /**
     * This private method resets the value of info in each node in the graph.
     * Reset the value = change it back to default value: White
     */
    private void resetInfo() {
        for (node_data n : this.graph.getV()) {
            n.setInfo("White");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_Algo that = (DWGraph_Algo) o;
        return this.graph.equals(that.graph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graph);
    }
/*
        private void flipGraph(){
            Collection<node_data> nodes = this.graph.getV();
            for(node_data n : nodes){
                Collection<edge_data> edges = this.graph.getE(n.getKey());
                for(edge_data e : edges){
                    int src = e.getSrc();
                    int dest = e.getDest();
                    double w = e.getWeight();
                    this.graph.removeEdge(src,dest);
                    this.graph.connect(dest,src,w);
                }
            }
        }
        public boolean isConnected() {
            if (this.graph.nodeSize() == 0) {
                return true;
            }
            node_data n = this.graph.getV().iterator().next();
            boolean b = this.bfs(n);
            resetTag();
            if(!b){
                return false;
            }
            flipGraph();
            b = this.bfs(n);
            flipGraph();
            resetTag();
            if(!b){
                return false;
            }
            return true;
        }
    */
}