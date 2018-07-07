package sample;

import javax.vecmath.Vector3d;

public class Wind {
    Vector3d components;
    public Wind(double speed, Double orientation) {
        double angleRad = (Math.PI * orientation)/180;
        components.x = Math.cos(angleRad) * speed;
        components.y = -Math.sin(angleRad) * speed;
        components.z=0d;
    }
}
