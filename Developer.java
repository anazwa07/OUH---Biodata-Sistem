package com.biodataouh;

public class Developer {
    private int id;
    private String nama;
    private String nim;
    private String kelas;
    // ── PERUBAHAN: Tambah field foto path untuk profil developer ──
    private String fotoPath;

    // CONSTRUCTOR UTAMA (lama, 4 param) — tetap ada agar backward-compatible
    public Developer(int id, String nama, String nim, String kelas) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.kelas = kelas;
        this.fotoPath = "";
    }

    // CONSTRUCTOR BARU (5 param) — digunakan saat DatabaseManager membaca kolom foto_path
    public Developer(int id, String nama, String nim, String kelas, String fotoPath) {
        this.id = id;
        this.nama = nama;
        this.nim = nim;
        this.kelas = kelas;
        this.fotoPath = (fotoPath != null) ? fotoPath : "";
    }

    // GETTER METHODS
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getNim() { return nim; }
    public String getKelas() { return kelas; }
    public String getFotoPath() { return fotoPath; }

    // SETTER METHODS
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setNim(String nim) { this.nim = nim; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    public void setFotoPath(String fotoPath) { this.fotoPath = (fotoPath != null) ? fotoPath : ""; }
}