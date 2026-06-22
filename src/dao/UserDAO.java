package dao;

import config.Koneksi;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public static int loggedInUserId = 1; // Default fallback
    public static String loggedInUserLevel = "Admin"; // Default fallback
    public static String loggedInUserFullName = "Administrator"; // Default fallback

    // ── LOGIN ────────────────────────────────────────────────────────────────
    public boolean login(String username, String password) {
        try {
            String sql = "SELECT * FROM tb_user WHERE username=? AND password=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                loggedInUserId = rs.getInt("id_user");
                loggedInUserLevel = rs.getString("level");
                loggedInUserFullName = rs.getString("nama_lengkap");
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────
    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        try {
            String sql = "SELECT id_user, username, password, nama_lengkap, level FROM tb_user ORDER BY id_user";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new User(
                    rs.getInt("id_user"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("nama_lengkap"),
                    rs.getString("level")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── INSERT ───────────────────────────────────────────────────────────────
    public boolean insert(User u) {
        try {
            String sql = "INSERT INTO tb_user (username, password, nama_lengkap, level) VALUES (?, ?, ?, ?)";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPassword());
            pstmt.setString(3, u.getNamaLengkap());
            pstmt.setString(4, u.getLevel());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────
    public boolean update(User u) {
        try {
            String sql = "UPDATE tb_user SET username=?, password=?, nama_lengkap=?, level=? WHERE id_user=?";
            Connection conn = Koneksi.configDB();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPassword());
            pstmt.setString(3, u.getNamaLengkap());
            pstmt.setString(4, u.getLevel());
            pstmt.setInt(5, u.getIdUser());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE ───────────────────────────────────────────────────────────────
    public boolean delete(int id) {
        try {
            String sql = "DELETE FROM tb_user WHERE id_user=?";
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
