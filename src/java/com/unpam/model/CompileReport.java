package com.unpam.model;
import net.sf.jasperreports.engine.JasperCompileManager;

public class CompileReport {

    public static void main(String[] args) {
        try {
            // GANTI DENGAN PATH LENGKAP KE FILE .jrxml ANDA
            String sourceFileName = "C:\\Users\\Warudo\\Documents\\kuliah\\Pemrograman 2\\TUGAS 18 PERTEMUAN\\AplikasiGajiKaryawanPTSalwarud\\src\\java\\reports\\LaporanGajiKaryawan.jrxml";

            System.out.println("Mencoba mengompilasi file: " + sourceFileName);

            // Mengompilasi file .jrxml menjadi .jasper
            String jasperFile = JasperCompileManager.compileReportToFile(sourceFileName);

            System.out.println("Kompilasi BERHASIL!");
            System.out.println("File .jasper dibuat di: " + jasperFile);

        } catch (Exception e) {
            System.err.println("Kompilasi GAGAL!");
            e.printStackTrace();
        }
    }
}