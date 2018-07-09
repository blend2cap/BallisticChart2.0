package sample;

import javax.vecmath.Vector3d;

public class Wind {
    Vector3d components = new Vector3d();
    public Wind(double speed, double orientation) {
        double angleRad = (Math.PI * orientation)/180;
        components.x = Math.cos(angleRad) * speed;
        components.y = -Math.sin(angleRad) * speed;
        components.z = 0d;
    }
}
