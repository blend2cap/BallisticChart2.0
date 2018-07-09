package sample;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javax.vecmath.Vector3d;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import static sample.CartridgeConnection.*;

public class Controller implements Initializable{

    @FXML private JFXToggleButton MultiplePathsSwitch;
    @FXML private JFXToggleButton SpeedGraphSwitch;
    @FXML private JFXTextField RangeField;
    @FXML private JFXTextField NameField;
    @FXML private JFXSlider RangeSlider;
    @FXML private Label dropLabel;
    @FXML private Label finalVelLabel;
    @FXML private ComboBox<Label> GCombo = new ComboBox<>();
    @FXML private  JFXTextField windSpeedField;
    @FXML private  JFXTextField windOrientationField;
    @FXML private  JFXTextField shootingAngleField;
    @FXML private  JFXTextField scopeElevationField;

    static BulletPhysical bullet;
    static Wind wind;
    static Double range;
    static  String GModel;
    static ArrayList<Double> times=new ArrayList<>();
    static ArrayList<Vector3d> positions;
    static ArrayList<Vector3d> velocities;

    public void setRangeSlider() {
        RangeSlider.setValue(Double.parseDouble(RangeField.getText()));
    }

    private void loadController(String path, boolean resizable) throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(path))));
        stage.setResizable(resizable);
        loader.load();
        loader.getController();
        stage.show();
    }

    public void Calculate() throws IOException, SQLException, ClassNotFoundException {

        try {
            Bullet bulletFromDB = selectedBullet(NameField.getText());
            bullet = new BulletPhysical(bulletFromDB, Double.parseDouble(shootingAngleField.getText()),
                    Double.parseDouble(scopeElevationField.getText()));
            range = Double.parseDouble(RangeField.getText()); //flight time in ms for testing
            wind = new Wind(Double.parseDouble(windSpeedField.getText()), Double.parseDouble(windOrientationField.getText()));
        }
        catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Fill all fields");
            alert.showAndWait();
            return;
        }
        loadController("/fxml/BulletDrop.fxml", true);
        dropLabel.setVisible(true);
        finalVelLabel.setVisible(true);
        //dropLabel.setText("Fall-Off:    "+String.valueOf(Math.round(DropChartController.finalDrop*100))+"cm");
       // finalVelLabel.setText("Final Velocity:  "+String.valueOf(Math.round(DropChartController.finalVel))+"m/s");
        if (SpeedGraphSwitch.isSelected()) openVelocityChart();

    }
    private void openVelocityChart() throws IOException {
        loadController("/fxml/VelocityChart.fxml", false);
    }

    public void openAddBullet() throws IOException {
        loadController("/fxml/addNewBullet.fxml", false);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GModel="G7";
        RangeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Long value = Math.round(Double.parseDouble(newValue.toString()));
            RangeField.setText(value.toString());
        });
        GCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.getText();
            GModel = value;
        });
        try {
            TextFields.bindAutoCompletion(NameField, GetBulletNames());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        //dropLabel.setVisible(false);
        //finalVelLabel.setVisible(false);
        GCombo.getItems().add(new Label("G1"));
        GCombo.getItems().add(new Label("G2"));
        GCombo.getItems().add(new Label("G5"));
        GCombo.getItems().add(new Label("G6"));
        GCombo.getItems().add(new Label("G7"));
        GCombo.getItems().add(new Label("G8"));


    }


}
