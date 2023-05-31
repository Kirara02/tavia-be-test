# Petunjuk Menjalankan Proyek

## Konfigurasi Database

Proyek ini menggunakan file `application.properties` untuk mengkonfigurasi koneksi ke database. Untuk mengganti nama database, user, dan password, ikuti langkah-langkah berikut:

1. Buka file `application.properties` yang terletak di `src/main/resources`.
2. Temukan bagian yang berkaitan dengan konfigurasi database, biasanya memiliki format seperti berikut:

   ```properties
   # Konfigurasi Database
   spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
   spring.datasource.username=myuser
   spring.datasource.password=mypassword
   ```

## Menjalankan Proyek

Untuk menjalankan proyek, ikuti langkah-langkah berikut:

1. Buka terminal atau command prompt.
2. Pastikan Anda berada di direktori proyek.
3. Ketik perintah berikut:

   ```shell
   mvn spring-boot:run
   ```
