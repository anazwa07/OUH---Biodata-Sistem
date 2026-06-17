package com.biodataouh;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/database_ouh";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String cekLogin(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("role");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Orangutan> ambilSemuaOrangutan() {
        ObservableList<Orangutan> daftarOrangutan = FXCollections.observableArrayList();
        String query = "SELECT * FROM orangutan";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                daftarOrangutan.add(new Orangutan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getInt("umur"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("status_konservasi"),
                    rs.getString("spesies"),         
                    rs.getDouble("berat_badan"),     
                    rs.getDouble("tinggi_badan"),    
                    rs.getString("lokasi_habitat"),  
                    rs.getString("deskripsi"),
                    rs.getString("foto")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarOrangutan;
    }

    public static boolean tambahOrangutan(String nama, int umur, String jk, String status, String spesies, double berat, double tinggi, String habitat, String deskripsi, String foto) {
        String query = "INSERT INTO orangutan (nama, umur, jenis_kelamin, status_konservasi, spesies, berat_badan, tinggi_badan, lokasi_habitat, deskripsi, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setInt(2, umur);
            stmt.setString(3, jk);
            stmt.setString(4, status);
            stmt.setString(5, spesies);
            stmt.setDouble(6, berat);
            stmt.setDouble(7, tinggi);
            stmt.setString(8, habitat);
            stmt.setString(9, deskripsi);
            stmt.setString(10, foto);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ubahOrangutan(int id, String nama, int umur, String jk, String status, String spesies, double berat, double tinggi, String habitat, String deskripsi, String foto) {
        String query = "UPDATE orangutan SET nama=?, umur=?, jenis_kelamin=?, status_konservasi=?, spesies=?, berat_badan=?, tinggi_badan=?, lokasi_habitat=?, deskripsi=?, foto=? WHERE id=?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setInt(2, umur);
            stmt.setString(3, jk);
            stmt.setString(4, status);
            stmt.setString(5, spesies);
            stmt.setDouble(6, berat);
            stmt.setDouble(7, tinggi);
            stmt.setString(8, habitat);
            stmt.setString(9, deskripsi);
            stmt.setString(10, foto);
            stmt.setInt(11, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean hapusOrangutan(int id) {
        String query = "DELETE FROM orangutan WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // ── PERUBAHAN: ambilSemuaDeveloper sekarang membaca kolom foto_path ──
    // CATATAN: Pastikan kolom foto_path sudah ada di tabel developer.
    // Jalankan: ALTER TABLE developer ADD COLUMN foto_path VARCHAR(512) DEFAULT '';
    public static ObservableList<Developer> ambilSemuaDeveloper() {
        ObservableList<Developer> daftarDeveloper = FXCollections.observableArrayList();
        String query = "SELECT * FROM developer ORDER BY id DESC";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String fotoPath = "";
                try { fotoPath = rs.getString("foto_path"); } catch (SQLException ex) { /* kolom belum ada, abaikan */ }
                daftarDeveloper.add(new Developer(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("nim"),
                    rs.getString("kelas"),
                    fotoPath
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daftarDeveloper;
    }

    public static void hapusDeveloper(int id) {
        String query = "DELETE FROM developer WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ── PERUBAHAN: tambahDeveloper sekarang menyimpan fotoPath ──
    public static void tambahDeveloper(String nama, String nim, String kelas, String fotoPath) {
        String query = "INSERT INTO developer (nama, nim, kelas, foto_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setString(2, nim);
            stmt.setString(3, kelas);
            stmt.setString(4, fotoPath != null ? fotoPath : "");
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Fallback: coba tanpa foto_path jika kolom belum ada
            try (Connection conn2 = connect(); PreparedStatement stmt2 = conn2.prepareStatement("INSERT INTO developer (nama, nim, kelas) VALUES (?, ?, ?)")) {
                stmt2.setString(1, nama); stmt2.setString(2, nim); stmt2.setString(3, kelas);
                stmt2.executeUpdate();
            } catch (SQLException e2) { e2.printStackTrace(); }
        }
    }

    // Overload lama agar tidak ada CompileError jika ada kode lain yang masih memanggil 3 param
    public static void tambahDeveloper(String nama, String nim, String kelas) {
        tambahDeveloper(nama, nim, kelas, "");
    }

    // ── PERUBAHAN: ubahDeveloper sekarang menyimpan fotoPath ──
    public static void ubahDeveloper(int id, String nama, String nim, String kelas, String fotoPath) {
        String query = "UPDATE developer SET nama = ?, nim = ?, kelas = ?, foto_path = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nama);
            stmt.setString(2, nim);
            stmt.setString(3, kelas);
            stmt.setString(4, fotoPath != null ? fotoPath : "");
            stmt.setInt(5, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Fallback: coba tanpa foto_path jika kolom belum ada
            try (Connection conn2 = connect(); PreparedStatement stmt2 = conn2.prepareStatement("UPDATE developer SET nama = ?, nim = ?, kelas = ? WHERE id = ?")) {
                stmt2.setString(1, nama); stmt2.setString(2, nim); stmt2.setString(3, kelas); stmt2.setInt(4, id);
                stmt2.executeUpdate();
            } catch (SQLException e2) { e2.printStackTrace(); }
        }
    }

    // Overload lama
    public static void ubahDeveloper(int id, String nama, String nim, String kelas) {
        ubahDeveloper(id, nama, nim, kelas, "");
    }
}