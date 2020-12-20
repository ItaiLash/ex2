package api;

import java.util.Objects;

/**
 * This class ia an implementation of geo_location interface.
 * geoLocation represents a geo location (x,y,z), aka Point3D.
 * @author itai.lashover and liav.weiss
 */
public class geoLocation implements geo_location {

    private double x,y,z;

    /**
     * constructor by variables.
     * @param x1
     * @param y1
     * @param z1
     */
    public geoLocation(double x1 , double y1, double z1){
        this.x = x1;
        this.y = y1;
        this.z = z1;
    }

    /**
     * Deep copy constructor.
     * @param other - geo_location
     */
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

    /**
     * Returns the distance between two geoLocations points.
     * @param g - geo_location
     * @return the distance between this and g.
     */
    @Override
    public double distance(geo_location g) {
        double nx = Math.pow(this.x-g.x(),2);
        double ny = Math.pow(this.y-g.y(),2);
        double nz = Math.pow(this.z-g.z(),2);
        double distance = Math.sqrt(nx+ny+nz);
        return distance;
    }

    /**
     * toString method.
     * @return geo_location as String.
     */
    @Override
    public String toString() {
        return  +this.x + ", " + this.y + ", " + this.z;
    }

    /**
     * Returns true if this are equal to another geo_location.
     * Consequently, if both arguments are null, true is returned
     * and if exactly one argument is null, false is returned.
     * Otherwise, equality is determined by comparing all the fields of the object.
     * @param o - an Object.
     * @return true if the arguments are equal to each other and false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        geoLocation that = (geoLocation) o;
        return Double.compare(that.x, x) == 0 &&
                Double.compare(that.y, y) == 0 &&
                Double.compare(that.z, z) == 0;
    }

    /**
     * Override hashcode because equals changed.
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
