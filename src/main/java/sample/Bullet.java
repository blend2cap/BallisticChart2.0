//This is the Bullet class used to perform calculations.
package sample;

public class Bullet {

    private int ID;
    private String name;
    private Double mass;
    private Double BC;
    private Double caliber;
    private Double velocity;

    public Bullet(String name, double mass, double BC, double caliber, double velocity) {
        this.name = name;
        this.mass = mass;
        this.BC = BC;
        this.caliber = caliber;
        this.velocity = velocity;
    }

    public Bullet(int ID, String name, Double mass, Double BC, Double caliber, Double velocity) {
        this.ID = ID;
        this.name = name;
        this.mass = mass;
        this.BC = BC;
        this.caliber = caliber;
        this.velocity = velocity;
    }



    public String getName() {
        return name;
    }


    public Double getMass() {
        return mass;
    }


    public Double getVelocity() {
        return velocity;
    }

    public Double getCaliber() {
        return caliber;
    }

    public Double getBC() {
        return BC;
    }

}
