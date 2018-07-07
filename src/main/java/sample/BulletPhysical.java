package sample;

import javax.vecmath.Vector3d;

public class BulletPhysical extends Bullet {

    Vector3d position;
    Vector3d velocity;
    double rotationSpeed;
    double deflection; //in degrees
    double gradient; //in degrees

    public BulletPhysical(String name, Double mass, Double BC, Double caliber, Double muzzleVelocity, double shootingAngle, double scopeElevation) {
        //shootingAngle in degrees
        //scopeElevation in MOA
        super(name, mass, BC, caliber, muzzleVelocity);
        double angle = Calculator.Convert_MOA_Rad(shootingAngle) + scopeElevation;
        velocity = new Vector3d(Math.cos(angle) * muzzleVelocity, 0, Math.sin(angle) * muzzleVelocity);
        this.rotationSpeed = 0d;
        this.deflection = 0d;
        this.gradient = 0d;
    }

    public  BulletPhysical(Bullet bulletBase, double shootingAngle, double scopeElevation){
        super(bulletBase.getName(), bulletBase.getMass(), bulletBase.getBC(), bulletBase.getCaliber(), bulletBase.getMuzzleVelocity());
        double angle = Calculator.Convert_MOA_Rad(shootingAngle) + scopeElevation;
        velocity = new Vector3d(Math.cos(angle) * bulletBase.getMuzzleVelocity(), 0, Math.sin(angle) * bulletBase.getMuzzleVelocity());
        this.rotationSpeed = 0d;
        this.deflection = 0d;
        this.gradient = 0d;
    }
}
