package view;

import dao.KategoriDAO;
import model.Kategori;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class PanelKategori extends JPanel {

    private JTextField txtNama;
    private JTextField txtId;       // hidden – menyimpan id baris terpilih
    private JTable table;
    private DefaultTableModel tableModel;
    private KategoriDAO dao = new KategoriDAO();

    public PanelKategori() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Kategori Barang");
        JLabel sub = new JLabel("Kelola kategori untuk pengelompokan produk");
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
        btnRefresh.addActionListener(e -> { loadData(); clearForm(); });
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── LEFT: FORM ───────────────────────────────────────────────────────
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formCard.setPreferredSize(new Dimension(320, 0));

        JLabel formTitle = new JLabel("Detail Kategori");
        formTitle.setFont(UITheme.FONT_HEADING);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Hidden ID
        txtId = new JTextField();
        txtId.setVisible(false);

        txtNama = addFormField(formCard, "NAMA KATEGORI", "Contoh: Sembako");

        formCard.add(Box.createRigidArea(new Dimension(0, 10)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel btnGrid = new JPanel(new GridLayout(2, 2, 10, 10));
        btnGrid.setOpaque(false);
        btnGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JButton btnSimpan = UITheme.createButton("Simpan",  UITheme.SUCCESS, Color.WHITE);
        JButton btnUpdate = UITheme.createButton("Update",  UITheme.ACCENT,  Color.WHITE);
        JButton btnHapus  = UITheme.createButton("Hapus",   UITheme.DANGER,  Color.WHITE);
        JButton btnReset  = UITheme.createButton("Reset",   UITheme.MUTED, Color.WHITE);

        btnSimpan.addActionListener(e -> save());
        btnUpdate.addActionListener(e -> update());
        btnHapus.addActionListener(e -> delete());
        btnReset.addActionListener(e -> clearForm());

        btnGrid.add(btnSimpan);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnHapus);
        btnGrid.add(btnReset);
        formCard.add(btnGrid);

        add(formCard, BorderLayout.WEST);

        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel tblTitle = new JLabel("Daftar Kategori");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari kategori...");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = {"ID", "Nama Kategori"};
        tableModel = new DefaultTableModel(headers, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
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
                txtId.setText(tableModel.getValueAt(modelRow, 0).toString());
                txtNama.setText(tableModel.getValueAt(modelRow, 1).toString());
            }
        });

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        loadData();
    }

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

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            for (Kategori k : dao.getAll()) {
                tableModel.addRow(new Object[]{k.getIdKategori(), k.getNamaKategori()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat data kategori!\nPastikan tabel tb_kategori sudah ada di database.\n\nError: " + e.getMessage(),
                "Error Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void save() {
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dao.insert(new Kategori(nama))) {
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Kategori berhasil disimpan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan kategori!\n" +
                "Kemungkinan penyebab:\n" +
                "1. Tabel tb_kategori belum ada\n" +
                "2. MySQL tidak berjalan\n" +
                "3. Database belum diimport\n\n" +
                "Silakan import file db_toko_berkah_jaya.sql terlebih dahulu.",
                "Error Simpan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih kategori dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        String nama = txtNama.getText().trim();
        if (nama.isEmpty()) { JOptionPane.showMessageDialog(this, "Nama kategori tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        if (dao.update(new Kategori(Integer.parseInt(idStr), nama))) {
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "Kategori berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void delete() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih kategori dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Yakin hapus kategori ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (dao.delete(Integer.parseInt(idStr))) {
                loadData(); clearForm();
                JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus kategori!\nKategori ini tidak bisa dihapus karena sedang digunakan oleh beberapa produk.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNama.setText(""); table.clearSelection();
    }
}
