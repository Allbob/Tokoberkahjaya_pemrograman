package main;

import config.Koneksi;
import java.sql.*;

public class FixDB {
    public static void main(String[] args) {
        try {
            Connection conn = Koneksi.configDB();
            PreparedStatement ps = conn.prepareStatement(
                "INSERT IGNORE INTO tb_customer (id_customer, nama_customer, alamat, telepon) " +
                "VALUES ('C000', 'Umum (Tanpa Nama)', '-', '-')"
            );
            ps.executeUpdate();
            System.out.println("Customer C000 created!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
