package sample;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static sample.CartridgeConnection.*;

public class Controller implements ChangeListener, Initializable{


    @FXML private JFXToggleButton MultiplePaths;
    @FXML private JFXToggleButton SpeedGraph;
    @FXML private JFXTextField Range;
    @FXML private JFXTextField NameField;
    @FXML private JFXSlider RangeSlider;
    @FXML private Label dropLabel;
    @FXML private Label finalVelLabel;
    public static Bullet bullet;
    public static Double range;


    public void setRangeSlider() {
        RangeSlider.setValue(Double.parseDouble(Range.getText()));
    }

    public void Calculate() throws IOException, SQLException, ClassNotFoundException {

        bullet=selectedBullet(NameField.getText());
        range=Double.parseDouble(Range.getText());
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BulletDrop.fxml"));
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("BulletDrop.fxml"))));
        //stage.setResizable(false);
        loader.load();
        loader.getController();
        stage.show();
        dropLabel.setVisible(true);
        finalVelLabel.setVisible(true);
        dropLabel.setText("Fall-Off:    "+String.valueOf(Math.round(DropChartController.finalDrop*100))+"cm");
        finalVelLabel.setText("Final Velocity:  "+String.valueOf(Math.round(DropChartController.finalVel))+"m/s");
        if (SpeedGraph.isSelected()) openVelocityChart();

    }
    public void openVelocityChart() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("VelocityChart.fxml"));
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("VelocityChart.fxml"))));
        stage.setResizable(false);
        loader.load();
        loader.getController();
        stage.show();
    }
    public void openAddBullet() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addNewBullet.fxml"));
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("addNewBullet.fxml"))));
        stage.setResizable(false);
        loader.load();
        loader.getController();
        stage.show();
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        Range.setText(String.valueOf(Math.round(RangeSlider.getValue())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RangeSlider.valueProperty().addListener(this);
        try {
            TextFields.bindAutoCompletion(NameField, GetBulletNames());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        dropLabel.setVisible(false);
        finalVelLabel.setVisible(false);
    }
}
