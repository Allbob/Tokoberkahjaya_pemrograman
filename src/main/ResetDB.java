package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ResetDB {
    public static void main(String[] args) {
        String mysqlUrl = "jdbc:mysql://localhost:3306/?allowMultiQueries=true";
        String user = "root";
        String pass = "";
        String sqlFilePath = "db_toko_berkah_jaya.sql";

        System.out.println("Memulai reset database dari " + sqlFilePath + "...");

        try (Connection conn = DriverManager.getConnection(mysqlUrl, user, pass);
             Statement stmt = conn.createStatement()) {

            Class.forName("com.mysql.cj.jdbc.Driver");

            BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Lewati komentar SQL dan baris kosong
                String trimmed = line.trim();
                if (trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                    continue;
                }
                
                sb.append(line).append("\n");

                // Jika diakhiri dengan semicolon, eksekusi statement
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    try {
                        stmt.execute(sql);
                    } catch (Exception e) {
                        System.err.println("Gagal mengeksekusi SQL: " + sql);
                        System.err.println("Error: " + e.getMessage());
                    }
                    sb.setLength(0); // Reset buffer
                }
            }
            reader.close();

            System.out.println("Reset database berhasil dilakukan!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Terjadi kesalahan sistem: " + e.getMessage());
        }
    }
}
