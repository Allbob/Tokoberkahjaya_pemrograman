package view;

import dao.UserDAO;
import model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class PanelUser extends JPanel {

    private JTextField     txtNamaLengkap, txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cbLevel;
    private JTextField     txtId;          // hidden – menyimpan id_user baris terpilih
    private JTable         table;
    private DefaultTableModel tableModel;
    private UserDAO        dao = new UserDAO();

    public PanelUser() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Manajemen User");
        JLabel sub = new JLabel("Tambah, ubah, dan hapus akun pengguna sistem");
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

        JLabel formTitle = new JLabel("Detail User");
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

        txtNamaLengkap = addFormField(formCard, "NAMA LENGKAP", "Nama lengkap pengguna");
        txtUsername    = addFormField(formCard, "USERNAME",      "Masukkan username");
        txtPassword    = addPasswordField(formCard, "PASSWORD",  "Masukkan password");

        // Level combo
        JLabel lblLevel = UITheme.createLabel("LEVEL");
        lblLevel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbLevel = UITheme.<String>createCombo();
        cbLevel.addItem("Admin");
        cbLevel.addItem("Petugas");
        cbLevel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbLevel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        formCard.add(lblLevel);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(cbLevel);
        formCard.add(Box.createRigidArea(new Dimension(0, 14)));

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

        JLabel tblTitle = new JLabel("Daftar User");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari user...");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = {"ID", "Nama Lengkap", "Username", "Password", "Level"};
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
                txtNamaLengkap.setText(tableModel.getValueAt(modelRow, 1).toString());
                txtUsername.setText(tableModel.getValueAt(modelRow, 2).toString());
                txtPassword.setText(tableModel.getValueAt(modelRow, 3).toString());
                cbLevel.setSelectedItem(tableModel.getValueAt(modelRow, 4).toString());
            }
        });

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

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

    private JPasswordField addPasswordField(JPanel panel, String labelText, String placeholder) {
        JLabel lbl = UITheme.createLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField field = UITheme.createPasswordField(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 14)));
        return field;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────

    private void loadData() {
        tableModel.setRowCount(0);
        List<User> list = dao.getAll();
        for (User u : list) {
            tableModel.addRow(new Object[]{u.getIdUser(), u.getNamaLengkap(), u.getUsername(), u.getPassword(), u.getLevel()});
        }
    }

    private void save() {
        String namaLengkap = txtNamaLengkap.getText().trim();
        String username    = txtUsername.getText().trim();
        String password    = new String(txtPassword.getPassword()).trim();
        String level       = cbLevel.getSelectedItem().toString();

        if (namaLengkap.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dao.insert(new User(username, password, namaLengkap, level))) {
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "User berhasil ditambahkan!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menambahkan user!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void update() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih user dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        String namaLengkap = txtNamaLengkap.getText().trim();
        String username    = txtUsername.getText().trim();
        String password    = new String(txtPassword.getPassword()).trim();
        String level       = cbLevel.getSelectedItem().toString();
        if (namaLengkap.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE); return;
        }
        if (dao.update(new User(Integer.parseInt(idStr), username, password, namaLengkap, level))) {
            loadData(); clearForm();
            JOptionPane.showMessageDialog(this, "User berhasil diperbarui!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void delete() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih user dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE); return; }
        
        int deleteId = Integer.parseInt(idStr);
        if (deleteId == UserDAO.loggedInUserId) {
            JOptionPane.showMessageDialog(this, "Anda tidak bisa menghapus akun yang sedang Anda gunakan saat ini!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Yakin hapus user ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (dao.delete(deleteId)) {
                loadData(); clearForm();
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user!\nUser ini tidak bisa dihapus karena mungkin memiliki riwayat input transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        txtId.setText(""); txtNamaLengkap.setText(""); txtUsername.setText("");
        txtPassword.setText(""); cbLevel.setSelectedIndex(0); table.clearSelection();
    }
}
