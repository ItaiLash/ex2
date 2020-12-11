package api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class edgeTest {
    edge_data e;
    node_data n1;
    node_data n2;


    public void edgeCreator(){
        geo_location g1 = new geoLocation(1,1,1);
        geo_location g2 = new geoLocation(2,2,2);
        n1 = new node(g1);
        n2 = new node(g2);
        e = new edge(n1.getKey(),n2.getKey(),3.12);
    }

    @Test
    void get() {
        edgeCreator();
        int n1key = e.getSrc();
        int n2key = e.getDest();
        double weight = e.getWeight();
        String info = e.getInfo();
        int tag = e.getTag();
        assertEquals(n1key,0);
        assertEquals(n2key,1);
        assertEquals(weight,3.12);
        assertEquals(info,"White");
        assertEquals(tag,-1);
    }

    @Test
    void deepCopy() {
        edgeCreator();
        edge_data ed = new edge(e);
        assertEquals(ed,e);
        ed.setTag(-2);
        assertNotEquals(ed,e);

    }

}