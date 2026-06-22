package main;

import config.Koneksi;
import java.sql.*;

public class TestDB2 {
    public static void main(String[] args) {
        try {
            Connection conn = Koneksi.configDB();
            Statement stmt = conn.createStatement();
            
            ResultSet rs = stmt.executeQuery("SELECT id_user, username FROM tb_user");
            while(rs.next()) {
                System.out.println("User: " + rs.getInt(1) + " - " + rs.getString(2));
            }
            
            ResultSet rsC = stmt.executeQuery("SELECT id_customer, nama_customer FROM tb_customer");
            while(rsC.next()) {
                System.out.println("Customer: " + rsC.getString(1) + " - " + rsC.getString(2));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
