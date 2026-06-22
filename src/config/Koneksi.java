package config;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
    private static Connection mysqlconfig;

    public static Connection configDB() {
        try {
            if (mysqlconfig == null || mysqlconfig.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/db_toko_berkah_jaya";
                String user = "root";
                String pass = "";
                Class.forName("com.mysql.cj.jdbc.Driver");
                mysqlconfig = DriverManager.getConnection(url, user, pass);
            }
        } catch (Exception e) {
            System.err.println("Koneksi gagal " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Gagal koneksi ke database! " + e.getMessage());
        }
        return mysqlconfig;
    }
}
