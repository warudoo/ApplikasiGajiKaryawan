package com.unpam.model;

import java.sql.*;
import java.util.ArrayList;

public class Karyawan {
    private String ktp;
    private String nama;
    private int ruang;
    private String password;
    // Kolom kodePekerjaan sudah dihapus dari model ini sesuai permintaan sebelumnya
    private String pesan;
    private ArrayList<Object[]> list = new ArrayList<>();
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter (Tidak ada perubahan)
    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }
    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }
    public int getRuang() { return ruang; }
    public void setRuang(int ruang) { this.ruang = ruang; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPesan() { return pesan; }
    public Object[][] getList() {
        return list.toArray(new Object[0][]);
    }

    // Metode simpan() dan baca...() tidak diubah
    public boolean simpan() {
        boolean sukses = false;
        try (Connection conn = koneksi.getConnection()) {
            String sql = "INSERT INTO tbkaryawan (ktp, nama, ruang, password) "
                       + "VALUES (?, ?, ?, ?) "
                       + "ON DUPLICATE KEY UPDATE nama=?, ruang=?, password=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ktp);
                stmt.setString(2, nama);
                stmt.setInt(3, ruang);
                stmt.setString(4, password);
                stmt.setString(5, nama);
                stmt.setInt(6, ruang);
                stmt.setString(7, password);
                sukses = stmt.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            pesan = "Error database: " + ex.getMessage();
        }
        return sukses;
    }
    
    public boolean bacaByKtp(String ktp) {
        boolean found = false;
        try (Connection conn = koneksi.getConnection()) {
            String sql = "SELECT ktp, nama, ruang, password FROM tbkaryawan WHERE ktp = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ktp);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    this.ktp = rs.getString("ktp");
                    this.nama = rs.getString("nama");
                    this.ruang = rs.getInt("ruang");
                    this.password = rs.getString("password");
                    found = true;
                }
            }
        } catch (SQLException ex) {
            pesan = "Error database: " + ex.getMessage();
        }
        return found;
    }

    public boolean bacaData() {
        boolean sukses = false;
        list.clear();
        try (Connection conn = koneksi.getConnection()) {
            String sql = "SELECT ktp, nama, ruang FROM tbkaryawan";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("ktp");
                    row[1] = rs.getString("nama");
                    row[2] = rs.getInt("ruang");
                    list.add(row);
                }
                sukses = true;
            }
        } catch (SQLException ex) {
            pesan = "Error database: " + ex.getMessage();
        }
        return sukses;
    }
    
    /**
     * PERBAIKAN UTAMA: Metode hapus sekarang menggunakan transaksi untuk memastikan
     * data gaji terkait dihapus terlebih dahulu sebelum data karyawan.
     * @param ktp KTP karyawan yang akan dihapus.
     * @return true jika kedua operasi berhasil, false jika gagal.
     */
    public boolean hapus(String ktp) {
        Connection conn = null;
        try {
            conn = koneksi.getConnection();
            // 1. Memulai transaksi
            conn.setAutoCommit(false);

            // 2. Hapus data terkait di tbgaji terlebih dahulu
            String sqlGaji = "DELETE FROM tbgaji WHERE ktp = ?";
            try (PreparedStatement stmtGaji = conn.prepareStatement(sqlGaji)) {
                stmtGaji.setString(1, ktp);
                stmtGaji.executeUpdate(); // Jalankan delete, tidak masalah jika tidak ada data yang terhapus
            }

            // 3. Hapus data utama di tbkaryawan
            String sqlKaryawan = "DELETE FROM tbkaryawan WHERE ktp = ?";
            try (PreparedStatement stmtKaryawan = conn.prepareStatement(sqlKaryawan)) {
                stmtKaryawan.setString(1, ktp);
                int rowsAffected = stmtKaryawan.executeUpdate();
                if (rowsAffected > 0) {
                    // 4. Jika semua berhasil, commit transaksi
                    conn.commit();
                    return true;
                } else {
                    // Jika karyawan tidak ditemukan, batalkan transaksi
                    conn.rollback();
                    pesan = "Karyawan dengan KTP " + ktp + " tidak ditemukan.";
                    return false;
                }
            }

        } catch (SQLException ex) {
            // Jika terjadi error SQL, batalkan semua perubahan
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            pesan = "Error database saat menghapus: " + ex.getMessage();
            ex.printStackTrace(); // Tampilkan error lengkap di log server
            return false;
        } finally {
            // Selalu kembalikan ke mode autocommit
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
