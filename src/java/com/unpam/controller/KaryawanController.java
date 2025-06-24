package com.unpam.controller;

import com.unpam.model.Enkripsi;
import com.unpam.model.Karyawan;
import com.unpam.model.Pekerjaan;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "KaryawanController", urlPatterns = {"/KaryawanController"})
public class KaryawanController extends HttpServlet {
    private final Karyawan karyawanModel = new Karyawan();
    private final Enkripsi enkripsi = new Enkripsi();
    // Model Pekerjaan tetap dibutuhkan untuk dropdown di GajiController, namun tidak lagi relevan di form Karyawan
    private final Pekerjaan pekerjaanModel = new Pekerjaan(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String formContent = generateKaryawanForm(request);
        request.setAttribute("konten", formContent);
        request.getRequestDispatcher("/MainForm").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
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
            case "lihat":
            default:
                processLihat(session);
                break;
        }
        response.sendRedirect("KaryawanController");
    }

    private String generateKaryawanForm(HttpServletRequest request) {
        HttpSession session = request.getSession();
        StringBuilder html = new StringBuilder();

        html.append("<div class='card'>");

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

        // Tombol lihat semua data
        html.append("<form method='post' style='margin-bottom:20px;'>")
            .append("<input type='hidden' name='action' value='lihat'>")
            .append("<button type='submit'>Lihat Semua Karyawan</button>")
            .append("</form>");

        // Form pencarian berdasarkan KTP
        html.append("<h2>Cari Karyawan</h2>")
            .append("<form method='post' style='margin-bottom:20px;'>")
            .append("<input type='hidden' name='action' value='cari'>")
            .append("<label>KTP: </label>")
            .append("<input type='text' name='ktp' required>")
            .append("<button type='submit'>Cari</button>")
            .append("</form>");

        // Tabel daftar karyawan
        if (session.getAttribute("karyawanList") != null) {
            Object[][] list = (Object[][]) session.getAttribute("karyawanList");
            html.append("<h2>Daftar Karyawan</h2>")
                .append("<table class='data-table'>")
                // PERUBAHAN: Header disesuaikan dengan data yang ditampilkan
                .append("<thead><tr><th>KTP</th><th>Nama</th><th>Ruang</th><th>Aksi</th></tr></thead>")
                .append("<tbody>");
            
            for (Object[] row : list) {
                html.append("<tr>")
                    .append("<td>").append(row[0]).append("</td>") // KTP
                    .append("<td>").append(row[1]).append("</td>") // Nama
                    .append("<td>").append(row[2]).append("</td>") // Ruang
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
            session.removeAttribute("karyawanList");
        }

        // Form simpan / update karyawan
        html.append("<h2>")
            .append(session.getAttribute("ktp") != null ? "Update" : "Tambah")
            .append(" Karyawan</h2>")
            .append("<form method='post'>")
            .append("<input type='hidden' name='action' value='simpan'>")
            .append("<div class='form-group'>")
            .append("<label>KTP</label>")
            .append("<input type='text' name='ktp' value='").append(session.getAttribute("ktp") != null ? session.getAttribute("ktp") : "").append("' required>")
            .append("</div>")
            .append("<div class='form-group'>")
            .append("<label>Nama</label>")
            .append("<input type='text' name='nama' value='").append(session.getAttribute("nama") != null ? session.getAttribute("nama") : "").append("' required>")
            .append("</div>")
            .append("<div class='form-group'>")
            .append("<label>Ruang</label>")
            .append("<select name='ruang'>");

        for (int i = 1; i <= 5; i++) {
            html.append("<option value='").append(i).append("'")
                .append(i == (session.getAttribute("ruang") != null ?
                        Integer.parseInt(session.getAttribute("ruang").toString()) : 1) ? " selected" : "")
                .append(">").append(i).append("</option>");
        }

        html.append("</select>")
            .append("</div>");

        // ===== PERUBAHAN: Dropdown pekerjaan dihapus karena tidak lagi relevan untuk Karyawan =====
        
        html.append("<div class='form-group'>")
            .append("<label>Password</label>")
            .append("<input type='password' name='password' required>")
            .append("</div>")
            .append("<button type='submit'>")
            .append(session.getAttribute("ktp") != null ? "Update" : "Simpan")
            .append("</button>");

        if (session.getAttribute("ktp") != null) {
            html.append("<button type='button' onclick='location.href=\"KaryawanController\"' style='margin-left:1rem;'>Batal</button>");
        }

        html.append("</form></div>");

        session.removeAttribute("ktp");
        session.removeAttribute("nama");
        session.removeAttribute("ruang");
        // PERUBAHAN: Menghapus atribut session yang sudah tidak ada
        // session.removeAttribute("kode_pekerjaan");

        return html.toString();
    }

    private void processSimpan(HttpServletRequest request, HttpSession session) {
        try {
            karyawanModel.setKtp(request.getParameter("ktp").trim());
            karyawanModel.setNama(request.getParameter("nama"));
            karyawanModel.setRuang(Integer.parseInt(request.getParameter("ruang")));
            
            // ===== PERUBAHAN: Baris untuk setKodePekerjaan dihapus =====
            // karyawanModel.setKodePekerjaan(request.getParameter("kode_pekerjaan"));

            String hashedPassword = enkripsi.hashMD5(request.getParameter("password"));
            karyawanModel.setPassword(hashedPassword);

            if (karyawanModel.simpan()) {
                session.setAttribute("message", "Data berhasil disimpan");
            } else {
                session.setAttribute("error", "Gagal menyimpan: " + karyawanModel.getPesan());
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
        }
    }

    private void processHapus(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp");
        if (ktp != null && !ktp.isEmpty()) {
            if (karyawanModel.hapus(ktp)) {
                session.setAttribute("message", "Data berhasil dihapus");
            } else {
                session.setAttribute("error", "Gagal menghapus: " + karyawanModel.getPesan());
            }
        }
    }

    private void processCari(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp").trim();
        if (ktp != null && !ktp.isEmpty()) {
            // Menggunakan metode bacaByKtp yang baru dari Canvas
            if (karyawanModel.bacaByKtp(ktp)) {
                session.setAttribute("ktp", karyawanModel.getKtp());
                session.setAttribute("nama", karyawanModel.getNama());
                session.setAttribute("ruang", karyawanModel.getRuang());
                // ===== PERUBAHAN: Baris untuk kode_pekerjaan dihapus =====
                // session.setAttribute("kode_pekerjaan", karyawanModel.getKodePekerjaan());
            } else {
                session.setAttribute("error", karyawanModel.getPesan());
            }
        }
    }

    private void processLihat(HttpSession session) {
        if (karyawanModel.bacaData()) {
            session.setAttribute("karyawanList", karyawanModel.getList());
        } else {
            session.setAttribute("error", "Gagal memuat data: " + karyawanModel.getPesan());
        }
    }

    private void processPilih(HttpServletRequest request, HttpSession session) {
        String ktp = request.getParameter("ktp");
        // Menggunakan metode bacaByKtp yang baru dari Canvas
        if (ktp != null && !ktp.isEmpty() && karyawanModel.bacaByKtp(ktp)) {
            session.setAttribute("ktp", karyawanModel.getKtp());
            session.setAttribute("nama", karyawanModel.getNama());
            session.setAttribute("ruang", karyawanModel.getRuang());
            // ===== PERUBAHAN: Baris untuk kode_pekerjaan dihapus =====
            // session.setAttribute("kode_pekerjaan", karyawanModel.getKodePekerjaan());
        }
    }
}
