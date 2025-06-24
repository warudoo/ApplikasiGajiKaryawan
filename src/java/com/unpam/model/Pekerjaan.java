/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unpam.model;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.unpam.view.PesanDialog;
import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 *
 * @author Warudo
 */
public class Pekerjaan {
    private String kodePekerjaan;
    private String namaPekerjaan;
    private int jumlahTugas;
    private String pesan;
    private Object[][] list;
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter
    public String getKodePekerjaan() { return kodePekerjaan; }
    public void setKodePekerjaan(String kodePekerjaan) { this.kodePekerjaan = kodePekerjaan; }
    public String getNamaPekerjaan() { return namaPekerjaan; }
    public void setNamaPekerjaan(String namaPekerjaan) { this.namaPekerjaan = namaPekerjaan; }
    public int getJumlahTugas() { return jumlahTugas; }
    public void setJumlahTugas(int jumlahTugas) { this.jumlahTugas = jumlahTugas; }
    public String getPesan() { return pesan; }
    public Object[][] getList() { return list; }

    public boolean simpan() {
        boolean sukses = false;
        try (Connection conn = koneksi.getConnection()) {
            String sql = "INSERT INTO tbpekerjaan (kodepekerjaan, namapekerjaan, jumlahtugas) "
                       + "VALUES (?, ?, ?) "
                       + "ON DUPLICATE KEY UPDATE namapekerjaan=?, jumlahtugas=?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, kodePekerjaan);
                stmt.setString(2, namaPekerjaan);
                stmt.setInt(3, jumlahTugas);
                stmt.setString(4, namaPekerjaan);
                stmt.setInt(5, jumlahTugas);
                
                int affectedRows = stmt.executeUpdate();
                sukses = affectedRows > 0;
            }
        } catch (SQLException ex) {
            pesan = "Error database: " + ex.getMessage();
        }
        return sukses;
    }

    public boolean baca(String kodePekerjaan) {
        boolean found = false;
        try (Connection conn = koneksi.getConnection()) {
            String sql = "SELECT * FROM tbpekerjaan WHERE kodepekerjaan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, kodePekerjaan);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    this.kodePekerjaan = rs.getString("kodepekerjaan");
                    namaPekerjaan = rs.getString("namapekerjaan");
                    jumlahTugas = rs.getInt("jumlahtugas");
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
    try (Connection conn = koneksi.getConnection()) {
        String sql = "SELECT kodepekerjaan, namapekerjaan, jumlahtugas FROM tbpekerjaan";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            rs.last();
            list = new Object[rs.getRow()][3]; // Changed to 3 columns
            rs.beforeFirst();

            int i = 0;
            while (rs.next()) {
                list[i][0] = rs.getString("kodepekerjaan");
                list[i][1] = rs.getString("namapekerjaan");
                list[i][2] = rs.getInt("jumlahtugas");
                i++;
            }
            sukses = true;
        }
    } catch (SQLException ex) {
        pesan = "Error database: " + ex.getMessage();
    }
    return sukses;
    }

    public boolean hapus(String kodePekerjaan) {
        boolean sukses = false;
        try (Connection conn = koneksi.getConnection()) {
            String sql = "DELETE FROM tbpekerjaan WHERE kodepekerjaan = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, kodePekerjaan);
                int affectedRows = stmt.executeUpdate();
                sukses = affectedRows > 0;
            }
        } catch (SQLException ex) {
            pesan = "Error database: " + ex.getMessage();
        }
        return sukses;
    }

    public ArrayList<Object[]> lihat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

