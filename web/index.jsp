<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% response.sendRedirect(request.getContextPath() + "/MainForm"); %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Form Utama</title>
        <link rel="stylesheet" type="text/css" href="style.css">
    </head>
    <body>
        <div id="header">
            <h1>APLIKASI GAJI KARYAWAN PT MUHAMAD SALWARUD</h1>
            <h2>SINGAPORE</h2>
        </div>
        <div id="menu">
            <ul>
                <li><a href="#">Home</a></li>
                <li><a href="#">Data Karyawan</a></li>
                <li><a href="#">Penggajian</a></li>
                <li><a href="#">Laporan</a></li>
            </ul>
        </div>
        <div id="content">
            <h3>Selamat Datang di Aplikasi Gaji Karyawan PT MUHAMAD SALWARUD</h3>
            <p>
                Silakan pilih menu yang tersedia untuk mengelola data karyawan, penggajian, dan laporan.
            </p>
        </div>
        <div id="footer">
            <p>Copyright &copy; 2025 PT MUHAMAD SALWARUD</p>
        </div>
    </body>
</html>
