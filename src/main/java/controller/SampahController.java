package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.SampahModel;

import java.time.LocalDate;

public class SampahController {

    @FXML private TableView<SampahModel> tblSampah;
    @FXML private TableColumn<SampahModel, String> colId;
    @FXML private TableColumn<SampahModel, String> colNama;
    @FXML private TableColumn<SampahModel, String> colJenis;
    @FXML private TableColumn<SampahModel, Double> colBerat;
    @FXML private TableColumn<SampahModel, LocalDate> colTanggal;

    @FXML private TextField txtId, txtNama, txtBerat;
    @FXML private ComboBox<String> cmbJenis;
    @FXML private DatePicker dateTanggal;
    @FXML private CheckBox chkSekarang;
    @FXML private Button btnTambah, btnUbah, btnHapus;

    private final ObservableList<SampahModel> dataList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set kolom tabel
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colJenis.setCellValueFactory(new PropertyValueFactory<>("jenis"));
        colBerat.setCellValueFactory(new PropertyValueFactory<>("berat"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));

        // Isi pilihan jenis sampah
        cmbJenis.setItems(FXCollections.observableArrayList("Organik", "Anorganik"));

        // Data awal (dummy)
        dataList.addAll(
                new SampahModel("S001", "Randy", "Organik", 2.5, LocalDate.of(2025, 11, 10)),
                new SampahModel("S002", "Ariel", "Anorganik", 3.2, LocalDate.of(2025, 11, 9))
        );

        tblSampah.setItems(dataList);

        // Checkbox untuk tanggal sekarang
        chkSekarang.selectedProperty().addListener((obs, oldVal, newVal) -> {
            dateTanggal.setDisable(newVal);
            if (newVal) dateTanggal.setValue(LocalDate.now());
        });

        // Tombol CRUD
        btnTambah.setOnAction(e -> tambahData());
        btnUbah.setOnAction(e -> ubahData());
        btnHapus.setOnAction(e -> hapusData());

        // Klik tabel isi form
        tblSampah.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                txtId.setText(newSel.getId());
                txtNama.setText(newSel.getNama());
                cmbJenis.setValue(newSel.getJenis());
                txtBerat.setText(String.valueOf(newSel.getBerat()));
                dateTanggal.setValue(newSel.getTanggal());
                chkSekarang.setSelected(false);
            }
        });
    }

    private void tambahData() {
        try {
            String id = txtId.getText().trim();
            String nama = txtNama.getText().trim();
            String jenis = cmbJenis.getValue();
            String beratStr = txtBerat.getText().trim();
            LocalDate tanggal = chkSekarang.isSelected() ? LocalDate.now() : dateTanggal.getValue();

            if (id.isEmpty() || nama.isEmpty() || jenis == null || beratStr.isEmpty() || tanggal == null) {
                showAlert(Alert.AlertType.WARNING, "Peringatan", "Isi semua field dengan benar!");
                return;
            }

            double berat = Double.parseDouble(beratStr);
            SampahModel newSampah = new SampahModel(id, nama, jenis, berat, tanggal);

            dataList.add(newSampah);
            tblSampah.refresh();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan", "Berat harus berupa angka!");
        }
    }

    private void ubahData() {
        SampahModel selected = tblSampah.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setId(txtId.getText());
                selected.setNama(txtNama.getText());
                selected.setJenis(cmbJenis.getValue());
                selected.setBerat(Double.parseDouble(txtBerat.getText()));
                selected.setTanggal(chkSekarang.isSelected() ? LocalDate.now() : dateTanggal.getValue());

                tblSampah.refresh();
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diubah!");
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Kesalahan", "Berat harus berupa angka!");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data terlebih dahulu!");
        }
    }

    private void hapusData() {
        SampahModel selected = tblSampah.getSelectionModel().getSelectedItem();
        if (selected != null) {
            dataList.remove(selected);
            tblSampah.refresh();
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil dihapus!");
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih data untuk dihapus!");
        }
    }

    private void clearForm() {
        txtId.clear();
        txtNama.clear();
        txtBerat.clear();
        cmbJenis.getSelectionModel().clearSelection();
        dateTanggal.setValue(null);
        chkSekarang.setSelected(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
