package sample;

import static sample.PhyConstants.*;

public class CalculationLib {

    public CalculationLib() {
    }

    // calculates bullet Cd based on G1 drag model
    public static double CalculateCd(Bullet bullet, Double gFunction){
        double caliber= bullet.getCaliber() * CALTOMETRIC.get();
        double mass = bullet.getMass() * GRTOKG.get();
        return mass/Math.pow(caliber,2)*gFunction/bullet.getBC()*METRICONVERSION.get();
    }

    //calculates Vertical Drag Acceleration
    public static double CalculateVerticalDragAcceleration(double Cd, Bullet bullet, double istantSpeed) {
        double diameter = bullet.getCaliber() * CALTOMETRIC.get();
        double mass= bullet.getMass()*GRTOKG.get();
        double area = Math.pow(diameter/2,2)*Math.PI;
        double Fy = 0.5 * AIRDENSITY.get() * Math.pow(istantSpeed, 2) * Cd * area;
        return (Fy-(mass*G.get()))/(mass*AIRLIFTCORRECTION.get());
    }

    //calculates momentary deceleration due to air drag resistance
    public static double CalcHorizontalDragAcceleration(Bullet bullet, double Cd, double istantSpeed){
        double diameter = bullet.getCaliber()*CALTOMETRIC.get();
        double mass= bullet.getMass()*GRTOKG.get();
        double area = Math.pow((diameter/2),2)*Math.PI;
        return -AIRDENSITY.get()*Math.pow(istantSpeed,2)*Cd*area/(2*mass); //istant speed should take in account wind velocity
    }
}
