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
Aplikasi ini mengikuti arsitektur Model-View-Controller. Berikut adalah gambaran struktur direktori proyek:

ApplikasiGajiKaryawan/
├── src/
│   └── java/
│       └── com/
│           └── unpam/
│               ├── controller/
│               │   ├── GajiController.java
│               │   ├── KaryawanController.java
│               │   └── ... (servlet lainnya)
│               ├── model/
│               │   ├── Gaji.java
│               │   ├── Karyawan.java
│               │   └── ... (model lainnya)
│               └── view/
│                   ├── MainForm.java
│                   └── PesanDialog.java
├── web/
│   ├── WEB-INF/
│   │   └── web.xml
│   └── style.css
└── build.xml

📦 Model
Bagian ini bertanggung jawab atas data dan logika bisnis aplikasi. Contohnya adalah kelas Koneksi.java yang mengelola hubungan ke database.

// Contoh dari com/unpam/model/Koneksi.java
public class Koneksi {
    public Connection con;
    public Statement stm;

    public void koneksi() {
        try {
            String url = "jdbc:mysql://localhost/db_gaji_karyawan";
            String username = "root";
            String password = ""; // Password default XAMPP
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, username, password);
            stm = con.createStatement();
        } catch (Exception e) {
            // Sebaiknya gunakan logging di aplikasi production
            System.err.println("Koneksi Gagal: " + e.getMessage());
        }
    }
}

🖼️ View
Bagian ini bertanggung jawab atas tampilan dan antarmuka pengguna. Semua elemen HTML, seperti form dan tabel, digenerate secara dinamis oleh Controller dan disajikan melalui MainForm.java.

MainForm.java: Bertindak sebagai template utama.

PesanDialog.java: Menampilkan notifikasi.

style.css: Mengatur seluruh tampilan visual aplikasi.

🎮 Controller (Java Servlets)
Controller bertindak sebagai jembatan antara Model dan View. Setiap permintaan HTTP dari pengguna akan ditangani oleh servlet yang sesuai.

// Contoh dari com/unpam/controller/KaryawanController.java
@WebServlet(name = "KaryawanController", urlPatterns = {"/KaryawanController"})
public class KaryawanController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Karyawan karyawan = new Karyawan();
            // Logika untuk simpan, ubah, atau hapus data
            String proses = request.getParameter("proses");
            
            if (proses != null) {
                switch (proses) {
                    case "simpan":
                        // ... logika menyimpan data
                        break;
                    case "ubah":
                        // ... logika mengubah data
                        break;
                }
            }

            // Tampilkan view
            MainForm.buka(request, response, "Karyawan", karyawan.tampilData());
        }
    }
    // ... metode doGet dan doPost
}

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