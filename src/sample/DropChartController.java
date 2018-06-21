package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class DropChartController implements Initializable {

    @FXML private LineChart<String, Number> dropChart;
    private  XYChart.Series<String, Number> series;
    CalculatePathVelocity calculatePathVelocity;
    static ArrayList<Point> position;
    static ArrayList<Point> velocity;
    public static Double finalVel;
    public static Double finalDrop;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        series = new XYChart.Series<>();
        series.setName(Controller.bullet.getName());
        dropChart.getData().add(series);
        dropChart.setCreateSymbols(false);
        position=new ArrayList<>();
        velocity=new ArrayList<>();
        calculatePathVelocity = new CalculatePathVelocity(Controller.bullet, Controller.range );
        try {
            calculatePathVelocity.Calculate(position, velocity);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        double stepSize;
        if (position.size()<20)
            stepSize=1;
        else
            stepSize=Math.floor(position.size()/20 *100)/100;
        for (double i=0; i<position.size(); i+=stepSize)
            series.getData().add(new XYChart.Data<>(String.valueOf(Math.round(position.get((int) Math.round(i)).x)), position.get((int) Math.round(i)).y));
        finalDrop=position.get(position.size()-1).y;
        finalVel = velocity.get(velocity.size()-1).x;
    }
}