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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.GameStateManager;

public class NewGameDialogController {

    @FXML
    private TextField txtName;

    @FXML
    private ChoiceBox<SlotItem> choiceSlot;

    @FXML
    private Label lblInfo;

    @FXML
    private Button btnStart;

    private Stage stage;
    private NewGameRequest result;

    @FXML
    public void initialize() {
        txtName.setText("Penjelajah");
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
        choiceSlot.setItems(FXCollections.observableArrayList(
            infos.stream().map(SlotItem::fromInfo).toList()
        ));
        if (!choiceSlot.getItems().isEmpty()) {
            choiceSlot.getSelectionModel().selectFirst();
            updateInfoText();
        }
        choiceSlot.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> updateInfoText());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void startNewGame() {
        SlotItem item = choiceSlot.getSelectionModel().getSelectedItem();
        String name = txtName.getText().trim();
        if (item == null) {
            lblInfo.setText("Pilih slot terlebih dahulu.");
            return;
        }
        if (name.isBlank()) {
            lblInfo.setText("Nama tidak boleh kosong.");
            return;
        }
        result = new NewGameRequest(item.slot, name);
        close();
    }

    @FXML
    private void cancel() {
        result = null;
        close();
    }

    private void close() {
        if (stage != null) {
            stage.close();
        }
    }

    private void updateInfoText() {
        SlotItem item = choiceSlot.getSelectionModel().getSelectedItem();
        if (item == null) {
            lblInfo.setText("");
            return;
        }
        lblInfo.setText(item.occupied
            ? "Slot ini berisi data \"" + item.playerName + "\" (" + item.getFormattedTime() + "). Akan ditimpa."
            : "Slot kosong. Data baru akan disimpan di sini.");
    }

    public Optional<NewGameRequest> getResult() {
        return Optional.ofNullable(result);
    }

    public record NewGameRequest(int slot, String name) {}

    private record SlotItem(int slot, boolean occupied, String playerName, long timestamp, String display) {
        static SlotItem fromInfo(GameStateManager.SlotInfo info) {
            String label = "Slot " + info.slot();
            label += info.occupied() ? " - " + info.playerName() : " - Kosong";
            return new SlotItem(info.slot(), info.occupied(), info.playerName(), info.timestamp(), label);
        }

        String getFormattedTime() {
            if (timestamp <= 0) return "-";
            return new SimpleDateFormat("dd MMM HH:mm").format(new Date(timestamp));
        }
    }
}

