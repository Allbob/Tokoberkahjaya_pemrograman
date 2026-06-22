package view;

import dao.CustomerDAO;
import model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;

public class PanelCustomer extends JPanel {

    private JTextField txtId, txtNama, txtAlamat, txtTelepon;
    private JTable table;
    private DefaultTableModel model;
    private CustomerDAO dao = new CustomerDAO();

    public PanelCustomer() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Data Pelanggan");
        JLabel sub = new JLabel("Kelola informasi pelanggan setia toko Anda");
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
        btnRefresh.addActionListener(e -> {
            loadData();
            clear();
        });
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── LEFT: FORM ───────────────────────────────────────────────────────
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formCard.setPreferredSize(new Dimension(320, 0));

        JLabel formTitle = new JLabel("Detail Pelanggan");
        formTitle.setFont(UITheme.FONT_HEADING);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        txtId = addFormField(formCard, "ID PELANGGAN", "Contoh: C003", null);

        // ── Nama Lengkap: hanya huruf ─────────────────────────────────────
        JLabel lblNamaWarn = makeWarnLabel("\u26A0 Nama hanya boleh berisi huruf!");
        txtNama = addFormField(formCard, "NAMA LENGKAP", "Nama pelanggan", lblNamaWarn);
        txtNama.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c) && !Character.isSpaceChar(c)
                        && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // blokir karakter
                    lblNamaWarn.setVisible(true);
                    // sembunyikan otomatis setelah 2 detik
                    new javax.swing.Timer(2000, ev -> {
                        lblNamaWarn.setVisible(false);
                        ((javax.swing.Timer) ev.getSource()).stop();
                    }).start();
                } else {
                    lblNamaWarn.setVisible(false);
                }
            }
        });

        txtAlamat = addFormField(formCard, "ALAMAT", "Alamat lengkap", null);

        // ── No. Telepon: hanya angka ──────────────────────────────────────
        JLabel lblTelpWarn = makeWarnLabel("\u26A0 No. Telepon hanya boleh berisi angka!");
        txtTelepon = addFormField(formCard, "NO. TELEPON", "08xxxxxxxxxx", lblTelpWarn);
        txtTelepon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)
                        && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // blokir karakter
                    lblTelpWarn.setVisible(true);
                    new javax.swing.Timer(2000, ev -> {
                        lblTelpWarn.setVisible(false);
                        ((javax.swing.Timer) ev.getSource()).stop();
                    }).start();
                } else {
                    lblTelpWarn.setVisible(false);
                }
            }
        });

        formCard.add(Box.createRigidArea(new Dimension(0, 24)));
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

        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel tblTitle = new JLabel("Daftar Pelanggan");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari pelanggan...");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = { "ID", "Nama Pelanggan", "Alamat", "No. Telepon" };
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UITheme.styleTable(table);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void changedUpdate(DocumentEvent e) {
                search();
            }

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
                if (row < 0)
                    return;
                int modelRow = table.convertRowIndexToModel(row);
                txtId.setText(model.getValueAt(modelRow, 0).toString());
                txtNama.setText(model.getValueAt(modelRow, 1).toString());
                txtAlamat.setText(model.getValueAt(modelRow, 2).toString());
                txtTelepon.setText(model.getValueAt(modelRow, 3).toString());
            }
        });

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        loadData();
    }

    /** Buat label peringatan merah kecil, tersembunyi by default */
    private JLabel makeWarnLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl.setForeground(UITheme.DANGER);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setVisible(false);
        return lbl;
    }

    private JTextField addFormField(JPanel panel, String labelText, String placeholder, JLabel warnLabel) {
        JLabel lbl = UITheme.createLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField field = UITheme.createField(placeholder);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        if (warnLabel != null) {
            panel.add(Box.createRigidArea(new Dimension(0, 3)));
            panel.add(warnLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 8)));
        } else {
            panel.add(Box.createRigidArea(new Dimension(0, 14)));
        }
        return field;
    }

    private void loadData() {
        model.setRowCount(0);
        List<Customer> list = dao.getAll();
        if (list != null) {
            for (Customer c : list) {
                // Sembunyikan ID sistem C000 agar tidak membingungkan user
                if (!c.getIdCustomer().equals("C000")) {
                    model.addRow(
                            new Object[] { c.getIdCustomer(), c.getNamaCustomer(), c.getAlamat(), c.getNoTelepon() });
                }
            }
        }
    }

    private void save() {
        Customer c = new Customer(txtId.getText(), txtNama.getText(), txtAlamat.getText(), txtTelepon.getText());
        if (dao.insert(c)) {
            loadData();
            clear();
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil disimpan!", "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void update() {
        Customer c = new Customer(txtId.getText(), txtNama.getText(), txtAlamat.getText(), txtTelepon.getText());
        if (dao.update(c)) {
            loadData();
            clear();
            JOptionPane.showMessageDialog(this, "Pelanggan berhasil diperbarui!", "Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void delete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Yakin hapus pelanggan ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            if (dao.delete(id)) {
                loadData();
                clear();
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus pelanggan!\nPelanggan ini tidak bisa dihapus karena memiliki riwayat transaksi.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clear() {
        txtId.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        txtTelepon.setText("");
    }
}
