package com.unpam.controller;

import com.unpam.model.Pekerjaan;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "PekerjaanController", urlPatterns = {"/PekerjaanController"})
public class PekerjaanController extends HttpServlet {
    private final Pekerjaan pekerjaanModel = new Pekerjaan();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String formContent = generatePekerjaanForm(request);
        request.setAttribute("konten", formContent);
        request.getRequestDispatcher("/MainForm").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String action = request.getParameter("action");

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
                processLihat(session);
                break;
        }
        response.sendRedirect("PekerjaanController");
    }

   private String generatePekerjaanForm(HttpServletRequest request) {
    HttpSession session = request.getSession();
    StringBuilder html = new StringBuilder();

    html.append("<div class='card'>");

    // Messages
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

    // Lihat semua data
    html.append("<form method='post' style='margin-bottom:20px;'>")
        .append("<input type='hidden' name='action' value='lihat'>")
        .append("<button type='submit'>Lihat Semua Pekerjaan</button>")
        .append("</form>");

    // Form cari pekerjaan
    html.append("<h2>Cari Pekerjaan</h2>")
        .append("<form method='post' style='margin-bottom:20px;'>")
        .append("<input type='hidden' name='action' value='cari'>")
        .append("<label>Kode </label>")
        .append("<input type='text' name='kode' required>")
        .append("<button type='submit'>Cari</button>")
        .append("</form>");

    // Tabel data
    if (session.getAttribute("pekerjaanList") != null) {
        Object[][] list = (Object[][]) session.getAttribute("pekerjaanList");
        html.append("<h2>Daftar Pekerjaan</h2>")
            .append("<table class='data-table'>")
            .append("<thead><tr><th>Kode</th><th>Nama</th><th>Aksi</th></tr></thead>")
            .append("<tbody>");
        for (Object[] row : list) {
            html.append("<tr>")
                .append("<td>").append(row[0]).append("</td>")
                .append("<td>").append(row[1]).append("</td>")
                .append("<td>")

                // Tombol Pilih
                .append("<form method='post' style='display:inline; margin-right:5px;'>")
                .append("<input type='hidden' name='action' value='pilih'>")
                .append("<input type='hidden' name='kode' value='").append(row[0]).append("'>")
                .append("<button type='submit'>Pilih</button>")
                .append("</form>")

                // Tombol Hapus
                .append("<form method='post' style='display:inline;'>")
                .append("<input type='hidden' name='action' value='hapus'>")
                .append("<input type='hidden' name='hapusKode' value='").append(row[0]).append("'>")
                .append("<button type='submit' onclick='return confirm(\"Yakin ingin menghapus?\")'>Hapus</button>")
                .append("</form>")

                .append("</td>")
                .append("</tr>");
        }
        html.append("</tbody></table>");
        session.removeAttribute("pekerjaanList");
    }

    // Form input
    html.append("<h2>").append(session.getAttribute("kode") != null ? "Update" : "Tambah").append(" Pekerjaan</h2>")
        .append("<form method='post'>")
        .append("<input type='hidden' name='action' value='simpan'>")
        .append("<div class='form-group'>")
        .append("<label>Kode</label>")
        .append("<input type='text' name='kode' value='").append(session.getAttribute("kode") != null ? session.getAttribute("kode") : "").append("' required>")
        .append("</div>")
        .append("<div class='form-group'>")
        .append("<label>Nama</label>")
        .append("<input type='text' name='nama' value='").append(session.getAttribute("nama") != null ? session.getAttribute("nama") : "").append("' required>")
        .append("</div>")
        .append("<div class='form-group'>")
        .append("<label>Jumlah Tugas</label>")
        .append("<select name='tugas'>");

    for (int i = 1; i <= 10; i++) {
        html.append("<option value='").append(i).append("'")
            .append(i == (session.getAttribute("tugas") != null ? Integer.parseInt(session.getAttribute("tugas").toString()) : 1) ? " selected" : "")
            .append(">").append(i).append("</option>");
    }

    html.append("</select>")
        .append("</div>")
        .append("<button type='submit'>").append(session.getAttribute("kode") != null ? "Update" : "Simpan").append("</button>");

    if (session.getAttribute("kode") != null) {
        html.append("<button type='button' onclick='location.href=\"PekerjaanController\"' style='margin-left:1rem;'>Batal</button>");
    }

    html.append("</form></div>");

    session.removeAttribute("kode");
    session.removeAttribute("nama");
    session.removeAttribute("tugas");
    return html.toString();
}


    private void processSimpan(HttpServletRequest request, HttpSession session) {
        try {
            pekerjaanModel.setKodePekerjaan(request.getParameter("kode"));
            pekerjaanModel.setNamaPekerjaan(request.getParameter("nama"));
            pekerjaanModel.setJumlahTugas(Integer.parseInt(request.getParameter("tugas")));

            if (pekerjaanModel.simpan()) {
                session.setAttribute("message", "Data berhasil disimpan");
            } else {
                session.setAttribute("error", "Gagal menyimpan: " + pekerjaanModel.getPesan());
            }
        } catch (Exception e) {
            session.setAttribute("error", "Error: " + e.getMessage());
        }
    }

    private void processHapus(HttpServletRequest request, HttpSession session) {
        String kode = request.getParameter("hapusKode");
        if (kode != null && !kode.isEmpty()) {
            if (pekerjaanModel.hapus(kode)) {
                session.setAttribute("message", "Data dihapus");
            } else {
                session.setAttribute("error", "Gagal menghapus: " + pekerjaanModel.getPesan());
            }
        }
    }

    private void processCari(HttpServletRequest request, HttpSession session) {
        String kode = request.getParameter("kode");
        if (kode != null && !kode.isEmpty()) {
            if (pekerjaanModel.baca(kode)) {
                session.setAttribute("kode", pekerjaanModel.getKodePekerjaan());
                session.setAttribute("nama", pekerjaanModel.getNamaPekerjaan());
                session.setAttribute("tugas", pekerjaanModel.getJumlahTugas());
            } else {
                session.setAttribute("error", pekerjaanModel.getPesan());
            }
        }
    }

    private void processLihat(HttpSession session) {
        if (pekerjaanModel.bacaData()) {
            session.setAttribute("pekerjaanList", pekerjaanModel.getList());
        } else {
            session.setAttribute("error", "Gagal memuat data: " + pekerjaanModel.getPesan());
        }
    }

    private void processPilih(HttpServletRequest request, HttpSession session) {
        String kode = request.getParameter("kode");
        if (kode != null && !kode.isEmpty() && pekerjaanModel.baca(kode)) {
            session.setAttribute("kode", pekerjaanModel.getKodePekerjaan());
            session.setAttribute("nama", pekerjaanModel.getNamaPekerjaan());
            session.setAttribute("tugas", pekerjaanModel.getJumlahTugas());
        }
    }
}



