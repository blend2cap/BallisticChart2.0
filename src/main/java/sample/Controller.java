package sample;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import static sample.CartridgeConnection.*;

public class Controller implements Initializable{

    @FXML private JFXToggleButton MultiplePaths;
    @FXML private JFXToggleButton SpeedGraph;
    @FXML private JFXTextField Range;
    @FXML private JFXTextField NameField;
    @FXML private JFXSlider RangeSlider;
    @FXML private Label dropLabel;
    @FXML private Label finalVelLabel;
    @FXML private ComboBox<Label> GCombo = new ComboBox<>();

    static Bullet bullet;
    static Double range;
    static  String GModel;

    public void setRangeSlider() {
        RangeSlider.setValue(Double.parseDouble(Range.getText()));
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

        bullet=selectedBullet(NameField.getText());
        range=Double.parseDouble(Range.getText());
        loadController("/fxml/BulletDrop.fxml", true);
        dropLabel.setVisible(true);
        finalVelLabel.setVisible(true);
        dropLabel.setText("Fall-Off:    "+String.valueOf(Math.round(DropChartController.finalDrop*100))+"cm");
        finalVelLabel.setText("Final Velocity:  "+String.valueOf(Math.round(DropChartController.finalVel))+"m/s");
        if (SpeedGraph.isSelected()) openVelocityChart();

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
            Range.setText(value.toString());
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
