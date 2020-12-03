package api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class nodeTest {
    private node n;
    geo_location g;

    public void nodeDefaultCreator(){
        g = new geoLocation(2,1,0);
        n = new node(g,2.4);
    }

    public void nodeCreator(){
        g = new geoLocation(-2,7,3.1);
        n = new node(4, g, -2.7, "Green", 4, Double.MAX_VALUE, null);
    }

    @Test
    void get() {
        nodeDefaultCreator();
        int key = n.getKey();
        assertEquals(key,0);
        geo_location geo = n.getLocation();
        assertEquals(geo,g);
        double weight = n.getWeight();
        assertEquals(weight,2.4);
        String info = n.getInfo();
        assertEquals(info,"White");
        int tag = n.getTag();
        assertEquals(tag,-1);
        double dis = n.getDis();
        assertEquals(dis,Double.MAX_VALUE);
        node_data pre = n.getPre();
        assertNull(pre);
    }

    @Test
    void deepCopyTest() {
        nodeCreator();
        node n1 = new node(n);
        assertEquals(n1,n);
        n1.setTag(-2);
        assertNotEquals(n1,n);

    }
}