package main;

import config.Koneksi;
import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        try {
            Connection conn = Koneksi.configDB();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "tb_penjualan", "id_customer");
            if (rs.next()) {
                String isNullable = rs.getString("IS_NULLABLE");
                System.out.println("id_customer IS_NULLABLE: " + isNullable);
            }
            
            // Check foreign keys
            ResultSet rsFK = meta.getImportedKeys(null, null, "tb_penjualan");
            while (rsFK.next()) {
                System.out.println("FK on column: " + rsFK.getString("FKCOLUMN_NAME") + " references " + rsFK.getString("PKTABLE_NAME"));
            }
            
            // Check id_user
            ResultSet rsUser = meta.getColumns(null, null, "tb_penjualan", "id_user");
            if (rsUser.next()) {
                System.out.println("id_user exists.");
            } else {
                System.out.println("id_user DOES NOT EXIST in tb_penjualan!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
