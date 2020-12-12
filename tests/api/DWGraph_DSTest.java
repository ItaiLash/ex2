package api;

import org.junit.jupiter.api.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DWGraph_DSTest {

    private static directed_weighted_graph g = new DWGraph_DS();
    private static directed_weighted_graph g2 = new DWGraph_DS();
    private static directed_weighted_graph g3 = new DWGraph_DS();
    private static Random _rnd = null;
    private static geo_location location = null;


    /**Returns weighted_graph g after random add of v_size nodes and e_size edge.
     *
     * @param v_size - number of nodes
     * @param e_size - number of edges
     * @param seed - seed
     * @return weighted_graph g after initialize.
     */
    public static directed_weighted_graph graph_creator(int v_size, int e_size, int seed, directed_weighted_graph g) {
        _rnd = new Random(seed);
        double x,y,z;
        node_data n;
        for(int i=0;i<v_size;i++) {
            x = _rnd.nextDouble();
            y = _rnd.nextDouble();
            z = _rnd.nextDouble();
            location = new geoLocation(x,y,z);
            n = new node(location);
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

    @BeforeAll
    public static void createGraph(){
        //create graph g
        graph_creator(5,7,1,g);

        //create graph g2
        int v = 10;
        int e = 15;
        graph_creator(v,e,1,g2);

        //create graph g3
        int v1 = 10;
        int e1 = 15;
        graph_creator(v1,e1,1,g3);
    }


    @Test
    @Order(1)
    void getNodeDoesNotExist() {
        assertNull(g.getNode(6));
    }

    @Test
    @Order(2)
    void getEdge() {
        assertNull(g.getEdge(0,1));
        System.out.println(g);
        assertNotNull(g.getEdge(1,0));
    }

    @Test
    @Order(3)
    void addExistingNode() {
        directed_weighted_graph g2 = new DWGraph_DS(g);
        g2.addNode(g2.getNode(0));
        assertEquals(g2,g);

    }

    @Test
    @Order(4)
    void removeNode() {
        int v = 10;
        int e = 15;
        System.out.println(g2);
        g2.removeNode(11);
        assertEquals(v-1, g2.nodeSize());
        assertEquals(e-4, g2.edgeSize());
        assertNull(g2.removeNode(15));
    }

    @Test
    @Order(5)
    void connect() {
        directed_weighted_graph g1 = new DWGraph_DS();
        for(int i=0 ; i<3 ; i++){
            node n = new node(new geoLocation(i,i,i));
            g1.addNode(n);
        }
        System.out.println(g1);
        g1.connect(25,26,2.5);
        DWGraph_DS g4 = new DWGraph_DS(g1);
        g1.connect(25,28,1);
        assertEquals(g1,g4);
        g1.removeEdge(25,26);
        assertNull(g1.getEdge(15,16));
        g1.connect(25,26,1.5);
        edge_data e = new edge(25,26,1.5);
        assertEquals(g1.getEdge(25,26),e);
        g1.connect(26,27,4.7);
        assertEquals(4.7,g1.getEdge(26,27).getWeight());
        assertThrows(RuntimeException.class, () -> g1.connect(27,25,-2.5));
    }


    @Test
    @Order(6)
    void removeEdge() {
        int v = 10;
        int e = 15;
        System.out.println(g3);
        g3.removeEdge(16,21);
        g3.removeEdge(17,19);
        g3.removeEdge(17,22);
        assertEquals(v, g3.nodeSize());
        assertEquals(e-3, g3.edgeSize());
        assertNull(g3.removeEdge(22,20));  //edge does not exist
        assertNull(g3.removeEdge(22,25));  //node 25 does not exist


    }
    @Test
    @Order(7)
    void nodeSize() {
        int size = g.nodeSize();
        g.removeNode(0);
        g.removeNode(0);     //node does not exist
        g.removeNode(5);     //node does not exist
        assertEquals(size-1,g.nodeSize());
    }

    @Test
    @Order(8)
    void edgeSize() {
        int size = g.edgeSize();
        g.removeEdge(1,2);
        g.removeEdge(1,3);      //edge does not exist
        Assertions.assertEquals(size-1,g.edgeSize());
    }


    @Test
    @Order(9)
    void testEquals() {
        directed_weighted_graph gra = new DWGraph_DS();
        graph_creator(100,1000,1,gra);
        directed_weighted_graph copy = new DWGraph_DS(gra);
        assertEquals(gra,copy);
        gra.removeNode(100);
        assertNotEquals(gra,copy);
    }

    @Test
    @Order(10)
    void getV() {
        directed_weighted_graph dGraph = new DWGraph_DS();
        assertEquals(0, dGraph.getV().size());
        node_data n = new node(new geoLocation(0,0,0));
        dGraph.addNode(n);
        dGraph.addNode(n);
        assertEquals(1, dGraph.getV().size());
    }

    @Test
    @Order(11)
    void getE() {
    directed_weighted_graph dGraph = new DWGraph_DS();
    node_data n1 = new node(new geoLocation(0,0,0));
    node_data n2 = new node(new geoLocation(0,0,0));
    dGraph.addNode(n1);
    dGraph.addNode(n2);
    dGraph.connect(n1.getKey(), n2.getKey(), 1);
    assertEquals(1, dGraph.getE(n1.getKey()).size());
    assertEquals(0, dGraph.getE(n2.getKey()).size());
    }

    @Test
    @Order(12)
    public void graphCreatorTestRuntime() throws Exception{
        int v = 100000, e = v*10;
        assertTimeout(Duration.ofSeconds(5),() ->{
            g = graph_creator(v,e,1,g);
        });
    }

    @Test
    @Order(13)
    public void graphCopyTestRuntime() throws Exception{
        int v = 100000, e = v*10;
        g = graph_creator(v,e,1,g);
        assertTimeout(Duration.ofSeconds(5),() ->{
            directed_weighted_graph gra = new DWGraph_DS(g);
        });
    }
}