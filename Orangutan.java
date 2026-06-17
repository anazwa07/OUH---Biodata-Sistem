package com.biodataouh;

public class Orangutan {
    private int id;
    private String nama;
    private int umur;
    private String jenisKelamin;
    private String statusKonservasi;
    private String spesies;
    private double beratBadan;
    private double tinggiBadan;
    private String lokasiHabitat;
    private String deskripsi;
    private String foto;

    // CONSTRUCTOR UTAMA: Wajib urut seperti ini agar DatabaseManager tidak error
    public Orangutan(int id, String nama, int umur, String jenisKelamin, String statusKonservasi, 
                     String spesies, double beratBadan, double tinggiBadan, String lokasiHabitat, 
                     String deskripsi, String foto) {
        this.id = id;
        this.nama = nama;
        this.umur = umur;
        this.jenisKelamin = jenisKelamin;
        this.statusKonservasi = statusKonservasi;
        this.spesies = spesies;
        this.beratBadan = beratBadan;
        this.tinggiBadan = tinggiBadan;
        this.lokasiHabitat = lokasiHabitat;
        this.deskripsi = deskripsi;
        this.foto = foto;
    }

    // GETTER METHODS
    public int getId() { return id; }
    public String getNama() { return nama; }
    public int getUmur() { return umur; }
    public int getStatusUmur() { return umur; } // Cadangan aman jika ada kode lama yang memanggil getStatusUmur()
    public String getJenisKelamin() { return jenisKelamin; }
    public String getStatusKonservasi() { return statusKonservasi; }
    public String getSpesies() { return spesies; }
    public double getBeratBadan() { return beratBadan; }
    public double getTinggiBadan() { return tinggiBadan; }
    public String getLokasiHabitat() { return lokasiHabitat; }
    public String getDeskripsi() { return deskripsi; }
    public String getFoto() { return foto; }

    // SETTER METHODS
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setUmur(int umur) { this.umur = umur; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }
    public void setStatusKonservasi(String statusKonservasi) { this.statusKonservasi = statusKonservasi; }
    public void setSpesies(String spesies) { this.spesies = spesies; }
    public void setBeratBadan(double beratBadan) { this.beratBadan = beratBadan; }
    public void setTinggiBadan(double tinggiBadan) { this.tinggiBadan = tinggiBadan; }
    public void setLokasiHabitat(String lokasiHabitat) { this.lokasiHabitat = lokasiHabitat; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public void setFoto(String foto) { this.foto = foto; }
}