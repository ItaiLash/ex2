package api;

import com.google.gson.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {
    private directed_weighted_graph graph;

    public DWGraph_Algo() {
        this.graph = new DWGraph_DS();
    }

    @Override
    public void init(directed_weighted_graph g) {
        this.graph = g;
    }

    @Override
    public directed_weighted_graph getGraph() {
        return graph;
    }

    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(this.graph);
    }

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


    @Override
    public double shortestPathDist(int src, int dest) {
        if (this.graph.getV().contains(src) || this.graph.getV().contains(dest)) {
            throw new RuntimeException("One or more of your keys does not exist in the graph!");
        }
        double d = Dijkstra(this.graph.getNode(src), this.graph.getNode(dest));
        resetDis();
        resetTag();
        resetPre();
        if (d == Integer.MAX_VALUE) {
            return -1;
        }
        return d;

    }

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
        node src2 = (node) this.graph.getNode(src);
        node dest2 = (node) this.graph.getNode(dest);
        List<node_data> reverseList = new LinkedList<>();
        node temp = dest2;
        while (temp.getPre() != null) {
            reverseList.add(temp);
            temp = (node) temp.getPre();
        }
        node_data[] arr = reverseList.toArray(node_data[]::new);
        list.add(src2);
        for (int i = arr.length - 1; i >= 0; i--) {
            list.add(arr[i]);
        }
        resetDis();
        resetTag();
        resetPre();
        return list;
    }

    @Override
    public boolean save(String file) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        JSONObject node = null;
        JSONObject edge = null;
        JSONArray nodes = new JSONArray();
        JSONArray edges = new JSONArray();
        for (node_data n : this.graph.getV()) {
            node = new JSONObject();
            node.put("pos", n.getLocation());
            node.put("id", n.getKey());
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

    private double Dijkstra(node_data src, node_data dest) {
        double shortest = Integer.MAX_VALUE;
        PriorityQueue<node_data> pq = new PriorityQueue<>();
        node tempSrc = (node) src;
        tempSrc.setDis(0.0);
        pq.add(src);
        while (!pq.isEmpty()) {
            node temp = (node) pq.poll();
            for (edge_data e : this.graph.getE(temp.getKey())) {
                node n = (node) this.graph.getNode(e.getDest());
                if (n.getTag() == -1) {
                    if (n.getDis() > temp.getDis() + e.getWeight()) {
                        n.setDis(Math.min(n.getDis(), temp.getDis() + e.getWeight()));
                        n.setPre(temp);
                    }
                    pq.add(n);
                }
            }
            temp.setTag(1);
            if (temp == dest) {
                return temp.getDis();
            }
        }
        return shortest;
    }

    private void resetTag() {
        for (node_data n : this.graph.getV()) {
            n.setTag(-1);
        }
    }

    private void resetDis() {
        for (node_data n : this.graph.getV()) {
            node temp = (node) n;
            temp.setDis(Double.MAX_VALUE);
        }
    }

    private void resetPre() {
        for (node_data n : this.graph.getV()) {
            node temp = (node) n;
            temp.setPre(null);
        }
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
    public static void main(String[] args) throws JSONException {
        directed_weighted_graph g = new DWGraph_DS();
        geo_location l0 = new geoLocation(0, 0, 0);
        geo_location l1 = new geoLocation(1, 1, 1);
        geo_location l2 = new geoLocation(2, 2, 2);
        geo_location l3 = new geoLocation(3, 3, 3);

        node_data n0 = new node(l0, 0);   //0
        node_data n1 = new node(l1, 1);   //1
        node_data n2 = new node(l2, 2);   //2
        node_data n3 = new node(l3, 3);   //3
        node_data n4 = new node(l3, 3);   //3

        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        //g.addNode(n4);
        g.connect(0, 1, 3.5);
        //g.connect(0,3,1);
        g.connect(1, 0, 2);
        //g.connect(1,1,1);
        g.connect(1, 2, 0.5);
        g.connect(2, 3, 1.2);
        g.connect(2, 3, 0.1);
        g.connect(0, 3, 6);

        g.connect(2, 0, 1.7);
        g.connect(2, 1, 1.9);
        g.connect(3, 0, 1.9);
        //  g.connect(3,1,1.9);
        // g.connect(3,2,1.9);
        directed_weighted_graph g2 = new DWGraph_DS();
        //g2.addNode(n0);


        System.out.println(g);
        dw_graph_algorithms gra = new DWGraph_Algo();
        gra.init(g);
        System.out.println(gra.isConnected());
        System.out.println(gra.shortestPathDist(0, 3));
        System.out.println(gra.shortestPath(0, 3));
        //gra.save("theGraph.obj");
        gra.load("theGraph.obj");
        System.out.println(gra.getGraph());


    }
}
