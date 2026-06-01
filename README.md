# Tabungin
**Proyek Akhir Lab PBO 2026 - Kelompok 24**

Tabungin adalah aplikasi desktop manajemen tabungan pribadi berbasis Java (JavaFX) yang membantu warga mengelola keuangan mereka secara terstruktur — mulai dari tabungan bulanan, wishlist, hingga deposit.

## Prasyarat

Pastikan sudah terinstal:
- **Java JDK**
- **Gradle** (atau gunakan `gradlew` bawaan proyek)
- **XAMPP** (untuk menjalankan MySQL secara lokal)

## Setup Database (Local MySQL via XAMPP)

Project ini menggunakan MySQL lokal. Konfigurasi koneksi ada pada `app/src/main/resources/db.properties`, sedangkan semua inisialisasi tabel ada pada `app/src/main/java/mytabungan/database/DBIniatializer.java`.

### Langkah 1 — Jalankan XAMPP
1. Buka **XAMPP Control Panel**
2. Klik **Start** pada modul **Apache** dan **MySQL**
3. Pastikan keduanya berstatus **Running** (ditandai warna hijau)

### Langkah 2 — Buat Database
1. Buka browser, akses `http://localhost/phpmyadmin`
2. Klik tab **SQL** di bagian atas
3. Jalankan perintah berikut:
```sql
CREATE DATABASE tabungin;
```
4. Pastikan database `tabungin` sudah muncul di panel kiri

### Langkah 3 — Konfigurasi Koneksi
1. Salin file contoh konfigurasi: `db.properties.example` → `db.properties`
2. Isi file `db.properties` sesuai pengaturan MySQL lokal:
```properties
DB_URL=jdbc:mysql://localhost:3306/tabungin
DB_USER=root
DB_PASSWORD=
```
> Secara default XAMPP menggunakan user `root` tanpa password. Sesuaikan jika menggunakan password berbeda.

### Langkah 4 — Buat Tabel

Tabel akan **dibuat otomatis** saat aplikasi pertama kali dijalankan melalui `DBIniatializer.init()` yang dipanggil di `App.java`. Tabel yang akan dibuat:

#### Tabel `users`
| Kolom | Tipe | Keterangan |
|---|---|---|
| id | INT AUTO_INCREMENT | Primary key |
| username | VARCHAR(50) | Unik, tidak boleh kosong |
| email | VARCHAR(100) | Email pengguna |
| password | VARCHAR(255) | Disimpan dalam bentuk hash (BCrypt) |
| created_at | TIMESTAMP | Waktu registrasi |

#### Tabel `tabungan_utama`
| Kolom | Tipe | Keterangan |
|---|---|---|
| id | INT AUTO_INCREMENT | Primary key |
| user_id | INT | Foreign key ke `users` |
| target_amount | DECIMAL(12,2) | Target tabungan |
| saved_amount | DECIMAL(12,2) | Jumlah yang sudah ditabung |
| period_month | VARCHAR(20) | Periode bulan tabungan |
| created_at | TIMESTAMP | Waktu dibuat |

#### Tabel `wishlists`
| Kolom | Tipe | Keterangan |
|---|---|---|
| id | INT AUTO_INCREMENT | Primary key |
| user_id | INT | Foreign key ke `users` |
| title | VARCHAR(100) | Nama wishlist |
| target_price | DECIMAL(12,2) | Harga target |
| saved_amount | DECIMAL(12,2) | Dana yang sudah terkumpul |
| max_limit | DECIMAL(12,2) | Batas maksimal alokasi (%) |
| status | VARCHAR(20) | `ONGOING` / `DONE` |
| period | VARCHAR(20) | Periode wishlist |
| created_at | TIMESTAMP | Waktu dibuat |

#### Tabel `deposits`
| Kolom | Tipe | Keterangan |
|---|---|---|
| id | INT AUTO_INCREMENT | Primary key |
| user_id | INT | Foreign key ke `users` |
| saving_type | VARCHAR(20) | Jenis tabungan |
| reference_id | INT | ID referensi ke tabel terkait |
| amount | DECIMAL(12,2) | Jumlah deposit |
| created_at | TIMESTAMP | Waktu deposit |

#### Catatan
- Jalankan aplikasi satu kali agar semua tabel terbuat otomatis.
- Apabila muncul error `database doesn't exist`, pastikan database `tabungin` sudah dibuat terlebih dahulu.
- Apabila ingin mengubah struktur tabel, edit `DBIniatializer.java` lalu hapus tabel lama melalui phpMyAdmin sebelum menjalankan ulang.
- Setelah tabel berhasil dibuat, verifikasi melalui phpMyAdmin — seharusnya terdapat 4 tabel: `users`, `tabungan_utama`, `wishlists`, `deposits`.

## Cara Menjalankan Program

Pastikan XAMPP sudah berjalan dan `db.properties` sudah dikonfigurasi, lalu jalankan di terminal:

```bash

./gradlew run
#or
gradlew run
```

## Deskripsi Aplikasi

**Tabungin** adalah aplikasi manajemen tabungan pribadi yang dirancang untuk membantu pengguna mengelola keuangan sehari-hari. Fitur yang tersedia:
- Membuat dan memantau **tabungan bulanan** dengan target tertentu
- Mengelola **wishlist** barang yang ingin dibeli beserta alokasi dananya
- Mencatat **deposit** secara berkala
- Melihat **pertumbuhan tabungan** secara visual
- Login dan registrasi dengan sistem autentikasi yang aman (password di-hash menggunakan BCrypt)

## Struktur Folder

```
app/src/main/java/mytabungan/
├── App.java                  # Entry point aplikasi JavaFX
├── dao/                      # Data Access Object — operasi database
│   ├── UserDAO.java
│   ├── SavingDAO.java
│   ├── DepositDAO.java
│   └── WishlistDAO.java
├── database/                 # Konfigurasi dan inisialisasi database
│   ├── DatabaseConfig.java   # Koneksi ke MySQL via db.properties
│   └── DBIniatializer.java   # Pembuatan tabel otomatis
├── models/                   # Model data (representasi tabel)
│   ├── User.java
│   ├── Saving.java           # Abstract base class
│   ├── MonthlySaving.java    # Extends Saving
│   ├── Wishlist.java         # Extends Saving
│   └── Deposit.java
├── scenes/                   # Tampilan UI (JavaFX Scene)
│   ├── AuthLayout.java       # Panel kiri yang dipakai Login & Register
│   ├── LoginScene.java
│   ├── RegisterScene.java
│   ├── MainScene.java
│   ├── TabunganScene.java
│   ├── WishlistScene.java
│   ├── GrowthScene.java
│   └── Sidebar.java
└── utils/                    # Utilitas umum
    ├── ValidationUtil.java   # Validasi input (email, password, username)
    ├── SessionManager.java   # Manajemen sesi user yang sedang login
    └── PasswordUtil.java     # Hash & verifikasi password (BCrypt)
```

## Struktur Kode

### Entry Point
`App.java` adalah kelas utama yang meng-extend `Application` (JavaFX). Saat dijalankan, ia memanggil `DBIniatializer.init()` untuk memastikan semua tabel sudah ada, lalu menampilkan `LoginScene`.

### Alur Autentikasi
1. Pengguna membuka aplikasi → `LoginScene`
2. Apabila belum memiliki akun → navigasi ke `RegisterScene`
3. Setelah login berhasil → `SessionManager.login(user)` menyimpan sesi
4. Navigasi ke `MainScene` dengan sidebar navigasi

### Pola DAO
Setiap tabel memiliki kelas DAO tersendiri yang menangani operasi CRUD menggunakan koneksi dari `DatabaseConfig.connect()`.

### Konfigurasi Database
Koneksi dibaca dari file `db.properties` (tidak di-hardcode) sehingga mudah disesuaikan per environment tanpa mengubah kode.

## Penerapan Pilar OOP (Object Oriented Programming)

### 1. Encapsulation
**Lokasi:** `app/src/main/java/mytabungan/models/`

Seluruh field pada kelas model dideklarasikan sebagai `private` sehingga tidak dapat diakses langsung dari luar kelas. Akses hanya diperbolehkan melalui method getter yang telah disediakan.

**Contoh pada `User.java`:**
```java
private int id;
private String username;
private String email;
private String password;

public int getId()         { return id; }
public String getUsername(){ return username; }
public String getEmail()   { return email; }
```

**Contoh pada `Saving.java`:**
```java
private int id;
private int userId;
protected double targetAmount;
protected double savedAmount;

public int getId()             { return id; }
public double getTargetAmount(){ return targetAmount; }
public double getSavedAmount() { return savedAmount; }
```

Field `targetAmount` dan `savedAmount` dideklarasikan `protected` agar dapat diakses langsung oleh subclass (`MonthlySaving`, `Wishlist`) tanpa perlu melewati getter, sementara tetap tersembunyi dari kelas luar.

Encapsulation juga diterapkan pada `DatabaseConfig.java` — detail koneksi (URL, user, password) dibaca secara internal dari `db.properties` dan tidak diekspos ke kelas lain selain melalui method `connect()`.

### 2. Inheritance
**Lokasi:** `app/src/main/java/mytabungan/models/`

`MonthlySaving` dan `Wishlist` merupakan subclass yang meng-extend kelas abstrak `Saving`. Dengan pewarisan ini, kedua subclass tidak perlu mendefinisikan ulang field dan method umum yang sudah ada di `Saving`.

**Hierarki pewarisan:**
```
Saving  (abstract)
├── MonthlySaving
└── Wishlist
```

**Field yang diwarisi dari `Saving.java`:**
```java
protected double targetAmount;
protected double savedAmount;
// beserta: id, userId, createdAt
```

**Method yang diwarisi dari `Saving.java`:**
```java
public int getId()
public int getUserId()
public double getTargetAmount()
public double getSavedAmount()
public LocalDateTime getCreatedAt()
```

**Contoh pewarisan pada `MonthlySaving.java`:**
```java
public class MonthlySaving extends Saving {
    private String periodMonth;

    public MonthlySaving(int id, int userId, double targetAmount,
                         double savedAmount, String periodMonth,
                         LocalDateTime createdAt) {
        super(id, userId, targetAmount, savedAmount, createdAt);
        this.periodMonth = periodMonth;
    }
}
```

**Contoh pewarisan pada `Wishlist.java`:**
```java
public class Wishlist extends Saving {
    private String title;
    private double maxLimit;
    private String status;
    private String period;

    public Wishlist(...) {
        super(id, userId, targetAmount, savedAmount, createdAt);
        ...
    }
}
```

### 3. Polymorphism
**Lokasi:** `app/src/main/java/mytabungan/models/`

Tiga method abstrak yang dideklarasikan di `Saving.java` diimplementasikan secara berbeda oleh masing-masing subclass sesuai dengan logika bisnis yang berlaku.

**Deklarasi di `Saving.java` (abstract):**
```java
public abstract boolean isReached();
abstract double getRemaining();
abstract double getProgressPercentage();
```

**Implementasi di `MonthlySaving.java`:**
```java
@Override
public boolean isReached() {
    return getSavedAmount() >= getTargetAmount();
}

@Override
public double getRemaining() {
    return Math.max(0, getTargetAmount() - getSavedAmount());
}

@Override
public double getProgressPercentage() {
    if (targetAmount == 0) return 0;
    return Math.min(100, (savedAmount / targetAmount) * 100);
}
```

**Implementasi di `Wishlist.java`:**
```java
@Override
public boolean isReached() {
    return getSavedAmount() >= getTargetAmount();
}

@Override
public double getRemaining() {
    return Math.max(0, getTargetAmount() - getSavedAmount());
}

@Override
public double getProgressPercentage() {
    if (targetAmount == 0) return 0;
    return Math.min(100, (savedAmount / targetAmount) * 100);
}
```

`Wishlist` juga memiliki method tambahan `calculateAllocation()` yang tidak dimiliki `MonthlySaving`, menunjukkan bahwa setiap subclass dapat memiliki perilaku yang spesifik terhadap jenisnya:
```java
// Wishlist.java
public double calculateAllocation(double totalSaving) {
    return totalSaving * maxLimit / 100;
}
```

### 4. Abstraction
**Lokasi:** `app/src/main/java/mytabungan/models/Saving.java`

Kelas `Saving` dideklarasikan sebagai `abstract class`, artinya kelas ini tidak dapat diinstansiasi secara langsung. Ia berfungsi sebagai kontrak (blueprint) yang memaksa semua subclass untuk mengimplementasikan method-method inti.

```java
public abstract class Saving {
    // Atribut umum semua jenis tabungan
    private int id;
    private int userId;
    protected double targetAmount;
    protected double savedAmount;
    protected LocalDateTime createdAt;

    // Method umum yang langsung diwarisi
    public int getId() { ... }
    public double getTargetAmount() { ... }
    public double getSavedAmount() { ... }

    // Kontrak abstrak — wajib diimplementasikan oleh subclass
    public abstract boolean isReached();
    abstract double getRemaining();
    abstract double getProgressPercentage();
}
```

Abstraksi juga diterapkan pada lapisan DAO. Setiap DAO (`UserDAO`, `SavingDAO`, `WishlistDAO`, `DepositDAO`) menyembunyikan seluruh detail query SQL dari lapisan UI (scenes). Scene hanya perlu memanggil method tanpa perlu mengetahui bagaimana query dieksekusi di dalam.

**Contoh pada `UserDAO.java`:**
```java
// Dipanggil dari LoginScene.java — scene tidak mengetahui detail SQL-nya
public User login(String usernameOrEmail, String password) { ... }
public boolean register(User user) { ... }
public boolean isEmailExists(String email) { ... }
public boolean isUsernameExists(String username) { ... }
```