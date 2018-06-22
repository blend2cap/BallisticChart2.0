//This is the Bullet class used to perform calculations.
package sample;

import javax.vecmath.Vector3d;

public class Bullet {

    private int ID;
    private String name;
    private Double mass;
    private Double BC;
    private Double caliber;
    private Vector3d muzzleVelocity;

    //used in Add Bullet to DB
    public Bullet(String name, double mass, double BC, double caliber, Double muzzleVelocity) {
        this.name = name;
        this.mass = mass;
        this.BC = BC;
        this.caliber = caliber;
        this.muzzleVelocity = new Vector3d(muzzleVelocity, 0d, 0d);
    }
    //used in application
    public Bullet(int ID, String name, Double mass, Double BC, Double caliber, Double muzzleVelocity) {
        this.ID = ID;
        this.name = name;
        this.mass = mass;
        this.BC = BC;
        this.caliber = caliber;
        this.muzzleVelocity = new Vector3d(muzzleVelocity, 0d, 0d);
    }



    String getName() {
        return name;
    }


    Double getMass() {
        return mass;
    }


    Vector3d getMuzzleVelocity() {
        return muzzleVelocity;
    }

    Double getCaliber() {
        return caliber;
    }

    Double getBC() {
        return BC;
    }

}
