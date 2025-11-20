package model;

import java.time.LocalDate;

public class SampahModel {
    private String id;
    private String nama;
    private String jenis;
    private double berat;
    private LocalDate tanggal;

    public SampahModel(String id, String nama, String jenis, double berat, LocalDate tanggal) {
        this.id = id;
        this.nama = nama;
        this.jenis = jenis;
        this.berat = berat;
        this.tanggal = tanggal;
    }

    // Getter
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getJenis() { return jenis; }
    public double getBerat() { return berat; }
    public LocalDate getTanggal() { return tanggal; }

    // Setter
    public void setId(String id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setBerat(double berat) { this.berat = berat; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
}
