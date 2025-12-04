package controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.GameStateManager;

public class LoadGameDialogController {

    @FXML
    private ChoiceBox<SlotItem> choiceSlot;

    @FXML
    private Label lblInfo;

    @FXML
    private Button btnLoad;

    private Stage stage;
    private Integer selectedSlot;

    @FXML
    public void initialize() {
        choiceSlot.setConverter(new StringConverter<SlotItem>() {
            @Override
            public String toString(SlotItem object) {
                return object == null ? "" : object.display;
            }

            @Override
            public SlotItem fromString(String string) {
                return null;
            }
        });
    }

    public void setSlotInfos(List<GameStateManager.SlotInfo> infos) {
        var occupied = infos.stream()
            .filter(GameStateManager.SlotInfo::occupied)
            .map(SlotItem::fromInfo)
            .toList();

        choiceSlot.setItems(FXCollections.observableArrayList(occupied));
        boolean hasData = !occupied.isEmpty();
        btnLoad.setDisable(!hasData);
        if (hasData) {
            choiceSlot.getSelectionModel().selectFirst();
            updateInfo();
        } else {
            lblInfo.setText("Belum ada data tersimpan.");
        }
        choiceSlot.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> updateInfo());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void loadSlot() {
        SlotItem item = choiceSlot.getSelectionModel().getSelectedItem();
        if (item == null) {
            lblInfo.setText("Pilih salah satu slot yang tersedia.");
            return;
        }
        selectedSlot = item.slot;
        close();
    }

    @FXML
    private void cancel() {
        selectedSlot = null;
        close();
    }

    private void updateInfo() {
        SlotItem item = choiceSlot.getSelectionModel().getSelectedItem();
        if (item == null) {
            lblInfo.setText("");
            return;
        }
        lblInfo.setText("Slot " + item.slot + " - " + item.playerName + " (" + item.getFormattedTime() + ")");
    }

    private void close() {
        if (stage != null) {
            stage.close();
        }
    }

    public Optional<Integer> getSelectedSlot() {
        return Optional.ofNullable(selectedSlot);
    }

    private record SlotItem(int slot, String playerName, long timestamp, String display) {
        static SlotItem fromInfo(GameStateManager.SlotInfo info) {
            String label = "Slot " + info.slot() + " - " + info.playerName();
            return new SlotItem(info.slot(), info.playerName(), info.timestamp(), label);
        }

        String getFormattedTime() {
            if (timestamp <= 0) return "-";
            return new SimpleDateFormat("dd MMM HH:mm").format(new Date(timestamp));
        }
    }
}

