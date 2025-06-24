package com.unpam.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Warudo
 */
public class Koneksi {
    private Connection connection;
    private String pesanKesalahan;
    
    public Connection getConnection() {
        try {
            // Menggunakan driver MySQL versi 5.x. Ini umum untuk NetBeans 8.2
            Class.forName("com.mysql.jdbc.Driver");
            
            // String koneksi, pastikan nama database, user, dan password sudah benar.
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/dbaplikasigajikaryawan", 
                "root", 
                ""); // Password dikosongkan
                
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            // =================================================================
            // PERUBAHAN PALING PENTING ADA DI SINI
            // Cetak detail error lengkap ke konsol/log server.
            System.err.println("!!! KESALAHAN KONEKSI DATABASE !!!");
            e.printStackTrace(); 
            // =================================================================
            
            pesanKesalahan = "Koneksi database gagal: " + e.getMessage();
            return null;
        }
    }
    
    public String getPesanKesalahan() {
        return pesanKesalahan;
    }
}