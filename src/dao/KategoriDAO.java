package dao;

import config.Koneksi;
import model.Kategori;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategoriDAO {

    public List<Kategori> getAll() {
        List<Kategori> list = new ArrayList<>();
        try {
            String sql = "SELECT id_kategori, nama_kategori FROM tb_kategori ORDER BY id_kategori";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new Kategori(rs.getInt("id_kategori"), rs.getString("nama_kategori")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Kategori k) {
        try {
            String sql = "INSERT INTO tb_kategori (nama_kategori) VALUES (?)";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, k.getNamaKategori());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Kategori k) {
        try {
            String sql = "UPDATE tb_kategori SET nama_kategori=? WHERE id_kategori=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, k.getNamaKategori());
            pstmt.setInt(2, k.getIdKategori());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM tb_kategori WHERE id_kategori=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
