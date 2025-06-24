package com.unpam.controller;

import com.unpam.model.Gaji;
import com.unpam.model.Karyawan;
import com.unpam.model.Pekerjaan;
import java.io.*;
import java.text.NumberFormat;
import java.util.Locale;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "GajiController", urlPatterns = {"/GajiController"})
public class GajiController extends HttpServlet {
    private final Gaji gajiModel = new Gaji();
    private final Karyawan karyawanModel = new Karyawan();
    private final Pekerjaan pekerjaanModel = new Pekerjaan();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String formContent = generateGajiForm(request);
        request.setAttribute("konten", formContent);
        request.getRequestDispatcher("/MainForm").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        if (action == null) {
            action = "lihat";
        }

        switch (action) {
            case "simpan":
                processSimpan(request, session);
                break;
            case "hapus":
                processHapus(request, session);
                break;
            case "cari":
                processCari(request, session);
                break;
            case "pilih":
                processPilih(request, session);
                break;
            case "lihat_karyawan":
                processLihatKaryawan(session);
                break;
            case "lihat_pekerjaan":
                processLihatPekerjaan(session);
                break;
            case "lihat":
            default:
                processLihat(session);
                break;
        }
        response.sendRedirect("GajiController");
    }

    private String generateGajiForm(HttpServletRequest request) {
        HttpSession session = request.getSession();
        StringBuilder html = new StringBuilder();

        html.append("<div class='card'>");

        // Notification messages
        if (session.getAttribute("message") != null) {
            html.append("<div class='alert alert-success'>")
                .append(session.getAttribute("message"))
                .append("</div>");
            session.removeAttribute("message");
        }

        if (session.getAttribute("error") != null) {
            html.append("<div class='alert alert-error'>")
                .append(session.getAttribute("error"))
                .append("</div>");
            session.removeAttribute("error");
        }

        // Toggle buttons for different views
        html.append("<div class='view-toggle'>")
            .append("<form method='post' style='display:inline;'>")
            .append("<input type='hidden' name='action' value='lihat'>")
            .append("<button type='submit' class='btn btn-primary'>Lihat Gaji</button>")
            .append("</form>")
            .append("<form method='post' style='display:inline; margin-left:10px;'>")
            .append("<input type='hidden' name='action' value='lihat_karyawan'>")
            .append("<button type='submit' class='btn btn-primary'>Lihat Karyawan</button>")
            .append("</form>")
            .append("<form method='post' style='display:inline; margin-left:10px;'>")
            .append("<input type='hidden' name='action' value='lihat_pekerjaan'>")
            .append("<button type='submit' class='btn btn-primary'>Lihat Pekerjaan</button>")
            .append("</form>")
            .append("</div>");

        // Display appropriate content based on view
        String currentView = (String) session.getAttribute("currentView");
        if (currentView == null) {
            currentView = "gaji";
        }

        if (currentView.equals("karyawan")) {
            html.append(generateKaryawanList(session));
        } else if (currentView.equals("pekerjaan")) {
            html.append(generatePekerjaanList(session));
        } else {
            // Default gaji view
            html.append(generateGajiContent(request, session));
        }

        html.append("</div>");
        return html.toString();
    }

    private String generateGajiContent(HttpServletRequest request, HttpSession session) {
        StringBuilder html = new StringBuilder();

        // Search forms
        html.append("<div style='display:flex; gap:20px; margin-bottom:20px;'>")
            .append("<form method='post' style='flex:1;'>")
            .append("<h3>Cari Berdasarkan KTP</h3>")
            .append("<input type='hidden' name='action' value='cari'>")
            .append("<input type='text' name='ktp' placeholder='Masukkan KTP'>")
            .append("<button type='submit'>Cari</button>")
            .append("</form>")
            .append("<form method='post' style='flex:1;'>")
            .append("<h3>Cari Berdasarkan Kode Pekerjaan</h3>")
            .append("<input type='hidden' name='action' value='cari'>")
            .append("<input type='text' name='kode_pekerjaan' placeholder='Masukkan Kode Pekerjaan'>")
            .append("<button type='submit'>Cari</button>")
            .append("</form>")
            .append("</div>");

        // View all button
        html.append("<form method='post' style='margin-bottom:20px;'>")
            .append("<input type='hidden' name='action' value='lihat'>")
            .append("<button type='submit'>Lihat Semua Data Gaji</button>")
            .append("</form>");

        // Data table
        if (session.getAttribute("gajiList") != null) {
            Object[][] list = (Object[][]) session.getAttribute("gajiList");
            html.append("<h2>Daftar Data Gaji</h2>")
                .append("<table class='data-table'>")
                .append("<thead><tr>")
                .append("<th>KTP</th>")
                .append("<th>Nama</th>")
                .append("<th>Ruang</th>")
                .append("<th>Kode Pekerjaan</th>")
                .append("<th>Nama Pekerjaan</th>")
                .append("<th>Jumlah Tugas</th>")
                .append("<th>Gaji Bersih</th>")
                .append("<th>Gaji Kotor</th>")
                .append("<th>Tunjangan</th>")
                .append("<th>Aksi</th>")
                .append("</tr></thead>")
                .append("<tbody>");
            
            for (Object[] row : list) {
                html.append("<tr>")
                    .append("<td>").append(row[0]).append("</td>")  // KTP
                    .append("<td>").append(row[1]).append("</td>")  // Nama
                    .append("<td>").append(row[2]).append("</td>")  // Ruang
                    .append("<td>").append(row[3]).append("</td>")  // Kode Pekerjaan
                    .append("<td>").append(row[4]).append("</td>")  // Nama Pekerjaan
                    .append("<td>").append(row[5]).append("</td>")  // Jumlah Tugas
                    .append("<td>").append(formatCurrency(row[6])).append("</td>")  // Gaji Bersih
                    .append("<td>").append(formatCurrency(row[7])).append("</td>")  // Gaji Kotor
                    .append("<td>").append(formatCurrency(row[8])).append("</td>")  // Tunjangan
                    .append("<td>")
                    .append("<form method='post' style='display:inline;'>")
                    .append("<input type='hidden' name='action' value='pilih'>")
                    .append("<input type='hidden' name='ktp' value='").append(row[0]).append("'>")
                    .append("<button type='submit'>Pilih</button>")
                    .append("</form>")
                    .append("<form method='post' style='display:inline; margin-left:5px;'>")
                    .append("<input type='hidden' name='action' value='hapus'>")
                    .append("<input type='hidden' name='ktp' value='").append(row[0]).append("'>")
                    .append("<button type='submit'>Hapus</button>")
                    .append("</form>")
                    .append("</td>")
                    .append("</tr>");
            }
            html.append("</tbody></table>");
            session.removeAttribute("gajiList");
        }

        // Form for add/edit
        html.append(generateGajiFormFields(session));

        return html.toString();
    }

    private String generateKaryawanList(HttpSession session) {
        StringBuilder html = new StringBuilder();
    
        if (karyawanModel.bacaData()) {
            html.append("<h2>Daftar Karyawan</h2>")
               .append("<table class='data-table'>")
               // ===== PERUBAHAN DI SINI: Kolom 'Kode Pekerjaan' dihapus dari tampilan =====
               .append("<thead><tr>")
               .append("<th>KTP</th>")
               .append("<th>Nama</th>")
               .append("<th>Ruang</th>")
               .append("</tr></thead>")
               .append("<tbody>");
            
            // Kode ini sekarang cocok dengan Karyawan.java yang mengembalikan 3 kolom
            for (Object[] row : karyawanModel.getList()) {
                html.append("<tr>")
                   .append("<td>").append(row[0]).append("</td>") // KTP
                   .append("<td>").append(row[1]).append("</td>") // Nama
                   .append("<td>").append(row[2]).append("</td>") // Ruang
                   .append("</tr>");
            }
            
            html.append("</tbody></table>");
        } else {
            html.append("<p class='error'>").append(karyawanModel.getPesan()).append("</p>");
        }
        
        return html.toString();
    }

    private String generatePekerjaanList(HttpSession session) {
        StringBuilder html = new StringBuilder();
    
        if (pekerjaanModel.bacaData()) {
            html.append("<h2>Daftar Pekerjaan</h2>")
               .append("<table class='data-table'>")
               .append("<thead><tr>")
               .append("<th>Kode</th>")
               .append("<th>Nama Pekerjaan</th>")
               .append("<th>Jumlah Tugas</th>")
               .append("</tr></thead>")
               .append("<tbody>");
            
            for (Object[] row : pekerjaanModel.getList()) {
                html.append("<tr>")
                   .append("<td>").append(row[0]).append("</td>") // Kode
                   .append("<td>").append(row[1]).append("</td>") // Nama
                   .append("<td>").append(row[2]).append("</td>") // Jumlah Tugas
                   .append("</tr>");
            }
            
            html.append("</tbody></table>");
        } else {
            html.append("<p class='error'>").append(pekerjaanModel.getPesan()).append("</p>");
        }
        
        return html.toString();
    }

    private String generateGajiFormFields(HttpSession session) {
        StringBuilder html = new StringBuilder();
        
        html.append("<h2>")
            .append(session.getAttribute("ktp") != null ? "Update" : "Tambah")
            .append(" Data Gaji</h2>")
            .append("<form method='post'>")
            .append("<input type='hidden' name='action' value='simpan'>");

        // Dropdown Karyawan
        html.append("<div class='form-group'>")
            .append("<label>Karyawan</label>")
            .append("<select name='ktp' required>");
        
        if (karyawanModel.bacaData()) {
            for (Object[] k : karyawanModel.getList()) {
                String ktp = (String) k[0];
                String nama = (String) k[1];
                boolean selected = ktp.equals(session.getAttribute("ktp"));
                html.append("<option value='").append(ktp).append("'")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(ktp).append(" - ").append(nama)
                    .append("</option>");
            }
        }
        html.append("</select></div>");

        // Dropdown Pekerjaan
        html.append("<div class='form-group'>")
            .append("<label>Kode Pekerjaan</label>")
            .append("<select name='kode_pekerjaan' required>");
        
        if (pekerjaanModel.bacaData()) {
            for (Object[] p : pekerjaanModel.getList()) {
                String kode = (String) p[0];
                String namaPekerjaan = (String) p[1];
                boolean selected = kode.equals(session.getAttribute("kode_pekerjaan"));
                html.append("<option value='").append(kode).append("'")
                    .append(selected ? " selected" : "")
                    .append(">")
                    .append(kode).append(" - ").append(namaPekerjaan)
                    .append("</option>");
            }
        }
        html.append("</select></div>");

        // Input fields untuk Gaji
        html.append("<div class='form-group'>")
            .append("<label>Gaji Bersih</label>")
            .append("<input type='number' name='gaji_bersih' value='")
            .append(session.getAttribute("gaji_bersih") != null ? session.getAttribute("gaji_bersih") : "")
            .append("' required></div>");

        html.append("<div class='form-group'>")
            .append("<label>Gaji Kotor</label>")
            .append("<input type='number' name='gaji_kotor' value='")
            .append(session.getAttribute("gaji_kotor") != null ? session.getAttribute("gaji_kotor") : "")
            .append("' required></div>");

        html.append("<div class='form-group'>")
            .append("<label>Tunjangan</label>")
            .append("<input type='number' name='tunjangan' value='")
            .append(session.getAttribute("tunjangan") != null ? session.getAttribute("tunjangan") : "")
            .append("' required></div>");

        // Tombol Simpan/Update
        html.append("<button type='submit'>")
            .append(session.getAttribute("ktp") != null ? "Update" : "Simpan")
            .append("</button>");

        if (session.getAttribute("ktp") != null) {
            html.append("<button type='button' onclick='location.href=\"GajiController\"' style='margin-left:1rem;'>Batal</button>");
        }

        html.append("</form>");

        return html.toString();
    }

    private String formatCurrency(Object amount) {
        if (amount == null) return "0";
        // Menggunakan NumberFormat untuk format mata uang yang lebih baik
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(Double.parseDouble(amount.toString()));
    }

    /**
     * Memproses penyimpanan data Gaji.
     * Logika dikembalikan ke versi sederhana karena konsistensi tidak lagi bergantung pada tbkaryawan.
     */
    private void processSimpan(HttpServletRequest request, HttpSession session) {
        try {
            gajiModel.setKtp(request.getParameter("ktp"));
            // ===== PERUBAHAN UTAMA: Mengambil kode pekerjaan langsung dari form =====
            gajiModel.setKodePekerjaan(request.getParameter("kode_pekerjaan"));
            gajiModel.setGajiBersih(Double.parseDouble(request.getParameter("gaji_bersih")));
            gajiModel.setGajiKotor(Double.parseDouble(request.getParameter("gaji_kotor")));
            gajiModel.setTunjangan(Double.parseDouble(request.getParameter("tunjangan")));

            if (gajiModel.simpan()) {
                session.setAttribute("message", "Data gaji berhasil disimpan.");
            } else {
                session.setAttribute("error", "Gagal menyimpan: " + gajiModel.getPesan());
                saveFormSession(request, session);
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: Terjadi kesalahan input. " + e.getMessage());
            saveFormSession(request, session);
        }
    }


    private void saveFormSession(HttpServletRequest request, HttpSession session) {
        session.setAttribute("ktp", request.getParameter("ktp"));
        session.setAttribute("kode_pekerjaan", request.getParameter("kode_pekerjaan"));
        session.setAttribute("gaji_bersih", request.getParameter("gaji_bersih"));
        session.setAttribute("gaji_kotor", request.getParameter("gaji_kotor"));
        session.setAttribute("tunjangan", request.getParameter("tunjangan"));
    }

    private void processHapus(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp");
        if (ktp != null && !ktp.isEmpty()) {
            if (gajiModel.hapus(ktp)) {
                session.setAttribute("message", "Data gaji berhasil dihapus");
            } else {
                session.setAttribute("error", "Gagal menghapus: " + gajiModel.getPesan());
            }
        }
    }

    private void processCari(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp");
        String kodePekerjaan = request.getParameter("kode_pekerjaan");
        
        if (ktp != null && !ktp.isEmpty()) {
            if (gajiModel.bacaByKtp(ktp)) { 
                // Logika ini mungkin perlu penyesuaian di Gaji.java jika satu KTP bisa punya banyak gaji
                session.setAttribute("gajiList", gajiModel.getList());
            } else {
                session.setAttribute("error", gajiModel.getPesan());
            }
        } else if (kodePekerjaan != null && !kodePekerjaan.isEmpty()) {
            if (gajiModel.bacaByKodePekerjaan(kodePekerjaan)) {
                session.setAttribute("gajiList", gajiModel.getList());
            } else {
                session.setAttribute("error", gajiModel.getPesan());
            }
        }
    }

    private void processLihat(HttpSession session) {
        session.setAttribute("currentView", "gaji");
        if (gajiModel.bacaData()) {
            session.setAttribute("gajiList", gajiModel.getList());
        } else {
            session.setAttribute("error", "Gagal memuat data: " + gajiModel.getPesan());
        }
    }

    private void processLihatKaryawan(HttpSession session) {
        session.setAttribute("currentView", "karyawan");
        // Logika untuk menampilkan data sudah ada di generateGajiForm
    }

    private void processLihatPekerjaan(HttpSession session) {
        session.setAttribute("currentView", "pekerjaan");
        // Logika untuk menampilkan data sudah ada di generateGajiForm
    }

    private void processPilih(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp");
        if (ktp != null && !ktp.isEmpty() && gajiModel.bacaByKtp(ktp)) {
            session.setAttribute("ktp", gajiModel.getKtp());
            session.setAttribute("kode_pekerjaan", gajiModel.getKodePekerjaan());
            session.setAttribute("gaji_bersih", String.valueOf(gajiModel.getGajiBersih()));
            session.setAttribute("gaji_kotor", String.valueOf(gajiModel.getGajiKotor()));
            session.setAttribute("tunjangan", String.valueOf(gajiModel.getTunjangan()));
        }
    }
}
