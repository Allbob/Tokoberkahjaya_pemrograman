package dao;

import config.Koneksi;
import model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tb_customer";
            Connection conn = Koneksi.configDB();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                Customer c = new Customer(
                    rs.getString("id_customer"),
                    rs.getString("nama_customer"),
                    rs.getString("alamat"),
                    rs.getString("telepon")
                );
                list.add(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Customer c) {
        try {
            String sql = "INSERT INTO tb_customer (id_customer, nama_customer, alamat, telepon) VALUES (?, ?, ?, ?)";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getIdCustomer());
            pstmt.setString(2, c.getNamaCustomer());
            pstmt.setString(3, c.getAlamat());
            pstmt.setString(4, c.getNoTelepon());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Customer c) {
        try {
            String sql = "UPDATE tb_customer SET nama_customer=?, alamat=?, telepon=? WHERE id_customer=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, c.getNamaCustomer());
            pstmt.setString(2, c.getAlamat());
            pstmt.setString(3, c.getNoTelepon());
            pstmt.setString(4, c.getIdCustomer());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String id) {
        try {
            String sql = "DELETE FROM tb_customer WHERE id_customer=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
