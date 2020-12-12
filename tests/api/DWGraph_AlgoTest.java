package api;

import org.json.JSONException;
import org.junit.jupiter.api.*;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DWGraph_AlgoTest {
    private static directed_weighted_graph g = new DWGraph_DS();
    private static dw_graph_algorithms ga = new DWGraph_Algo();

    @BeforeAll
    static void createConnected() {
        for (int i = 0; i < 4; i++) {
            node_data n = new node(new geoLocation(i, i, i));
            g.addNode(n);
        }
        g.connect(0, 1, 0.5);
        g.connect(0, 3, 9);
        g.connect(1, 0, 3);
        g.connect(1, 2, 2);
        g.connect(1, 3, 4);
        g.connect(2, 0, 6);
        g.connect(2, 3, 1);
        g.connect(3, 2, 1);
        ga.init(g);
    }


    @Test
    @Order(1)
    void copy() {
        directed_weighted_graph copy = new DWGraph_DS(g);
        dw_graph_algorithms gaCopy = new DWGraph_Algo();
        gaCopy.init(copy);
        assertEquals(ga, gaCopy);
        copy.removeNode(0);
        gaCopy.init(copy);
        assertNotEquals(ga, gaCopy);
    }

    @Test
    @Order(6)
    void isConnected() {
        assertTrue(ga.isConnected());
        directed_weighted_graph empty = new DWGraph_DS();
        dw_graph_algorithms emptyGa = new DWGraph_Algo();
        emptyGa.init(empty);
        assertTrue(emptyGa.isConnected());
        directed_weighted_graph singleNode = new DWGraph_DS();
        singleNode.addNode(new node(new geoLocation(0, 0, 0)));
        dw_graph_algorithms singleNodeGa = new DWGraph_Algo();
        singleNodeGa.init(singleNode);
        assertTrue(singleNodeGa.isConnected());
        g.removeEdge(0, 1);
        ga.init(g);
        assertFalse(ga.isConnected());

    }

    @Test
    @Order(2)
    void shortestPathDist() {
        double dist = ga.shortestPathDist(0, 3);
        assertEquals(3.5, dist);

        dw_graph_algorithms ga2 = new DWGraph_Algo();
        directed_weighted_graph g2 = ga.copy();
        g2.addNode(new node(20, new geoLocation(1, 1, 1), 0, "", -1));
        ga2.init(g2);
        dist = ga2.shortestPathDist(1, 20);
        assertEquals(-1, dist);

        assertThrows(RuntimeException.class, () -> ga.shortestPathDist(2, 10));
    }

    @Test
    @Order(3)
    void shortestPath() {
        List<node_data> actual = ga.shortestPath(0, 3);
        List<node_data> expected = new LinkedList<>();
        expected.add(g.getNode(0));
        expected.add(g.getNode(1));
        expected.add(g.getNode(2));
        expected.add(g.getNode(3));
        assertEquals(expected, actual);

        g.connect(0, 3, 3);
        actual = ga.shortestPath(0, 3);
        expected.clear();
        expected.add(g.getNode(0));
        expected.add(g.getNode(3));
        assertEquals(expected, actual);

        actual = ga.shortestPath(2, 2);
        expected.clear();
        expected.add(g.getNode(2));
        assertEquals(expected, actual);

        assertThrows(RuntimeException.class, () -> ga.shortestPath(2, 10));
    }

    @Test
    void saveAndLoad() throws JSONException {
        ga.save("myGraph.obj");
        dw_graph_algorithms ga3 = new DWGraph_Algo();
        ga3.load("myGraph.obj");
        System.out.println(ga.getGraph());
        System.out.println(ga3.getGraph());
        assertEquals(ga,ga3);
    }
}