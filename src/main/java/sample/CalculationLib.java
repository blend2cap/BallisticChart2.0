package sample;

import org.jscience.mathematics.vector.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import static sample.PhyConstants.*;

class CalculationLib {

    // calculates bullet Cd based on G1 drag model
    private static double CalculateCd(Bullet bullet, Double gFunction){
        double caliber= bullet.getCaliber() * CALTOMETRIC.get();
        double mass = bullet.getMass() * GRTOKG.get();
        return mass/Math.pow(caliber,2)*gFunction/bullet.getBC()*METRICONVERSION.get();
    }

    //calculates Vertical Drag Acceleration
    private static Vector3d CalculateVerticalDragAcceleration(double Cd, Bullet bullet, Double velocity_y) {
        double diameter = bullet.getCaliber() * CALTOMETRIC.get();
        double mass = bullet.getMass() * GRTOKG.get();
        double area = Math.pow(diameter / 2, 2) * Math.PI * 16;
        double Fy = 0.5 * AIRDENSITY.get() * Math.pow(velocity_y, 2) * Cd * area;
        double F_gravity = mass * G.get();
        double ay = (Fy - F_gravity) / (mass*AIRLIFTCORRECTION.get());
        return new Vector3d(0d, ay, 0d);
    }

    //calculates momentary deceleration due to air drag resistance
    private static Vector3d CalcHorizontalDragAcceleration(Bullet bullet, double Cd, Vector3d velocity){
        double diameter = bullet.getCaliber() * CALTOMETRIC.get();
        double mass = bullet.getMass() * GRTOKG.get();
        double area = Math.pow((diameter / 2), 2) * Math.PI;
        double Fx = -0.5*AIRDENSITY.get() * Math.pow(velocity.x,2)*Cd*area;
        double ax = Fx/mass;
        return new Vector3d(ax, 0d, 0d); //velocity should be affected by wind
    }

    private static double findClosestBc(double speed) throws SQLException, ClassNotFoundException {
        for (GFunction it : CartridgeConnection.GetGFunctions()) {
            if (it.Speed >= speed) {
                switch (Controller.GModel) {
                    case "G1":   return it.G1;
                    case "G2": return  it.G2;
                    case "G5": return it.G5;
                    case "G6": return  it.G6;
                    case "G7": return  it.G7;
                    case "G8": return  it.G8;
                    default: return  it.G7;
                }
            }
        }
        return 0;
    }
    static void EulerIntegration(ArrayList<Point3d> positionList, ArrayList<Vector3d> velocityList, Bullet bullet, Double range) throws SQLException, ClassNotFoundException {
        Vector3d velocityAtZero = new Vector3d(bullet.getMuzzleVelocity().x, 0d, 0d);
        velocityList.add(velocityAtZero);
        Point3d positionAtZero = new Point3d();
        positionList.add(positionAtZero);
        int i=1;
        Double step=0.01;   //calculation resolution (h)
        do {
            Double gFunction=findClosestBc(velocityList.get(i-1).x);
            Double Cd = CalculationLib.CalculateCd(bullet, gFunction);
            Vector3d acceleration_x = CalculationLib.CalcHorizontalDragAcceleration(bullet, Cd, velocityList.get(i-1));
            acceleration_x.scale(step);
            Vector3d nextVelocity = new Vector3d();
            nextVelocity.add(velocityList.get(i - 1), acceleration_x);
            velocityList.add(nextVelocity);
            Point3d newPosition = new Point3d();
            newPosition.x = positionList.get(i - 1).x + (velocityList.get(i - 1).x * step) + (0.5 * acceleration_x.x * Math.pow(step, 2));
            positionList.add(newPosition);
            //new Velocity for previous calculation
            Vector3d newVelocity = new Vector3d(velocityList.get(i).x, velocityList.get(i-1).y, 0d);
            velocityList.set(i-1, newVelocity); //store velocity of previous calculation
            //end X axis calc

            Vector3d scaledVerticalAcceleration = CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocityList.get(i-1).y);
            scaledVerticalAcceleration.scale(step);
            newVelocity.add(scaledVerticalAcceleration);
            velocityList.set(i, newVelocity);
            newPosition.y = (positionList.get(i - 1).y + (velocityList.get(i - 1).y * step)) - (0.5 *
                    CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocityList.get(i - 1).y).y * Math.pow(step, 2));
            positionList.set(i, newPosition);
            velocityList.set(i-1, new Vector3d(velocityList.get(i-1).x, velocityList.get(i).y, 0d));
            //update old value
            i++;
            //end y axis calc
        }while (positionList.get(i-1).x < range);
    }

    //TODO: write Heun's method
    static void HeunIntegration(ArrayList<Point3d> positionList, ArrayList<Vector3d> velocityList, Bullet bullet, Double range) throws SQLException, ClassNotFoundException {
        Vector3d velocityAtZero = new Vector3d(bullet.getMuzzleVelocity().x, 0d, 0d);
        velocityList.add(velocityAtZero);
        Point3d positionAtZero = new Point3d();
        positionList.add(positionAtZero);
        int i=1;
        Double step=0.01;   //calculation resolution (h)
        do {
            Double gFunction=findClosestBc(velocityList.get(i-1).x);
            Double Cd = CalculationLib.CalculateCd(bullet, gFunction);
            Vector3d acceleration_x = CalculationLib.CalcHorizontalDragAcceleration(bullet, Cd, velocityList.get(i-1));
            acceleration_x.scale(step);
            Vector3d nextVelocity = new Vector3d();
            nextVelocity.add(velocityList.get(i - 1), acceleration_x);
            velocityList.add(nextVelocity);
            Point3d newPosition = new Point3d();
            newPosition.x = positionList.get(i - 1).x + (velocityList.get(i - 1).x * step) + (0.5 * acceleration_x.x * Math.pow(step, 2));
            positionList.add(newPosition);
            //new Velocity for previous calculation
            Vector3d newVelocity = new Vector3d(velocityList.get(i).x, velocityList.get(i-1).y, 0d);
            velocityList.set(i-1, newVelocity); //store velocity of previous calculation
            //end X axis calc

            Vector3d scaledVerticalAcceleration = CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocityList.get(i-1).y);
            scaledVerticalAcceleration.scale(step);
            newVelocity.add(scaledVerticalAcceleration);
            velocityList.set(i, newVelocity);
            newPosition.y = (positionList.get(i - 1).y + (velocityList.get(i - 1).y * step)) - (0.5 *
                    CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocityList.get(i - 1).y).y * Math.pow(step, 2));

            positionList.set(i, newPosition);

            velocityList.set(i-1, new Vector3d(velocityList.get(i-1).x, velocityList.get(i).y, 0d));
            //update old value
            i++;
            //end y axis calc
        }while (positionList.get(i-1).x < range);

    }
}
