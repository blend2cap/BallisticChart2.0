package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class VelocityController implements Initializable {
    @FXML
    private LineChart<String, Double> velocityChart;
    private XYChart.Series<String, Double> series;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        series=new XYChart.Series<>();
        series.setName(Controller.bullet.getName());
        velocityChart.getData().add(series);
        velocityChart.setCreateSymbols(false);
        double stepSize;
        if (DropChartController.velocity.size()<20)
            stepSize=1;
        else
            stepSize=Math.floor(DropChartController.velocity.size()/20 *100)/100;
        for (double i=0; i<DropChartController.velocity.size(); i+=stepSize)
            series.getData().add(new XYChart.Data<>(String.valueOf(Math.round(DropChartController.position.get((int) Math.round(i)).x)), DropChartController.velocity.get((int) Math.round(i)).x));
    }
}
