package api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class nodeTest {
    private node n;
    geo_location g;

    public void nodeDefaultCreator(){
        g = new geoLocation(2,1,0);
        n = new node(g);
    }

    public void nodeCreator(){
        g = new geoLocation(-2,7,3.1);
        n = new node(4, g, -2.7, "Green", 4);
    }

    @Test
    void get() {
        nodeDefaultCreator();
        int key = n.getKey();
        assertEquals(key,0);
        geo_location geo = n.getLocation();
        assertEquals(geo,g);
        double weight = n.getWeight();
        assertEquals(weight,Double.MAX_VALUE);
        String info = n.getInfo();
        assertEquals(info,"White");
        int tag = n.getTag();
        assertEquals(tag,-1);

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