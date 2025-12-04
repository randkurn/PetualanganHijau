package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import model.AudioManager;
import model.SettingsManager;

public class OptionsController {

    @FXML
    private Slider sliderVolume;

    @FXML
    private CheckBox chkMute;

    @FXML
    private Label lblValue;

    @FXML
    private Button btnClose;

    @FXML
    public void initialize() {
        SettingsManager.load(); // pastikan nilai terbaru
        sliderVolume.setMin(0);
        sliderVolume.setMax(100);
        sliderVolume.setValue(SettingsManager.getRawVolume() * 100);
        chkMute.setSelected(SettingsManager.isMuted());
        updateLabel();

        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            SettingsManager.setVolume(newVal.doubleValue() / 100.0);
            AudioManager.refreshVolume();
            updateLabel();
        });

        chkMute.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SettingsManager.setMuted(newVal);
            AudioManager.refreshVolume();
            updateLabel();
        });
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    private void updateLabel() {
        double currentVolume = SettingsManager.isMuted() ? 0 : SettingsManager.getRawVolume();
        lblValue.setText(String.format("%.0f%%", currentVolume * 100));
    }
}

