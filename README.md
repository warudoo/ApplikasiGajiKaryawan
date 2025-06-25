Aplikasi Penggajian Karyawan PT. Salwarud 🏢
Selamat datang di repositori Aplikasi Penggajian Karyawan! Proyek ini adalah aplikasi web yang dirancang untuk mengelola data gaji karyawan secara efisien. Dibangun sepenuhnya dengan Java di NetBeans IDE 8.2, aplikasi ini menerapkan pola desain Model-View-Controller (MVC) untuk memastikan kode yang terstruktur dan mudah dikelola.

✨ Fitur Utama
👤 Manajemen Karyawan: Melakukan operasi CRUD (Create, Read, Update, Delete) untuk data karyawan.

🛠️ Manajemen Pekerjaan: Mengelola data master pekerjaan beserta detailnya.

💰 Transaksi Gaji: Memproses, menghitung, dan menyimpan data gaji karyawan.

🔐 Sistem Login & Keamanan: Dilengkapi dengan sistem otentikasi pengguna. Password disimpan dengan aman menggunakan enkripsi MD5.

🔍 Pencarian Data: Fitur pencarian canggih untuk menemukan data gaji berdasarkan KTP atau kode pekerjaan.

📄 Laporan Dinamis: Menghasilkan laporan penggajian yang fleksibel dengan JasperReports. Laporan dapat diekspor ke berbagai format, termasuk:

Excel (.xlsx, .xls)

Open Document Text (.odt)

Rich Text Format (.rtf)

🏗️ Arsitektur Proyek (MVC)
Aplikasi ini mengikuti arsitektur Model-View-Controller untuk memisahkan logika bisnis, data, dan antarmuka pengguna.

📦 Model
Bagian ini bertanggung jawab atas data dan logika bisnis aplikasi.

Karyawan.java: Mengelola data dan operasi terkait karyawan.

Pekerjaan.java: Mengelola data dan operasi terkait pekerjaan.

Gaji.java: Mengelola data gaji dan integrasi dengan JasperReports.

Koneksi.java: Mengatur koneksi ke database MySQL.

Enkripsi.java: Menyediakan utilitas untuk hashing password.

CompileReport.java: Bertugas meng-compile file laporan Jasper.

🖼️ View
Bagian ini bertanggung jawab atas tampilan dan antarmuka pengguna.

MainForm.java: Bertindak sebagai template utama yang menampilkan menu navigasi dan konten dinamis.

Tampilan Dinamis: Semua halaman, form, dan tabel data dihasilkan secara dinamis oleh Controller dalam format HTML, memberikan pengalaman pengguna yang responsif.

PesanDialog.java: Menampilkan pesan atau notifikasi kepada pengguna.

style.css: Mengatur seluruh tampilan visual aplikasi.

🎮 Controller (Java Servlets)
Bagian ini bertindak sebagai jembatan antara Model dan View, menangani semua permintaan HTTP dari pengguna.

LoginController.java: Mengelola proses otentikasi.

LogoutController.java: Mengelola proses keluar dari sistem.

KaryawanController.java: Menangani semua logika untuk CRUD data karyawan.

PekerjaanController.java: Menangani logika untuk CRUD data pekerjaan.

GajiController.java: Mengatur alur kerja untuk transaksi dan manajemen gaji.

LaporanGajiController.java: Mengelola logika untuk memfilter dan menghasilkan laporan gaji.

🚀 Teknologi & Pustaka
Kategori

Teknologi / Pustaka

Bahasa

Java

IDE

NetBeans IDE 8.2

Server

GlassFish Server

Database

MySQL (terhubung via JDBC)

Pelaporan

JasperReports

Dependensi

MySQL JDBC Driver, Pustaka Apache Commons, iText, POI

⚙️ Alur Kerja Aplikasi
Login: Pengguna mengakses aplikasi dan harus melakukan login. LoginController akan memvalidasi kredensial dengan mengenkripsi password yang diinput.

Dashboard: Setelah berhasil login, pengguna akan diarahkan ke halaman utama (MainForm) yang berisi menu navigasi.

Manajemen Data: Pengguna dapat memilih menu untuk mengelola data master (Karyawan, Pekerjaan) atau data transaksi (Gaji). Setiap aksi (tambah, ubah, hapus) akan ditangani oleh Controller yang sesuai.

Pembuatan Laporan: Melalui menu laporan, LaporanGajiController akan menampilkan opsi filter. Setelah filter diterapkan, JasperReports akan menghasilkan laporan sesuai format yang dipilih.