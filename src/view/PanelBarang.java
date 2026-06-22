package view;

import dao.KategoriDAO;
import dao.BarangDAO;
import model.Barang;
import model.Kategori;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class PanelBarang extends JPanel {

    private JTextField      txtId, txtNama, txtSatuan, txtHarga, txtStok;
    private JComboBox<String> cbKategori;
    private JTable          table;
    private DefaultTableModel model;
    private BarangDAO       dao         = new BarangDAO();
    private KategoriDAO     kategoriDAO = new KategoriDAO();
    private List<Kategori>  kategoriList;

    public PanelBarang() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Manajemen Stok Barang");
        JLabel sub = new JLabel("Kelola produk dan inventaris toko Anda");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);
        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.add(title);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        titleGroup.add(sub);
        header.add(titleGroup, BorderLayout.WEST);
        JButton btnRefresh = UITheme.createButton("Muat Ulang", UITheme.ACCENT, Color.WHITE);
        btnRefresh.addActionListener(e -> { loadKategori(); loadData(); clear(); });
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── LEFT: FORM ───────────────────────────────────────────────────────
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formCard.setPreferredSize(new Dimension(320, 0));

        JLabel formTitle = new JLabel("Detail Produk");
        formTitle.setFont(UITheme.FONT_HEADING);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        txtId     = addFormField(formCard, "KODE BARANG",    "Contoh: B005");
        txtNama   = addFormField(formCard, "NAMA PRODUK",    "Nama lengkap produk");

        // Kategori dropdown
        JLabel lblKat = UITheme.createLabel("KATEGORI");
        lblKat.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbKategori = UITheme.<String>createCombo();
        cbKategori.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbKategori.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        formCard.add(lblKat);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(cbKategori);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

        txtSatuan = addFormField(formCard, "SATUAN",          "Contoh: pcs, kg, lusin");
        txtHarga  = addFormField(formCard, "HARGA JUAL (Rp)", "Contoh: 15000");
        txtStok   = addFormField(formCard, "STOK TERSEDIA",   "Jumlah stok");

        formCard.add(Box.createRigidArea(new Dimension(0, 4)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        btnGrid.setOpaque(false);
        btnGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JButton btnSimpan = UITheme.createButton("Simpan", UITheme.SUCCESS, Color.WHITE);
        btnSimpan.addActionListener(e -> save());
        JButton btnUpdate = UITheme.createButton("Update", UITheme.ACCENT, Color.WHITE);
        btnUpdate.addActionListener(e -> update());
        JButton btnHapus = UITheme.createButton("Hapus", UITheme.DANGER, Color.WHITE);
        btnHapus.addActionListener(e -> delete());
        JButton btnReset = UITheme.createButton("Reset", UITheme.MUTED, Color.WHITE);
        btnReset.addActionListener(e -> clear());
        btnGrid.add(btnSimpan);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnHapus);
        btnGrid.add(btnReset);
        formCard.add(btnGrid);

        add(formCard, BorderLayout.WEST);

        // ── RIGHT: TABLE ─────────────────────────────────────────────────────
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel tblTitle = new JLabel("Daftar Barang");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari barang (Kode, Nama)...");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = {"Kode", "Nama Barang", "Kategori", "Satuan", "Harga (Rp)", "Stok"};
        model = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
            private void search() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                int modelRow = table.convertRowIndexToModel(row);
                txtId.setText(model.getValueAt(modelRow, 0).toString());
                txtNama.setText(model.getValueAt(modelRow, 1).toString());
                // pilih kategori berdasarkan nama di kolom 2
                String namaKat = model.getValueAt(modelRow, 2).toString();
                for (int i = 0; i < cbKategori.getItemCount(); i++) {
                    if (cbKategori.getItemAt(i).equals(namaKat)) { cbKategori.setSelectedIndex(i); break; }
                }
                txtSatuan.setText(model.getValueAt(modelRow, 3).toString());
                txtHarga.setText(model.getValueAt(modelRow, 4).toString());
                txtStok.setText(model.getValueAt(modelRow, 5).toString());
            }
        });

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        loadKategori();
        loadData();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private JTextField addFormField(JPanel panel, String labelText, String placeholder) {
        JLabel lbl = UITheme.createLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField field = UITheme.createField(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        return field;
    }

    private void loadKategori() {
        kategoriList = kategoriDAO.getAll();
        cbKategori.removeAllItems();
        for (Kategori k : kategoriList) cbKategori.addItem(k.getNamaKategori());
    }

    /** Ambil id_kategori berdasarkan nama yang dipilih di combobox */
    private int getSelectedKategoriId() {
        int idx = cbKategori.getSelectedIndex();
        if (idx >= 0 && kategoriList != null && idx < kategoriList.size())
            return kategoriList.get(idx).getIdKategori();
        return 0;
    }

    private void loadData() {
        model.setRowCount(0);
        List<Barang> list = dao.getAll();
        if (list == null) return;
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        // Buat map id_kategori → nama_kategori
        java.util.Map<Integer, String> katMap = new java.util.HashMap<>();
        if (kategoriList != null)
            for (Kategori k : kategoriList) katMap.put(k.getIdKategori(), k.getNamaKategori());

        for (Barang b : list) {
            String namaKat = katMap.getOrDefault(b.getIdKategori(), String.valueOf(b.getIdKategori()));
            model.addRow(new Object[]{
                b.getIdBarang(), b.getNamaBarang(), namaKat,
                b.getSatuan(), "Rp" + nf.format(b.getHargaJual()), b.getStok()
            });
        }
    }

    private double parseHargaInput() throws Exception {
        String txt = txtHarga.getText().trim();
        // Bersihkan semua karakter selain angka dan koma/titik desimal
        txt = txt.replace("Rp", "").replace(" ", "").replace(".", "").replace(",", ".");
        return Double.parseDouble(txt);
    }

    private void save() {
        try {
            Barang b = new Barang(
                txtId.getText(), getSelectedKategoriId(),
                txtNama.getText(), txtSatuan.getText(),
                parseHargaInput(), Integer.parseInt(txtStok.getText())
            );
            if (dao.insert(b)) { loadData(); clear(); showSuccess("Barang berhasil disimpan!"); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void update() {
        try {
            Barang b = new Barang(
                txtId.getText(), getSelectedKategoriId(),
                txtNama.getText(), txtSatuan.getText(),
                parseHargaInput(), Integer.parseInt(txtStok.getText())
            );
            if (dao.update(b)) { loadData(); clear(); showSuccess("Barang berhasil diperbarui!"); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Input tidak valid!", "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void delete() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih barang dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Yakin hapus barang ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dao.delete(idStr)) {
                loadData(); clear();
                JOptionPane.showMessageDialog(this, "Barang berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus barang!\nBarang ini tidak bisa dihapus karena telah digunakan dalam transaksi penjualan.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clear() {
        txtId.setText(""); txtNama.setText(""); txtSatuan.setText("");
        txtHarga.setText(""); txtStok.setText("");
        if (cbKategori.getItemCount() > 0) cbKategori.setSelectedIndex(0);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Berhasil", JOptionPane.INFORMATION_MESSAGE);
    }
}
