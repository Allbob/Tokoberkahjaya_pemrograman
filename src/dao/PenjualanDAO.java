package dao;

import config.Koneksi;
import model.Penjualan;
import model.DetailPenjualan;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PenjualanDAO {

    public String generateNoFaktur() {
        String noFaktur = "";
        try {
            Connection conn = Koneksi.configDB();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String todayStr = sdf.format(new java.util.Date());
            String prefix = "PJ-" + todayStr + "-";

            String sql = "SELECT no_faktur FROM tb_penjualan WHERE no_faktur LIKE ? ORDER BY no_faktur DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, prefix + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String lastFaktur = rs.getString("no_faktur");
                String counterStr = lastFaktur.substring(lastFaktur.length() - 4);
                int counter = Integer.parseInt(counterStr) + 1;
                noFaktur = prefix + String.format("%04d", counter);
            } else {
                noFaktur = prefix + "0001";
            }
        } catch (Exception e) {
            e.printStackTrace();
            noFaktur = "PJ-" + System.currentTimeMillis();
        }
        return noFaktur;
    }

    public boolean simpanTransaksi(Penjualan p, List<DetailPenjualan> details) {
        Connection conn = null;
        try {
            conn = Koneksi.configDB();
            conn.setAutoCommit(false);

            // 1. Insert ke tb_penjualan (header)
            String sqlInsert = "INSERT INTO tb_penjualan (no_faktur, tgl_transaksi, id_customer, total_bayar, id_user) " +
                               "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psInsert = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            psInsert.setString(1, p.getNoFaktur());
            psInsert.setDate(2, new java.sql.Date(p.getTglTransaksi().getTime()));
            psInsert.setString(3, p.getIdCustomer());
            psInsert.setDouble(4, p.getTotalBayar());
            psInsert.setInt(5, p.getIdUser());
            psInsert.executeUpdate();

            // Mendapatkan id_jual yang dibuat otomatis
            int idJual = 0;
            ResultSet rsKeys = psInsert.getGeneratedKeys();
            if (rsKeys.next()) {
                idJual = rsKeys.getInt(1);
            }

            // 2. Insert ke tb_detail_penjualan dan update stok barang
            String sqlDetail = "INSERT INTO tb_detail_penjualan (id_jual, id_barang, harga_satuan, jumlah_beli, subtotal) " +
                               "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psDetail = conn.prepareStatement(sqlDetail);

            String sqlUpdateStok = "UPDATE tb_barang SET stok = stok - ? WHERE id_barang = ?";
            PreparedStatement psUpdateStok = conn.prepareStatement(sqlUpdateStok);

            for (DetailPenjualan d : details) {
                psDetail.setInt(1, idJual);
                psDetail.setString(2, d.getIdBarang());
                psDetail.setDouble(3, d.getHargaSatuan());
                psDetail.setInt(4, d.getJumlahBeli());
                psDetail.setDouble(5, d.getSubtotal());
                psDetail.executeUpdate();

                psUpdateStok.setInt(1, d.getJumlahBeli());
                psUpdateStok.setString(2, d.getIdBarang());
                psUpdateStok.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    public List<Object[]> getLaporan() {
        List<Object[]> list = new ArrayList<>();
        try {
            String sql = "SELECT p.no_faktur, p.tgl_transaksi, COALESCE(c.nama_customer, 'Umum') as nama_customer, " +
                         "b.nama_barang, d.jumlah_beli, d.subtotal, u.username " +
                         "FROM tb_penjualan p " +
                         "LEFT JOIN tb_customer c ON p.id_customer = c.id_customer " +
                         "JOIN tb_detail_penjualan d ON p.id_jual = d.id_jual " +
                         "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                         "JOIN tb_user u ON p.id_user = u.id_user " +
                         "ORDER BY p.id_jual DESC, d.id_detail ASC";
            Connection conn = Koneksi.configDB();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("no_faktur"),
                    rs.getDate("tgl_transaksi"),
                    rs.getString("nama_customer"),
                    rs.getString("nama_barang"),
                    rs.getInt("jumlah_beli"),
                    rs.getDouble("subtotal"),
                    rs.getString("username")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public Object[] getHeaderTransaksi(String noFaktur) {
        try {
            String sql = "SELECT p.no_faktur, p.tgl_transaksi, COALESCE(c.nama_customer, 'Umum') as nama_customer, " +
                         "p.total_bayar, u.username " +
                         "FROM tb_penjualan p " +
                         "LEFT JOIN tb_customer c ON p.id_customer = c.id_customer " +
                         "JOIN tb_user u ON p.id_user = u.id_user " +
                         "WHERE p.no_faktur = ?";
            Connection conn = Koneksi.configDB();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, noFaktur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Object[]{
                    rs.getString("no_faktur"),
                    rs.getDate("tgl_transaksi"),
                    rs.getString("nama_customer"),
                    rs.getDouble("total_bayar"),
                    rs.getString("username")
                };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Object[]> getDetailTransaksi(String noFaktur) {
        List<Object[]> list = new ArrayList<>();
        try {
            String sql = "SELECT b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal " +
                         "FROM tb_detail_penjualan d " +
                         "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                         "JOIN tb_penjualan p ON d.id_jual = p.id_jual " +
                         "WHERE p.no_faktur = ? " +
                         "ORDER BY d.id_detail ASC";
            Connection conn = Koneksi.configDB();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, noFaktur);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("nama_barang"),
                    rs.getDouble("harga_satuan"),
                    rs.getInt("jumlah_beli"),
                    rs.getDouble("subtotal")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Object[]> getAllDetailTransaksi() {
        List<Object[]> list = new ArrayList<>();
        try {
            String sql = "SELECT d.id_detail, p.no_faktur, b.nama_barang, d.harga_satuan, d.jumlah_beli, d.subtotal " +
                         "FROM tb_detail_penjualan d " +
                         "JOIN tb_penjualan p ON d.id_jual = p.id_jual " +
                         "JOIN tb_barang b ON d.id_barang = b.id_barang " +
                         "ORDER BY d.id_detail DESC";
            Connection conn = Koneksi.configDB();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id_detail"),
                    rs.getString("no_faktur"),
                    rs.getString("nama_barang"),
                    rs.getDouble("harga_satuan"),
                    rs.getInt("jumlah_beli"),
                    rs.getDouble("subtotal")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
