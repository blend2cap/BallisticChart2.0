package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DropChartController implements Initializable {

    @FXML private LineChart<String, Double> dropChart;
    //TODO: create list of series for multiple path plotting
    private  XYChart.Series<String, Double> series;

    static ArrayList<Point3d> position;
    static ArrayList<Vector3d> velocity;
    static Double finalVel;
    static Double finalDrop;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        series = new XYChart.Series<>();
        series.setName(Controller.bullet.getName());
        dropChart.getData().add(series);
        dropChart.setCreateSymbols(false);
        position= new ArrayList<>();
        velocity= new ArrayList<>();
        try {
            CalculationLib.EulerIntegration(position, velocity, Controller.bullet, Controller.range);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        double stepSize;
        if (position.size()<20)
            stepSize=1;
        else
            stepSize=Math.floor(position.size()/20 *100)/100;
        for (float i=0; i<position.size(); i+=stepSize) {
            Double point_x = position.get( Math.round(i)).x;
            Double point_y = position.get( Math.round(i)).y;
            Double point_z = position.get( Math.round(i)).z;
            series.getData().add(new XYChart.Data<>(point_x.toString(), point_y));
        }
        finalDrop=position.get(position.size()-1).y;
        finalVel = velocity.get(velocity.size()-1).x;
    }
}