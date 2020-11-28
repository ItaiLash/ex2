package api;

/**
 * This class ia an implementation of geo_location interface.
 * geoLocation represents a geo location <x,y,z>, aka Point3D
 */

public class geoLocation implements geo_location {

    private double x,y,z;


    public geoLocation(double x1 , double y1, double z1){
        this.x = x1;
        this.y = y1;
        this.z = z1;
    }
    public geoLocation(geo_location other){
        this.x = other.x();
        this.y = other.y();
        this.z = other.z();

    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    @Override
    public double distance(geo_location g) {
        double nx = Math.pow(this.x-g.x(),2);
        double ny = Math.pow(this.y-g.y(),2);
        double nz = Math.pow(this.z-g.z(),2);
        double distance = Math.sqrt(nx+ny+nz);
        return distance;

    }
}
