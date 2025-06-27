# 🏢 Aplikasi Penggajian Karyawan PT. Salwarud

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/NetBeans%20IDE-1B6AC6?style=for-the-badge&logo=apache-netbeans-ide&logoColor=white" alt="NetBeans">
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/MVC%20Architecture-blue?style=for-the-badge" alt="MVC Architecture">
</p>

Aplikasi web penggajian karyawan yang dirancang untuk mengelola data gaji secara efisien. Proyek ini dibangun sepenuhnya dengan **Java** menggunakan **NetBeans IDE 8.2** dan menerapkan pola desain **Model-View-Controller (MVC)** untuk memastikan kode yang terstruktur dan mudah dikelola.

---

## ✨ Fitur Utama

-   **👤 Manajemen Karyawan:** Operasi CRUD (Create, Read, Update, Delete) penuh untuk data karyawan.
-   **🛠️ Manajemen Pekerjaan:** Mengelola data master pekerjaan beserta detailnya.
-   **💰 Transaksi Gaji:** Memproses, menghitung, dan menyimpan riwayat gaji karyawan.
-   **🔐 Sistem Login & Keamanan:** Sistem otentikasi pengguna dengan enkripsi password menggunakan **MD5**.
-   **🔍 Pencarian Data:** Fitur pencarian canggih untuk data gaji berdasarkan KTP atau kode pekerjaan.
-   **📄 Laporan Dinamis:** Menghasilkan laporan penggajian yang fleksibel dengan **JasperReports**, yang dapat diekspor ke berbagai format (Excel, ODT, RTF).

---

## 🏗️ Arsitektur & Teknologi

Aplikasi ini dibangun di atas tumpukan teknologi Java klasik dan mengikuti arsitektur MVC yang jelas untuk memisahkan logika bisnis, data, dan presentasi.

| Kategori      | Teknologi / Pustaka                                 |
| :------------ | :-------------------------------------------------- |
| **Bahasa** | `Java`                                              |
| **IDE** | `NetBeans IDE 8.2`                                  |
| **Server** | `GlassFish Server`                                  |
| **Database** | `MySQL` (terhubung via JDBC)                        |
| **Pelaporan** | `JasperReports`                                     |
| **Dependensi** | `MySQL JDBC Driver`, `Apache Commons`, `iText`, `POI` |

<br>

<details>
<summary>📂 Struktur Proyek</summary>

```

ApplikasiGajiKaryawan/
├── src/
│   └── java/
│       └── com/
│           └── unpam/
│               ├── controller/ (Logika & Jembatan)
│               │   ├── GajiController.java
│               │   └── KaryawanController.java
│               ├── model/ (Data & Logika Bisnis)
│               │   ├── Gaji.java
│               │   └── Karyawan.java
│               └── view/ (Tampilan & UI)
│                   ├── MainForm.java
│                   └── PesanDialog.java
├── web/
│   ├── WEB-INF/
│   │   └── web.xml
│   └── style.css
└── build.xml

````

</details>

---

## 🚀 Alur Kerja & Contoh Kode

Aplikasi ini memiliki alur kerja yang terstruktur mulai dari otentikasi hingga pelaporan.

1.  **Login:** Pengguna melakukan otentikasi melalui `LoginController` yang memvalidasi kredensial.
2.  **Dashboard:** Setelah berhasil, pengguna diarahkan ke `MainForm` yang menjadi pusat navigasi.
3.  **Manajemen Data:** Pengguna mengelola data Karyawan, Pekerjaan, atau Gaji melalui `Controller` yang sesuai.
4.  **Pembuatan Laporan:** `LaporanGajiController` memproses permintaan laporan dan menggunakan `JasperReports` untuk menghasilkan output.

<br>

<details>
<summary>📦 Contoh Kode Model (Koneksi Database)</summary>

```java
// com/unpam/model/Koneksi.java
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
````

\</details\>

\<details\>
\<summary\>🎮 Contoh Kode Controller (Logika Karyawan)\</summary\>

```java
// com/unpam/controller/KaryawanController.java
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
```

\</details\>


### Perubahan Utama:

1.  **Header Profesional:** Menambahkan *badges* (lencana) di bagian atas untuk menyoroti teknologi utama yang Anda gunakan (Java, NetBeans, MySQL). Ini memberikan kesan pertama yang modern.
2.  **Struktur yang Jelas:** Menggunakan heading (`##`) untuk membagi README menjadi bagian-bagian yang logis seperti "Fitur Utama", "Arsitektur & Teknologi", dan "Alur Kerja".
3.  **Tabel Teknologi:** Mengubah daftar teknologi menjadi tabel yang rapi agar lebih mudah dibaca.
4.  **Struktur Proyek yang Bisa Dilipat (`<details>`):** Struktur direktori dan contoh kode yang panjang saya masukkan ke dalam tag `<details>` yang bisa diklik untuk dibuka. Ini membuat README utama terlihat jauh lebih ringkas dan tidak mengintimidasi.
5.  **Penekanan dengan Bold:** Memberi penekanan pada kata kunci penting seperti **Java**, **NetBeans IDE 8.2**, dan **Model-View-Controller (MVC)** menggunakan `**`.

Sekarang README Anda terlihat jauh lebih terstruktur dan profesional!
```
