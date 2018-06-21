package sample;

import java.sql.SQLException;
import java.util.ArrayList;

import static sample.CartridgeConnection.findClosestBc;

public class CalculatePathVelocity {

    private Bullet bullet;
    private Double range;

    public CalculatePathVelocity(Bullet bullet, Double range) {
        this.bullet = bullet;
        this.range = range;

    }

    public void Calculate(ArrayList<Point> position, ArrayList<Point> velocity) throws SQLException, ClassNotFoundException {
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
            System.out.println("Ypos: "+position.get(i-1).y);
        }while (position.get(i-1).x < range);
    }

}
