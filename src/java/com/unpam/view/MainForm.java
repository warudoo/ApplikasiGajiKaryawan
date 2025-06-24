package com.unpam.view;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "MainForm", urlPatterns = {"/MainForm"})
public class MainForm extends HttpServlet {

    public void tampilkan(HttpServletRequest request, HttpServletResponse response, String konten)
            throws ServletException, IOException {

        // Pastikan halaman tidak bisa di-cache oleh browser
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession(false);
        String userName = null;
        if (session != null) {
            userName = (String) session.getAttribute("userName");
        }

        // Buat sidebar menu dengan dynamic login/logout dan welcome message
        String sidebarMenu =
            "<div class='sidebar'>" +
            "  <div class='sidebar-header'>" +
            "    <h2 class='logo'>PT. MUHAMAD SALWARUD</h2>";

        if (userName != null && !userName.isEmpty()) {
            sidebarMenu += "    <p style='padding:0; color: rgba(255,255,255,0.8);'>Welcome, " + userName + "</p>";
        }

        sidebarMenu +=
            "  </div>" +
            "  <ul class='sidebar-menu'>" +
            "    <li class='menu-title'>Master Data</li>" +
            "    <li><a href='KaryawanController'><span>Karyawan</span></a></li>" +
            "    <li><a href='PekerjaanController'><span>Pekerjaan</span></a></li>" +
            "    <li class='menu-title'>Transaksi</li>" +
            "    <li><a href='GajiController'><span>Gaji Karyawan</span></a></li>" +
            "    <li class='menu-title'>Laporan</li>" +
            "    <li><a href='LaporanGajiController'><span>Laporan Gaji Karyawan</span></a></li>";

        if (userName != null && !userName.isEmpty()) {
            sidebarMenu +=
                "    <li class='login-item'><a href='LoginController?action=logout' class='logout-btn'><span>Logout</span></a></li>";
        } else {
            sidebarMenu +=
                "    <li class='login-item'><a href='LoginController' class='login-btn'><span>Login</span></a></li>";
        }

        sidebarMenu +=
            "  </ul>" +
            "</div>";

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html lang='en'>");
            out.println("<head>");
            out.println("  <meta charset='UTF-8'>");
            out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("  <title>PT. WARUD MENCARI CINTA SEJATI</title>");
            out.println("  <style>");
            out.println("    :root { --primary: #006400; --secondary: #228B22; --light: #ecf0f1; --sidebar-width: 250px; }");
            out.println("    * { box-sizing: border-box; margin: 0; font-family: 'Segoe UI', sans-serif; }");
            out.println("    body { display: flex; background: #f5f5f5; }");
            out.println("    .sidebar { width: var(--sidebar-width); background: var(--primary); color: white; height: 100vh; position: fixed; }");
            out.println("    .sidebar-header { padding: 1.5rem 1rem; border-bottom: 1px solid rgba(255,255,255,0.1); }");
            out.println("    .logo { color: white; font-size: 1.3rem; font-weight: bold; }");
            out.println("    .sidebar-menu { list-style: none; padding: 1rem 0; }");
            out.println("    .sidebar-menu li { margin-bottom: 0.2rem; }");
            out.println("    .sidebar-menu a { color: white; text-decoration: none; display: block; padding: 0.8rem 1.5rem; transition: all 0.3s; }");
            out.println("    .sidebar-menu a:hover { background: var(--secondary); }");
            out.println("    .sidebar-menu a span { margin-left: 10px; }");
            out.println("    .menu-title { color: rgba(255,255,255,0.7); font-size: 0.9rem; padding: 1rem 1.5rem 0.5rem; text-transform: uppercase; }");
            out.println("    .login-item { margin-top: 1.5rem; }");
            out.println("    .login-btn { background: var(--secondary); border-radius: 4px; margin: 0 1rem; text-align: center; }");
            out.println("    .logout-btn { background: #e74c3c; border-radius: 4px; margin: 0 1rem; text-align: center; display: block; color: white; padding: 0.8rem 1.5rem; text-decoration: none; }");
            out.println("    .logout-btn:hover { background: #c0392b; }");
            out.println("    .main-content { margin-left: var(--sidebar-width); flex: 1; padding: 2rem; }");
            out.println("    .container { max-width: 1200px; margin: 0 auto; }");
            out.println("    .card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); padding: 2rem; margin-bottom: 2rem; }");
            out.println("    .form-group { margin-bottom: 1rem; }");
            out.println("    label { display: block; margin-bottom: 0.5rem; color: var(--primary); }");
            out.println("    input, select { width: 100%; padding: 0.8rem; border: 1px solid #ddd; border-radius: 5px; }");
            out.println("    button { background: var(--secondary); color: white; padding: 0.8rem 1.5rem; border: none; border-radius: 5px; cursor: pointer; }");
            out.println("    button:hover { opacity: 0.9; }");
            out.println("    .data-table { width: 100%; border-collapse: collapse; margin: 1rem 0; }");
            out.println("    .data-table th, .data-table td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }");
            out.println("    .data-table th { background: var(--primary); color: white; }");
            out.println("    .data-table tr:hover { background: var(--light); }");
            out.println("    .alert { padding: 1rem; border-radius: 5px; margin: 1rem 0; }");
            out.println("    .alert-success { background: #d4edda; color: #155724; }");
            out.println("    .alert-error { background: #f8d7da; color: #721c24; }");
            out.println("  </style>");
            out.println("</head>");
            out.println("<body>");
            out.println(sidebarMenu);
            out.println("  <div class='main-content'>");
            out.println("    <div class='container'>");
            out.println(konten);
            out.println("    </div>");
            out.println("  </div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        // Cegah cache halaman agar tidak bisa diakses setelah logout
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        HttpSession session = request.getSession(false);

        // Redirect ke login jika belum login
        if (session == null 
            || session.getAttribute("isLoggedIn") == null 
            || !(Boolean) session.getAttribute("isLoggedIn")) {
            response.sendRedirect("login.jsp");
            return;
        }

        String konten = request.getAttribute("konten") != null ? (String) request.getAttribute("konten") : "";

        tampilkan(request, response, konten);
    }
}
