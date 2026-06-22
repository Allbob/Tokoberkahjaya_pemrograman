package dao;

import config.Koneksi;
import model.Barang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public List<Barang> getAll() {
        List<Barang> list = new ArrayList<>();
        try {
            String sql = "SELECT id_barang, id_kategori, nama_barang, satuan, harga_jual, stok FROM tb_barang";
            Connection conn = Koneksi.configDB();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Barang(
                    rs.getString("id_barang"),
                    rs.getInt("id_kategori"),
                    rs.getString("nama_barang"),
                    rs.getString("satuan"),
                    rs.getDouble("harga_jual"),
                    rs.getInt("stok")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Barang b) {
        try {
            String sql = "INSERT INTO tb_barang (id_barang, id_kategori, nama_barang, satuan, harga_jual, stok) VALUES (?, ?, ?, ?, ?, ?)";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, b.getIdBarang());
            pstmt.setInt(2, b.getIdKategori());
            pstmt.setString(3, b.getNamaBarang());
            pstmt.setString(4, b.getSatuan());
            pstmt.setDouble(5, b.getHargaJual());
            pstmt.setInt(6, b.getStok());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Barang b) {
        try {
            String sql = "UPDATE tb_barang SET id_kategori=?, nama_barang=?, satuan=?, harga_jual=?, stok=? WHERE id_barang=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, b.getIdKategori());
            pstmt.setString(2, b.getNamaBarang());
            pstmt.setString(3, b.getSatuan());
            pstmt.setDouble(4, b.getHargaJual());
            pstmt.setInt(5, b.getStok());
            pstmt.setString(6, b.getIdBarang());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String id) {
        try {
            String sql = "DELETE FROM tb_barang WHERE id_barang=?";
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
