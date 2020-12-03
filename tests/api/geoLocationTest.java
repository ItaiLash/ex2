package api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class geoLocationTest {
    public static geo_location arr[];


    @BeforeAll
    public static void pointCreator(){
        arr = new geo_location[5];
        arr[0] = new geoLocation(0,0,0);
        arr[1] = new geoLocation(1,1,1);
        arr[2] = new geoLocation(-2,-1,-6);
        arr[3] = new geoLocation(7, 4, 3);
        arr[4] = new geoLocation(17, 6, 2);

    }

    @Test
    public void distanceTest(){
        double d1 = arr[0].distance(arr[1]);
        double d2 = arr[3].distance(arr[4]);
        double d3 = arr[2].distance(arr[3]);
        assertEquals(d1,Math.sqrt(3));
        assertEquals(d2,Math.sqrt(105));
        assertEquals(d3,Math.sqrt(187));
    }

    @Test
    public void distanceTestAbs(){
        double d1 = arr[2].distance(arr[3]);
        double d2 = arr[3].distance(arr[2]);
        assertEquals(d1,d2);
    }

    @Test
    public void distanceTestSamePoint(){
        double d1 = arr[0].distance(arr[0]);
        assertEquals(d1,0);
    }
}