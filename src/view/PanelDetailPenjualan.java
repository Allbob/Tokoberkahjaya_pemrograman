package view;

import dao.PenjualanDAO;
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

public class PanelDetailPenjualan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private PenjualanDAO dao = new PenjualanDAO();

    public PanelDetailPenjualan() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Detail Penjualan");
        JLabel sub = new JLabel("Melihat data rincian barang dari seluruh transaksi");
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
        btnRefresh.addActionListener(e -> loadData());
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── TABLE CARD ──────────────────────────────────────────────────────
        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel tblTitle = new JLabel("Daftar Rincian Barang");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari nomor faktur atau barang...");
        txtSearch.setPreferredSize(new Dimension(280, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = {"ID Detail", "No Faktur", "Nama Barang", "Harga Satuan", "Jumlah Beli", "Subtotal"};
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

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        loadData();
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<Object[]> list = dao.getAllDetailTransaksi();
            if (list == null || list.isEmpty()) {
                return;
            }
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            for (Object[] row : list) {
                double hargaSatuan = (double) row[3];
                double subtotal = (double) row[5];
                model.addRow(new Object[]{
                    row[0],
                    row[1],
                    row[2],
                    "Rp" + nf.format(hargaSatuan),
                    row[4],
                    "Rp" + nf.format(subtotal)
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
