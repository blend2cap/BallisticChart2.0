package sample;

//Calculate position, velocity, deflection angle, gradient angle, vertex info
//Using Heun's method for differentation https://en.wikipedia.org/wiki/Heun%27s_method


import com.google.common.collect.ImmutableList;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

class Calculator {
    static double Convert_MOA_Rad(double MOA) {
        return (Math.PI * MOA) / 10_800;
    }

    private static double h,hdid, hnext;
    private static final double eps = 1.0e-10d;
    private static final double tiny = 1.0e-30;
    private static int step; //step used in for loop
    private static final int n_steps = 1_000;
    private static double t = 0, previous_t = 0;
    private static double dtsav;
    private static double tsav;
    private static int model;
    private static BulletPhysical bullet;
    private static DerivativeInfo derivative = new DerivativeInfo(); //dxdts
    private static Info averagedInstantValues = new Info(); //xout used in AverageDerivative() and rzzetr()
    private static Wind wind;
    private static ArrayList<Info> instantValuesList = new ArrayList<>(); //xp in VB
    private static Info instantValue; //x array in VB
    private static Info scaledValues = new Info(); //instant values multiplied by step. xscal array in VB.
    private static VertexValues vertexValue = new VertexValues();
    private static Info instantValuesAtDerivativeUp = null; //xm(i)
    private static Info instantValuesAtDerivativeLow = new Info(); //xn(i)
    private static Info errorValues = new Info();

    static class VertexValues {
        double velocityZ = 100_000;//initialized with big value so can enter if statement the first time, then gets updated only when bullet.z is greater than it
        double distanceAtMaxHeight;
        double maxHeight;
        double timeAtMaxHeight;
        double heightZ = 100_000; //check when we reach second zero
        double distanceAtZero; //distance in X axis from starting position to the second intersection between bore sight line and scope sight line AKA scope zero
        double timeAtZero;
    }

    static class Info {
        double time;
        Vector3d velocity;
        Vector3d position;
        double deflection;
        double gradient;

        Info(double time, Vector3d velocity, Vector3d position, double deflection, double gradient) {
            this.time = time;
            this.velocity = velocity;
            this.position = position;
            this.deflection = deflection;
            this.gradient = gradient;
        }

        public Info clone(){
            Info clone = new Info();
            clone.position = this.position;
            clone.velocity = this.velocity;
            clone.gradient = this.gradient;
            clone.deflection = this.deflection;
            clone.time = this.time;
            return clone;
        }
        Info() {
        }
    }

    static class DerivativeInfo {
        double accelerationX, accelerationY, accelerationZ;
        Vector3d velocity;
    }

    static void Calculate(BulletPhysical bulletPhysical, Wind windFromController, double range, int selectedModel) {
        model = selectedModel;
        bullet = bulletPhysical;
        wind = windFromController;
        //derivative = new DerivativeInfo();
        final double flightTime = range / 1_000; //t2
        final double h1 = flightTime / 1_000_000;
        t = previous_t;
        h = h1;
        //derivatives
        dtsav = flightTime / 20;
        tsav = t - dtsav * 2;
        Controller.times.add(0d);
        Controller.positions.add(new Vector3d(0, 0, 0));
        instantValuesList.add(new Info(0, bullet.velocity, new Vector3d(),
                Math.atan(bullet.velocity.y / bullet.velocity.x),
                Math.atan(bullet.velocity.z / bullet.velocity.x)));
        instantValue = instantValuesList.get(0);
        for (step = 0; step < n_steps; step++) {
            Derive(instantValue, derivative);
            scaleValues();
            if (Math.abs(t - tsav) > Math.abs(dtsav) && instantValuesList.size()<100){
                Controller.times.add(previous_t);
                instantValue.deflection = Math.atan(instantValue.velocity.y/instantValue.velocity.x);
                instantValue.gradient = Math.atan(instantValue.velocity.z/instantValue.velocity.x);
                instantValuesList.add(instantValue);
                tsav = t;
            }
            boolean condition = (t + h - flightTime) * (t + h - previous_t) > 0d;
            if (condition)
                h = flightTime - t;
            BsStep();
            //if (hdid==h) nok++ calculation went smooth
            if ((t - flightTime) * (flightTime - previous_t) >= 0d && instantValuesList.size() < 100) {
                //Calculations ended, we can put values in final list (max 100)
                instantValue.deflection = Math.atan(instantValue.velocity.y / instantValue.velocity.x);
                instantValue.gradient = Math.atan(instantValue.velocity.z / instantValue.velocity.x);
                instantValue.time = t;
                instantValuesList.add(instantValue);
                return;
            }
            h = hnext;
        }
    }

    private static void BsStep() {
        int[] sequence = {2,4,6,8,12,16,24,32,48,64,96};
        tsav=t;
        double error=0;
        while (true){
            for (int i = 0; i < sequence.length; i++) {
                AverageDerivative();
                Rzettr(sequence[i], i);
                //measure error, if too big shrink value of h
                error = MeasureError(error);
                if (error < 1d) {
                    t+=h;
                    hdid=h;
                    if (i==5) hnext = h*1.2;
                    if (i==6) hnext = h*0.95;
                    hnext = (h*16)/sequence[i];
                    return;
                }
            }
            h /= 16;

        }
    }

    private static double MeasureError(double error) {
        if (Math.abs(errorValues.velocity.x / scaledValues.velocity.x)>error) error=Math.abs(errorValues.velocity.x / scaledValues.velocity.x);
        if (Math.abs(errorValues.velocity.y/scaledValues.velocity.y)>error) error=Math.abs(errorValues.velocity.y/scaledValues.velocity.y);
        if (Math.abs(errorValues.velocity.z/scaledValues.velocity.z)>error) error=Math.abs(errorValues.velocity.z/scaledValues.velocity.z);
        if (Math.abs(errorValues.position.x/scaledValues.position.x)>error) error=Math.abs(errorValues.position.x/scaledValues.position.x);
        if (Math.abs(errorValues.position.y/scaledValues.position.y)>error) error=Math.abs(errorValues.position.y/scaledValues.position.y);
        if (Math.abs(errorValues.position.z/scaledValues.position.z)>error) error=Math.abs(errorValues.position.z/scaledValues.position.z);
        error /= eps;
        return error;
    }

    //TODO: finish here
    private static void Rzettr(int numSequence, int iter) {
        double test = Math.sqrt(h/numSequence);
        double ddx=0, xx=0,v=0,C=0,B=0, m1;
        if (iter == 1) {
            instantValue = averagedInstantValues.clone(); //xz = xest
            errorValues = averagedInstantValues.clone(); //
            return;
        }
        if (iter < 7) m1 = iter; else m1=7;
        double ft = 1.4783978394802331713130441894294; //(sqrt(3)+sqrt(1.5))/2






        double b1 = Math.sqrt(2) * v;
        B = b1 - C;
        if (B != 0) {
            B=(C-v)/B;
            ddx = C*B;
            C = b1*B;
        }
        else ddx = v;
        xx += ddx;
        errorValues.velocity.x = ddx;








        /*for
        {//test
            //double xx = instantValuesAtDerivativeLow.velocity.x;
            //double v = instantValuesAtDerivativeLow.velocity.y;
            double b1 = instantValuesAtDerivativeLow.velocity.x * Math.sqrt(2);
            double B = b1 - instantValuesAtDerivativeLow.velocity.x;
            double C = instantValuesAtDerivativeLow.velocity.x;
            if (B != 0) {
                B=0;//(instantValuesAtDerivativeLow.velocity.x - )/B;
                ddx = C * B;
                C=b1*B;
            }
            //else ddx=v;
        }*/
    }

    private static void AverageDerivative() { //mmid
        h = htot / n_steps;


        instantValuesAtDerivativeUp = instantValue.clone();

        //v = v+h*a
        instantValuesAtDerivativeLow.velocity.x = instantValue.velocity.x + h * derivative.accelerationX;
        instantValuesAtDerivativeLow.velocity.y = instantValue.velocity.y + h * derivative.accelerationY;
        instantValuesAtDerivativeLow.velocity.z = instantValue.velocity.z + h * derivative.accelerationZ;
        //s = s+h*v
        instantValuesAtDerivativeLow.position.x = instantValue.position.x + h * derivative.velocity.x;
        instantValuesAtDerivativeLow.position.y = instantValue.position.y + h * derivative.velocity.y;
        instantValuesAtDerivativeLow.position.z = instantValue.position.z + h * derivative.velocity.z;
        t = tsav + h;
        Derive(instantValuesAtDerivativeLow, averagedInstantValues);
        for (int j = 0; j < step; j++) {
            UpdateAndSwap(instantValuesAtDerivativeUp, instantValuesAtDerivativeLow, averagedInstantValues);
            t += h;
            Derive(instantValuesAtDerivativeLow, averagedInstantValues);
        }
        // Calculate middle point of the segment between lower and upper derivative and set as final derivative
        UpdateInstantValues(instantValuesAtDerivativeUp, instantValuesAtDerivativeLow, averagedInstantValues);
    }

    private static void UpdateInstantValues(Info derivativeUp, Info derivativeLow, Info averagedValues) {
        averagedValues.velocity.x = 0.5 * (derivativeUp.velocity.x + derivativeLow.velocity.x+ h * averagedValues.velocity.x);
        averagedValues.velocity.y = 0.5 * (derivativeUp.velocity.y + derivativeLow.velocity.y+ h * averagedValues.velocity.y);
        averagedValues.velocity.z = 0.5 * (derivativeUp.velocity.z + derivativeLow.velocity.z+ h * averagedValues.velocity.z);
        averagedValues.position.x = 0.5 * (derivativeUp.position.x + derivativeLow.position.x + h * averagedValues.position.x);
        averagedValues.position.y = 0.5 * (derivativeUp.position.y + derivativeLow.position.y + h * averagedValues.position.y);
        averagedValues.position.z = 0.5 * (derivativeUp.position.z + derivativeLow.position.z + h * averagedValues.position.z);
    }

    private static void UpdateAndSwap(Info instantValuesAtDerivativeUp, Info instantValuesAtDerivativeLow, Info averagedInstantValues) {
        Info swap = new Info();
        swap.velocity.x = instantValuesAtDerivativeUp.velocity.x + 2 * h * averagedInstantValues.velocity.x;
        swap.velocity.y = instantValuesAtDerivativeUp.velocity.y + 2 * h * averagedInstantValues.velocity.y;
        swap.velocity.z = instantValuesAtDerivativeUp.velocity.z + 2 * h * averagedInstantValues.velocity.z;
        swap.position.x = instantValuesAtDerivativeUp.position.x + 2 * h * averagedInstantValues.position.x;
        swap.position.y = instantValuesAtDerivativeUp.position.y + 2 * h * averagedInstantValues.position.y;
        swap.position.z = instantValuesAtDerivativeUp.position.z + 2 * h * averagedInstantValues.position.z;
        instantValuesAtDerivativeUp =  instantValuesAtDerivativeLow.clone();
        instantValuesAtDerivativeLow.velocity = swap.velocity;
        instantValuesAtDerivativeLow.position = swap.position;
    }

    static Vector3d Add(Vector3d a, Vector3d b){
        Vector3d result = new Vector3d();
        result.add(a,b);
        return result;
    }

    private static void Derive(Info instantValue, DerivativeInfo myDerivative) {

        //Derive used in Calculate
        double U = Add(instantValue.velocity, wind.components).length();
        double Ma = U / PhyConstants.MACH_MS.get();
        double Corrector = 0.23324395157568 * Ma;
        //Since GD is calculated in Imperial units, we must correct the
        //the square of feet times meter multiplied by a factor of
        //the relative speed. This factor seems to be about 2,511
        //and may be related to the devices with which
        //BC was calculated (check this out)
        double GD = Corrector * Gavre_calc(Ma) / bullet.getBC();
        //calculate decelerations before multiplying by step
        myDerivative.accelerationX = -GD * (instantValue.velocity.x + wind.components.x);
        myDerivative.accelerationY = -GD * (instantValue.velocity.y + wind.components.y);
        myDerivative.accelerationZ = -GD * (instantValue.velocity.z + wind.components.z) - PhyConstants.G.get();
        myDerivative.velocity.x = instantValue.velocity.x;
        myDerivative.velocity.y = instantValue.velocity.y;
        myDerivative.velocity.z = instantValue.velocity.z;
        if (Math.abs(instantValue.velocity.z) < Math.abs(vertexValue.velocityZ)) {
            vertexValue.velocityZ = instantValue.velocity.z;
            vertexValue.maxHeight = instantValue.position.z;
            vertexValue.distanceAtMaxHeight = instantValue.position.x;
            vertexValue.timeAtMaxHeight = instantValue.time;
        } else if ((instantValue.position.z <= 0d) && (Math.abs(vertexValue.heightZ) > Math.abs(instantValue.position.z))) {
            vertexValue.heightZ = instantValue.position.z;
            vertexValue.distanceAtZero = instantValue.position.x;
            vertexValue.timeAtZero = instantValue.time;
        }
    }

    private static void Derive(Info instantValue, Info myDerivative) {

        //Derive used in AverageDerivative
        double U = Add(instantValue.velocity, wind.components).length();
        double Ma = U / PhyConstants.MACH_MS.get();
        double Corrector = 0.23324395157568 * Ma;
        //Since GD is calculated in Imperial units, we must correct the
        //the square of feet times meter multiplied by a factor of
        //the relative speed. This factor seems to be about 2,511
        //and may be related to the devices with which
        //BC was calculated (check this out)
        double GD = Corrector * Gavre_calc(Ma) / bullet.getBC();
        //calculate decelerations before multiplying by step
        myDerivative.velocity.x = -GD * (instantValue.velocity.x + wind.components.x);
        myDerivative.velocity.y = -GD * (instantValue.velocity.y + wind.components.y);
        myDerivative.velocity.z = -GD * (instantValue.velocity.z + wind.components.z) - PhyConstants.G.get();
        myDerivative.position.x = instantValue.position.x;
        myDerivative.position.y = instantValue.position.y;
        myDerivative.position.z = instantValue.position.z;
        if (Math.abs(instantValue.velocity.z) < Math.abs(vertexValue.velocityZ)) {
            vertexValue.velocityZ = instantValue.velocity.z;
            vertexValue.maxHeight = instantValue.position.z;
            vertexValue.distanceAtMaxHeight = instantValue.position.x;
            vertexValue.timeAtMaxHeight = instantValue.time;
        } else if ((instantValue.position.z <= 0d) && (Math.abs(vertexValue.heightZ) > Math.abs(instantValue.position.z))) {
            vertexValue.heightZ = instantValue.position.z;
            vertexValue.distanceAtZero = instantValue.position.x;
            vertexValue.timeAtZero = instantValue.time;
        }
    }
    private static double Gavre_calc(Double Ma) {
        if (Ma < 1.0e-24) return 0;
        double velocity_fps = Ma * PhyConstants.MACH_FPS.get();
        switch (model) {
            case 7:
                return CD(velocity_fps, GavreParams.G7);
            default:
                //use G7 by default
                return CD(velocity_fps, GavreParams.G7);

        }

    }

    private static double CD(double velocity_fps, ImmutableList<GavreParams> list) {
        for (GavreParams item : list) {
            if (velocity_fps < item.maxFps) {
                //divide CD-value with (8 / [pi rho0])
                return (item.A * Math.pow(velocity_fps, 3) + item.B * Math.pow(velocity_fps, 2) + item.C * velocity_fps + item.D) / 1107.16482150884;
            }
        }
        //if the bullet is faster than the biggest velocity available in the list, return the biggest
        GavreParams fastest = list.get(list.size() - 1);
        return (fastest.A * Math.pow(velocity_fps, 3) + fastest.B * Math.pow(velocity_fps, 2) + fastest.C * velocity_fps + fastest.D) / 1107.16482150884;
    }

    private static void scaleValues() {
        scaledValues.velocity.x = Math.abs(instantValue.velocity.x) + Math.abs(derivative.accelerationX * h) + tiny;
        scaledValues.velocity.y = Math.abs(instantValue.velocity.y) + Math.abs(derivative.accelerationY * h) + tiny;
        scaledValues.velocity.z = Math.abs(instantValue.velocity.z) + Math.abs(derivative.accelerationZ * h) + tiny;
        scaledValues.position.x = Math.abs(instantValue.position.x) + Math.abs(derivative.velocity.x * h) + tiny;
        scaledValues.position.y = Math.abs(instantValue.position.y) + Math.abs(derivative.velocity.y * h) + tiny;
        scaledValues.position.z = Math.abs(instantValue.position.z) + Math.abs(derivative.velocity.z) + tiny;
    }
}
