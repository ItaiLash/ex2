package api;

import okio.Timeout;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    private static directed_weighted_graph g;
    private static Random _rnd = null;
    private static geo_location location = null;

    /**Returns weighted_graph g after random add of v_size nodes and e_size edge.
     *
     * @param v_size - number of nodes
     * @param e_size - number of edges
     * @param seed - seed
     * @return weighted_graph g after initialize.
     */
    public static directed_weighted_graph graph_creator(int v_size, int e_size, int seed) {
        g = new DWGraph_DS();
        _rnd = new Random(seed);
        double x,y,z;
        node_data n;
        for(int i=0;i<v_size;i++) {
            x = _rnd.nextDouble();
            y = _rnd.nextDouble();
            z = _rnd.nextDouble();
            location = new geoLocation(x,y,z);
            n = new node(location,i);
            g.addNode(n);
        }
        int[] nodes = nodes(g);
        while(g.edgeSize() < e_size) {
            int a = nextRnd(0,v_size);
            int b = nextRnd(0,v_size);
            int i = nodes[a];
            int j = nodes[b];
            double w = _rnd.nextDouble();
            g.connect(i,j, w);
        }
        return g;
    }
    private static int nextRnd(int min, int max) {
        double v = nextRnd(0.0+min, (double)max);
        int ans = (int)v;
        return ans;
    }
    private static double nextRnd(double min, double max) {
        double d = _rnd.nextDouble();
        double dx = max-min;
        double ans = d*dx+min;
        return ans;
    }
    /**
     * Simple method for returning an array with all the node_data of the graph,
     * Note: this should be using an Iterator<node_edge> to be fixed in Ex1
     * @param g
     * @return
     */
    private static int[] nodes(directed_weighted_graph g) {
        int size = g.nodeSize();
        Collection<node_data> V = g.getV();
        node_data[] nodes = new node_data[size];
        V.toArray(nodes);
        int[] ans = new int[size];
        for(int i=0;i<size;i++) {ans[i] = nodes[i].getKey();}
        Arrays.sort(ans);
        return ans;
    }

    @Test
    public void graphCreatorTestRuntime() throws Exception{
        int v = 100000, e = v*10;
        assertTimeout(Duration.ofSeconds(5),() ->{
            g = graph_creator(v,e,1);
        });
    }

    @Test
    public void graphCopyTestRuntime() throws Exception{
        int v = 100000, e = v*10;
        g = graph_creator(v,e,1);
        assertTimeout(Duration.ofSeconds(5),() ->{
            directed_weighted_graph gra = new DWGraph_DS(g);
        });
    }

    @Test
    void getNodeDoesNotExist() {
        graph_creator(5,7,1);
        assertNull(g.getNode(6));
    }



    @Test
    void getEdge() {
        graph_creator(5,7,1);
        assertNull(g.getEdge(0,1));
        assertNotNull(g.getEdge(1,0));
    }




    @Test
    void addExistingNode() {
        graph_creator(5,7,1);
        directed_weighted_graph g2 = new DWGraph_DS(g);
        g2.addNode(g2.getNode(0));
        assertEquals(g2,g);

    }
/*
    @Test
    void connect() {
        g = new DWGraph_DS();
        for(int i=0 ; i<3 ; i++){
            g.addNode(i);
        }
        g.connect(1,2,2.5);
        g.connect(0,3,1);
        g.removeEdge(0,1);
        assertNull(g.getEdge(0,1));
        g.connect(0,1,1.5);
        edge_data e = new edge()
        assertEquals(g.getEdge(0,1),);
        g.connect(1,2,4.7);
        assertEquals(4.7,g.getEdge(1,2));
    }

    @Test
    void removeNode() {
        int v = 10;
        int e = 15;
        g = graph_creator(v,e,1);
        g.removeNode(0);
        g.removeNode(0);
        g.removeNode(1);
        assertEquals(g.nodeSize(),v-2);
        assertEquals(g.edgeSize(),e-4);
    }

    @Test
    void removeEdge() {
        int v = 10;
        int e = 15;
        g = graph_creator(v,e,1);
        System.out.println(g);
        g.removeEdge(3,0);
        g.removeEdge(4,2);
        g.removeEdge(1,1);
        assertEquals(g.edgeSize(),e-1);
    }

    @Test
    void nodeSize() {
        g = new WGraph_DS();
        g.addNode(0);
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(1);
        g.addNode(0);
        g.removeNode(0);
        g.removeNode(2);
        g.removeNode(2);
        int size = g.nodeSize();
        Assertions.assertEquals(2,size);
    }

    @Test
    void edgeSize() {
        g = graph_creator(10,20,1);
        g.removeEdge(0,4);
        g.removeEdge(0,4);      //edge does not exist
        g.removeEdge(3,9);
        g.removeEdge(0,1);      //edge does not exist
        int size = g.edgeSize();
        Assertions.assertEquals(18,size);
    }


    @Test
    void testEquals() {
        g = graph_creator(100,1000,1);
        weighted_graph g2 = new WGraph_DS(g);
        assertEquals(g,g2);
    }


    @Test
    void getNode() {
    }

    @Test
    void getEdge() {
    }

    @Test
    void addNode() {
    }

    @Test
    void connect() {
    }

    @Test
    void getV() {
    }

    @Test
    void getE() {
    }

    @Test
    void removeNode() {
    }

    @Test
    void removeEdge() {
    }

    @Test
    void nodeSize() {
    }

    @Test
    void edgeSize() {
    }

    @Test
    void getMC() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }

    @Test
    void main() {
    }

     */
}