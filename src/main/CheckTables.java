package main;

import config.Koneksi;
import java.sql.*;

public class CheckTables {
    public static void main(String[] args) {
        try {
            Connection conn = Koneksi.configDB();
            String catalog = conn.getCatalog();
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(catalog, null, "%", new String[]{"TABLE"});
            System.out.println("--- TABLES IN DATABASE: " + catalog + " ---");
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("Table: " + tableName);
                ResultSet columns = meta.getColumns(catalog, null, tableName, "%");
                while (columns.next()) {
                    System.out.println("  - " + columns.getString("COLUMN_NAME") + " (" + columns.getString("TYPE_NAME") + ")");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
