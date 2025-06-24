package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.view.MainForm;
import java.io.IOException;
import java.io.InputStream; // Import InputStream
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller ini bertanggung jawab untuk menangani permintaan terkait laporan gaji.
 * Ia menampilkan form filter dan memproses permintaan untuk mencetak laporan.
 * UI-nya telah disesuaikan agar terlihat modern.
 */
@WebServlet(name = "LaporanGajiController", urlPatterns = {"/LaporanGajiController"})
public class LaporanGajiController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String[][] formatTypeData = {
            {"XLSX (Microsoft Excel)", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {"XLS (Microsoft Excel 97-2003)", "xls", "application/vnd.ms-excel"},
            {"ODT (OpenDocument Text)", "odt", "application/vnd.oasis.opendocument.text"},
            {"RTF (Rich Text Format)", "rtf", "text/rtf"}
        };

        HttpSession session = request.getSession(true);
        String userName = "";

        String tombol = request.getParameter("tombol");
        String ktp = request.getParameter("ktp");
        String ruang = request.getParameter("ruang");
        String formatType = request.getParameter("formatType");

        if (tombol == null) tombol = "";
        if (ktp == null) ktp = "";
        String opsi = "Semua";
        if (ruang == null || ruang.equals("")) ruang = "0";
        if (formatType == null) formatType = formatTypeData[0][0];

        String keterangan = "<br>";
        int noType = 0;

        for (int i = 0; i < formatTypeData.length; i++) {
            if (formatTypeData[i][0].equals(formatType)) {
                noType = i;
                break;
            }
        }

        try {
            Object userObj = session.getAttribute("userName");
            if (userObj != null) {
                userName = userObj.toString();
            }
        } catch (Exception ex) {
            System.err.println("Error retrieving userName from session: " + ex.getMessage());
        }

        if (!(userName == null || userName.equals(""))) {
            if (tombol.equals("Cetak")) {
                Gaji gaji = new Gaji();

                int ruangDipilih = 0;
                try {
                    ruangDipilih = Integer.parseInt(ruang);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid 'ruang' parameter: " + ex.getMessage());
                }

                if (!ktp.isEmpty()) {
                    opsi = "KTP";
                } else if (ruangDipilih != 0) {
                    opsi = "ruang";
                } else {
                    opsi = "Semua";
                }

                // --- START MODIFIKASI UNTUK MEMUAT JASPER DARI CLASSPATH ---
                String jasperPathInClasspath = "reports/LaporanGajiKaryawan.jasper"; // Pastikan ini adalah path di dalam folder 'Source Packages' atau 'resources'
                InputStream jasperStream = getClass().getClassLoader().getResourceAsStream(jasperPathInClasspath);

                if (jasperStream == null) {
                    // Ini akan muncul jika file .jasper tidak ditemukan di classpath
                    keterangan = "Error: File laporan '" + jasperPathInClasspath + "' tidak ditemukan di dalam aplikasi. Pastikan sudah di-deploy dengan benar.";
                    System.err.println(keterangan);
                } else {
                    // Memanggil metode cetakLaporan dari model Gaji dengan InputStream
                    if (gaji.cetakLaporan(opsi, ktp, ruangDipilih, formatTypeData[noType][1], jasperStream)) {
                        byte[] pdfasBytes = gaji.getPdfasBytes();
                        try (OutputStream outStream = response.getOutputStream()) {
                            response.setHeader("Content-Disposition", "inline; filename=LaporanGajiReport." + formatTypeData[noType][1]);
                            response.setContentType(formatTypeData[noType][2]);
                            response.setContentLength(pdfasBytes.length);
                            outStream.write(pdfasBytes, 0, pdfasBytes.length);
                        }
                        return;
                    } else {
                        String gajiPesan = gaji.getPesan();
                        keterangan = (gajiPesan != null && !gajiPesan.isEmpty()) ? gajiPesan : "Terjadi kesalahan yang tidak diketahui saat membuat laporan.";
                    }
                }
                // --- AKHIR MODIFIKASI UNTUK MEMUAT JASPER DARI CLASSPATH ---
            }

            StringBuilder konten = new StringBuilder();

            konten.append("<div class='card w-full max-w-md mx-auto p-6 bg-white rounded-lg shadow-xl'>");
            konten.append("<h2 class='text-2xl font-bold text-gray-900 mb-6 text-center'>Cetak Laporan Gaji</h2>");
            konten.append("<form action='LaporanGajiController' method='post'>");

            konten.append("<div class='form-group mb-4'>");
            konten.append("<label for='ktp' class='block text-sm font-medium text-gray-700 mb-1'>Filter Berdasarkan KTP (Kosongkan untuk semua)</label>");
            konten.append("<input type='text' id='ktp' value='").append(ktp).append("' name='ktp' maxlength='15' size='15' class='w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm'>");
            konten.append("</div>");

            konten.append("<div class='form-group mb-4'>");
            konten.append("<label for='ruang' class='block text-sm font-medium text-gray-700 mb-1'>Filter Berdasarkan Ruang</label>");
            konten.append("<select id='ruang' name='ruang' class='w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm'>");
            konten.append("<option value='0'").append("0".equals(ruang) ? " selected" : "").append(">Semua</option>");
            for (int j = 1; j <= 10; j++) {
                if (String.valueOf(j).equals(ruang)) {
                    konten.append("<option selected value='").append(j).append("'>").append(j).append("</option>");
                } else {
                    konten.append("<option value='").append(j).append("'>").append(j).append("</option>");
                }
            }
            konten.append("</select>");
            konten.append("</div>");

            konten.append("<div class='form-group mb-4'>");
            konten.append("<label for='formatType' class='block text-sm font-medium text-gray-700 mb-1'>Format Laporan</label>");
            konten.append("<select id='formatType' name='formatType' class='w-full p-2 border border-gray-300 rounded-md shadow-sm focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm'>");
            for (String[] formatLaporan : formatTypeData) {
                if (formatLaporan[0].equals(formatType)) {
                    konten.append("<option selected value='").append(formatLaporan[0]).append("'>").append(formatLaporan[0]).append("</option>");
                } else {
                    konten.append("<option value='").append(formatLaporan[0]).append("'>").append(formatLaporan[0]).append("</option>");
                }
            }
            konten.append("</select>");
            konten.append("</div>");

            if (!keterangan.trim().equals("<br>") && !keterangan.trim().isEmpty()) {
                boolean isError = keterangan.toLowerCase().contains("error") || keterangan.toLowerCase().contains("gagal") || keterangan.toLowerCase().contains("tidak ditemukan") || keterangan.contains("Terjadi kesalahan yang tidak diketahui");
                konten.append("<div class='alert ").append(isError ? "bg-red-100 text-red-700" : "bg-green-100 text-green-700").append(" p-3 rounded-md text-sm mt-4'>");
                konten.append(keterangan.replaceAll("\n", "<br>").replaceAll(":", ","));
                konten.append("</div>");
            }

            konten.append("<button type='submit' name='tombol' value='Cetak' class='w-full mt-6 inline-flex justify-center py-2 px-4 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500;'>Cetak Laporan</button>");
            konten.append("</form>");
            konten.append("</div>");

            new MainForm().tampilkan(request, response, konten.toString());
        } else {
            response.sendRedirect(".");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
