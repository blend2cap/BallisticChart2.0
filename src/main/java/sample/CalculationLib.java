package sample;

import javax.vecmath.Vector3d;
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
    private static double CalculateVerticalDragAcceleration(double Cd, Bullet bullet, double istantSpeed) {
        double diameter = bullet.getCaliber() * CALTOMETRIC.get();
        double mass= bullet.getMass()*GRTOKG.get();
        double area = Math.pow(diameter/2,2)*Math.PI;
        double Fy = 0.5 * AIRDENSITY.get() * Math.pow(istantSpeed, 2) * Cd * area;
        return (Fy-(mass*G.get()))/(mass*AIRLIFTCORRECTION.get());
    }

    //calculates momentary deceleration due to air drag resistance
    private static double CalcHorizontalDragAcceleration(Bullet bullet, double Cd, double instantSpeed){
        double diameter = bullet.getCaliber()*CALTOMETRIC.get();
        double mass= bullet.getMass()*GRTOKG.get();
        double area = Math.pow((diameter/2),2)*Math.PI;
        return -AIRDENSITY.get()*Math.pow(instantSpeed,2)*Cd*area/(2*mass); //instant speed should take in account wind velocity
    }

    private static double findClosestBc(double speed) throws SQLException, ClassNotFoundException {
        for (GFunction it : CartridgeConnection.GetGFunctions()) {
            if (it.Speed >= speed) {
                return it.G1;
            }
        }
        return 0;
    }
    static void EulerCalculation(ArrayList<Point> position, ArrayList<Point> velocity, Bullet bullet, Double range) throws SQLException, ClassNotFoundException {
        velocity.add(new Point(bullet.getVelocity(), 0D));
        position.add(new Point(0D, 0D));
        int i=1;
        Double step=0.01;   //calculation resolution
        do {
            Double gFunction=findClosestBc(velocity.get(i-1).x);
            Double Cd = CalculationLib.CalculateCd(bullet, gFunction);
            Double acceleration = CalculationLib.CalcHorizontalDragAcceleration(bullet, Cd, velocity.get(i-1).x);

            velocity.add(new Point(velocity.get(i-1).x + acceleration * step));
            position.add(new Point(position.get(i - 1).x + (velocity.get(i - 1).x * step) + (0.5 * acceleration * Math.pow(step, 2))));
            velocity.set(i-1, new Point(velocity.get(i).x, velocity.get(i-1).y)); //leaves y untouched
            //end X axis calc
            velocity.set(i, new Point(velocity.get(i).x, velocity.get(i-1).y+ CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocity.get(i-1).y)*step));
            position.set(i, new Point(position.get(i).x, position.get(i-1).y+ velocity.get(i-1).y * step - 0.5 * CalculationLib.CalculateVerticalDragAcceleration(Cd, bullet, velocity.get(i-1).y)*Math.pow(step, 2)));
            velocity.set(i-1, new Point(velocity.get(i-1).x, velocity.get(i).y));
            i++;
            //end y axis calc
        }while (position.get(i-1).x < range);
    }

    //TODO: write Heun's method
    static void HeunIntegration(){
        float h=0.01f; //stepSize

    }
}
