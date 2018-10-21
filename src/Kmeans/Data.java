package Kmeans;

public class Data {
    private double x;
    private double y;
    private double z;
    private String name;
    public Data(double x, double y, double z, String name) {
        this.x=x;
        this.y=y;
        this.z=z;
        this.name=name;
    }
    public Data() {

    }
    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public double getZ() {
        return z;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}