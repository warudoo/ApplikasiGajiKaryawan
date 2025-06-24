package com.unpam.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 * Kelas ini menangani semua logika bisnis dan interaksi database
 * yang terkait dengan Gaji Karyawan, termasuk fungsi CRUD dan pembuatan laporan.
 */
public class Gaji {
    // Properti untuk menampung satu data Gaji
    private String ktp;
    private String kodePekerjaan;
    private double gajiBersih;
    private double gajiKotor;
    private double tunjangan;
    
    private String pesanKesalahan;
    private byte[] pdfasBytes; // Properti untuk menyimpan byte array laporan yang dihasilkan

    // Properti untuk menampung daftar data Gaji untuk ditampilkan di tabel
    private ArrayList<Object[]> list = new ArrayList<>();
    
    private final Koneksi koneksi = new Koneksi();

    // Getter dan Setter
    public String getKtp() { return ktp; }
    public void setKtp(String ktp) { this.ktp = ktp; }
    public String getKodePekerjaan() { return kodePekerjaan; }
    public void setKodePekerjaan(String kodePekerjaan) { this.kodePekerjaan = kodePekerjaan; }
    public double getGajiBersih() { return gajiBersih; }
    public void setGajiBersih(double gajiBersih) { this.gajiBersih = gajiBersih; }
    public double getGajiKotor() { return gajiKotor; }
    public void setGajiKotor(double gajiKotor) { this.gajiKotor = gajiKotor; }
    public double getTunjangan() { return tunjangan; }
    public void setTunjangan(double tunjangan) { this.tunjangan = tunjangan; }
    public String getPesan() { return pesanKesalahan; }
    public Object[][] getList() { return list.toArray(new Object[0][]); }
    public byte[] getPdfasBytes() { return pdfasBytes; } // Getter untuk byte array laporan

    // =====================================================================
    // FUNGSI CRUD (Simpan, Baca, Hapus)
    // =====================================================================

    public boolean simpan() {
        try (Connection conn = koneksi.getConnection()) {
            if (conn == null) {
                pesanKesalahan = "Koneksi database gagal.";
                return false;
            }
            
            String checkSql = "SELECT ktp FROM tbgaji WHERE ktp = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, this.ktp);
                ResultSet rs = checkStmt.executeQuery();

                String sql;
                if (rs.next()) {
                    // Data sudah ada, lakukan UPDATE
                    sql = "UPDATE tbgaji SET kodepekerjaan = ?, gajibersih = ?, gajikotor = ?, tunjangan = ? WHERE ktp = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, this.kodePekerjaan);
                        stmt.setDouble(2, this.gajiBersih);
                        stmt.setDouble(3, this.gajiKotor);
                        stmt.setDouble(4, this.tunjangan);
                        stmt.setString(5, this.ktp);
                        return stmt.executeUpdate() > 0;
                    }
                } else {
                    // Data belum ada, lakukan INSERT
                    sql = "INSERT INTO tbgaji (ktp, kodepekerjaan, gajibersih, gajikotor, tunjangan) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, this.ktp);
                        stmt.setString(2, this.kodePekerjaan);
                        stmt.setDouble(3, this.gajiBersih);
                        stmt.setDouble(4, this.gajiKotor);
                        stmt.setDouble(5, this.tunjangan);
                        return stmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException ex) {
            pesanKesalahan = "Error database saat menyimpan gaji: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }
    
    private final String selectQuery = "SELECT g.ktp, k.nama, k.ruang, g.kodepekerjaan, " +
                                       "p.namapekerjaan, p.jumlahtugas, g.gajibersih, g.gajikotor, g.tunjangan " +
                                       "FROM tbgaji g " +
                                       "JOIN tbkaryawan k ON g.ktp = k.ktp " +
                                       "JOIN tbpekerjaan p ON g.kodepekerjaan = p.kodepekerjaan ";

    public boolean bacaData() {
        list.clear();
        try (Connection conn = koneksi.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getString("ktp");
                row[1] = rs.getString("nama");
                row[2] = rs.getInt("ruang");
                row[3] = rs.getString("kodepekerjaan");
                row[4] = rs.getString("namapekerjaan");
                row[5] = rs.getInt("jumlahtugas");
                row[6] = rs.getDouble("gajibersih");
                row[7] = rs.getDouble("gajikotor");
                row[8] = rs.getDouble("tunjangan");
                list.add(row);
            }
            return true;
        } catch (SQLException ex) {
            pesanKesalahan = "Error database saat membaca data gaji: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean bacaByKtp(String ktp) {
        list.clear();
        String sql = selectQuery + " WHERE g.ktp = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, ktp);
            ResultSet rs = stmt.executeQuery();
            
            boolean found = false;
            if (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getString("ktp"); row[1] = rs.getString("nama"); row[2] = rs.getInt("ruang");
                row[3] = rs.getString("kodepekerjaan"); row[4] = rs.getString("namapekerjaan"); row[5] = rs.getInt("jumlahtugas");
                row[6] = rs.getDouble("gajibersih"); row[7] = rs.getDouble("gajikotor"); row[8] = rs.getDouble("tunjangan");
                list.add(row);

                this.ktp = rs.getString("ktp");
                this.kodePekerjaan = rs.getString("kodepekerjaan");
                this.gajiBersih = rs.getDouble("gajibersih");
                this.gajiKotor = rs.getDouble("gajikotor");
                this.tunjangan = rs.getDouble("tunjangan");
                found = true;
            }
            if (!found) {
                pesanKesalahan = "Data gaji untuk KTP " + ktp + " tidak ditemukan.";
            }
            return found;
        } catch (SQLException ex) {
            pesanKesalahan = "Error database saat mencari gaji by KTP: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }

    public boolean bacaByKodePekerjaan(String kodePekerjaan) {
        list.clear();
        String sql = selectQuery + " WHERE g.kodepekerjaan = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kodePekerjaan);
            ResultSet rs = stmt.executeQuery();
            
            boolean found = false;
            while (rs.next()) {
                Object[] row = new Object[9];
                row[0] = rs.getString("ktp"); row[1] = rs.getString("nama"); row[2] = rs.getInt("ruang");
                row[3] = rs.getString("kodepekerjaan"); row[4] = rs.getString("namapekerjaan"); row[5] = rs.getInt("jumlahtugas");
                row[6] = rs.getDouble("gajibersih"); row[7] = rs.getDouble("gajikotor"); row[8] = rs.getDouble("tunjangan");
                list.add(row);
                found = true;
            }
            if (!found) {
                pesanKesalahan = "Tidak ada data gaji dengan kode pekerjaan " + kodePekerjaan;
            }
            return found;
        } catch (SQLException ex) {
            pesanKesalahan = "Error database saat mencari gaji by pekerjaan: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }
    
    public boolean hapus(String ktp) {
        String sql = "DELETE FROM tbgaji WHERE ktp = ?";
        try (Connection conn = koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ktp);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            pesanKesalahan = "Error database saat menghapus gaji: " + ex.getMessage();
            ex.printStackTrace();
            return false;
        }
    }

    // =====================================================================
    // FUNGSI UTAMA: Pembuatan Laporan
    // =====================================================================

    /**
     * Menghasilkan laporan dari JasperReports dalam bentuk byte array.
     * Metode ini disesuaikan agar sinkron dengan pemanggilan di LaporanGajiController
     * dan menggunakan koneksi database langsung untuk mengisi laporan.
     *
     * @param opsi Opsi filter yang dipilih (misal: "KTP", "ruang", "Semua").
     * @param ktpParam Nomor KTP untuk filter (digunakan jika opsi adalah "KTP").
     * @param ruangParam Nomor ruang untuk filter (digunakan jika opsi adalah "ruang").
     * @param format Format output laporan (misal: "pdf", "xlsx").
     * @param jasperStream InputStream dari file JasperReport (.jasper).
     * @return true jika laporan berhasil dibuat dan disimpan ke pdfasBytes, false jika gagal.
     */
    public boolean cetakLaporan(String opsi, String ktpParam, int ruangParam, String format, InputStream jasperStream) {
        Connection conn = null;

        this.pesanKesalahan = "";
        this.pdfasBytes = null;

        System.out.println("--- MEMULAI PROSES LAPORAN (Gaji.java - Tanpa GajiReportData) ---");
        System.out.println("Opsi Filter: [" + opsi + "], KTP: [" + ktpParam + "], Ruang: [" + ruangParam + "], Format: [" + format + "]");

        try {
            conn = koneksi.getConnection();
            if (conn == null) {
                this.pesanKesalahan = "Koneksi database gagal. Pastikan database berjalan.";
                System.out.println("DIAGNOSTIK: Koneksi null, proses laporan dihentikan.");
                return false;
            }

            Map<String, Object> parameters = new HashMap<>();

            if (ktpParam != null && !ktpParam.trim().isEmpty()) {
                parameters.put("ktp", ktpParam);
                System.out.println("DIAGNOSTIK: Parameter Jasper 'ktp' diset: " + ktpParam);
            } else {
                parameters.put("ktp", null); 
                System.out.println("DIAGNOSTIK: Parameter Jasper 'ktp' diset: null (untuk filter 'Semua' KTP)");
            }

            if (ruangParam != 0) { 
                parameters.put("ruang", ruangParam);
                System.out.println("DIAGNOSTIK: Parameter Jasper 'ruang' diset: " + ruangParam);
            } else {
                parameters.put("ruang", null);
                System.out.println("DIAGNOSTIK: Parameter Jasper 'ruang' diset: null (untuk filter 'Semua' Ruang)");
            }
            
            // --- DIAGNOSTIK BARU UNTUK INPUT STREAM ---
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = jasperStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            byte[] jasperBytes = buffer.toByteArray();
            System.out.println("DIAGNOSTIK: Ukuran InputStream JasperReports: " + jasperBytes.length + " bytes.");
            
            // Konversi ByteArrayOutputStream kembali ke InputStream untuk JasperFillManager
            InputStream bufferedJasperStream = new java.io.ByteArrayInputStream(jasperBytes);
            // --- AKHIR DIAGNOSTIK BARU ---


            System.out.println("DIAGNOSTIK: JasperReports akan mengisi laporan langsung dari koneksi database.");

            // Mengisi laporan menggunakan InputStream JasperReport, parameter, dan koneksi database langsung
            JasperPrint jasperPrint = JasperFillManager.fillReport(bufferedJasperStream, parameters, conn); // Gunakan bufferedJasperStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            // Ekspor laporan berdasarkan format yang diminta
            if (format.equalsIgnoreCase("pdf")) {
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.exportReport();
                System.out.println("DIAGNOSTIK: Laporan PDF berhasil diekspor.");
            } else if (format.equalsIgnoreCase("xlsx")) {
                JRXlsxExporter exporter = new JRXlsxExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
                exporter.exportReport();
                System.out.println("DIAGNOSTIK: Laporan XLSX berhasil diekspor.");
            } else {
                this.pesanKesalahan = "Format laporan tidak didukung: " + format;
                System.out.println("DIAGNOSTIK: Format laporan tidak didukung.");
                return false;
            }

            this.pdfasBytes = baos.toByteArray();
            System.out.println("DIAGNOSTIK: Byte array laporan berhasil disimpan.");
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            this.pesanKesalahan = "Gagal membuat atau mengonversi laporan: " + e.getMessage();
            return false;
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                    System.out.println("DIAGNOSTIK: Koneksi database ditutup.");
                }
            } catch (SQLException e) {
                System.err.println("Error saat menutup koneksi database: " + e.getMessage());
            }
            // Penting: jasperStream tidak ditutup di sini karena ia dibuka di LaporanGajiController
        }
    }
}
