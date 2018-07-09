package sample;

import javax.vecmath.Vector3d;

class BulletPhysical extends Bullet {

    Vector3d velocity;
    double rotationSpeed;
    double deflection; //in degrees
    double gradient; //in degrees

    BulletPhysical(Bullet bulletBase, double shootingAngle, double scopeElevation) {
        super(bulletBase.getName(), bulletBase.getMass(), bulletBase.getBC(), bulletBase.getCaliber(), bulletBase.getMuzzleVelocity());
        double angle = Calculator.Convert_MOA_Rad(shootingAngle) + Calculator.Convert_MOA_Rad(scopeElevation);
        velocity = new Vector3d(Math.cos(angle) * bulletBase.getMuzzleVelocity(), 0, Math.sin(angle) * bulletBase.getMuzzleVelocity());
        this.rotationSpeed = 0d;
        this.deflection = 0d;
        this.gradient = 0d;
    }
}
