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

public class PanelLaporan extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private PenjualanDAO dao = new PenjualanDAO();
    private JLabel lblTotal, lblCount, lblAvg;

    public PanelLaporan() {
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JPanel titleGroup = new JPanel();
        titleGroup.setOpaque(false);
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        JLabel title = UITheme.createSectionTitle("Laporan Penjualan");
        JLabel sub = new JLabel("Ringkasan dan riwayat seluruh transaksi toko");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);
        titleGroup.add(title);
        titleGroup.add(Box.createRigidArea(new Dimension(0, 4)));
        titleGroup.add(sub);
        header.add(titleGroup, BorderLayout.WEST);
        JButton btnRefresh = UITheme.createButton("Muat Ulang", UITheme.ACCENT, Color.WHITE);
        btnRefresh.addActionListener(e -> loadData());
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ── STAT CARDS ───────────────────────────────────────────────────────
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setPreferredSize(new Dimension(0, 110));

        // Card 1
        lblCount = new JLabel("0");
        statsRow.add(buildStatCard("TOTAL TRANSAKSI", lblCount, UITheme.ACCENT));
        // Card 2
        lblTotal = new JLabel("Rp 0");
        statsRow.add(buildStatCard("TOTAL PENDAPATAN", lblTotal, UITheme.SUCCESS));
        // Card 3
        lblAvg = new JLabel("Rp 0");
        statsRow.add(buildStatCard("RATA-RATA / TRANSAKSI", lblAvg, UITheme.WARNING));

        add(statsRow, BorderLayout.SOUTH);

        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel topTablePanel = new JPanel(new BorderLayout());
        topTablePanel.setOpaque(false);
        topTablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        JLabel tblTitle = new JLabel("Riwayat Transaksi");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        topTablePanel.add(tblTitle, BorderLayout.WEST);

        JTextField txtSearch = UITheme.createField("Cari transaksi...");
        txtSearch.setPreferredSize(new Dimension(250, 42));
        topTablePanel.add(txtSearch, BorderLayout.EAST);

        tableCard.add(topTablePanel, BorderLayout.NORTH);

        String[] headers = { "No Faktur", "Tanggal", "Pelanggan", "Barang", "Jumlah", "Total", "Kasir" };
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

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int modelRow = table.convertRowIndexToModel(row);
                        String noFaktur = model.getValueAt(modelRow, 0).toString();
                        tampilkanDetailFaktur(noFaktur);
                    }
                }
            }
        });

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

        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildStatCard(String labelText, JLabel valueLabel, Color accent) {
        JPanel card = UITheme.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // top accent bar
        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(accent);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(0, 4));
        card.add(bar, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lbl = UITheme.createLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lbl);
        content.add(Box.createRigidArea(new Dimension(0, 8)));
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        model.setRowCount(0);
        try {
            List<Object[]> list = dao.getLaporan();
            if (list == null || list.isEmpty()) {
                lblCount.setText("0");
                lblTotal.setText("Rp 0");
                lblAvg.setText("Rp 0");
                return;
            }
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            double totalPendapatan = 0;
            java.util.Set<String> uniqueFakturs = new java.util.HashSet<>();
            for (Object[] row : list) {
                double trxTotal = (double) row[5];
                totalPendapatan += trxTotal;
                uniqueFakturs.add((String) row[0]);
                model.addRow(
                        new Object[] { row[0], row[1], row[2], row[3], row[4], "Rp" + nf.format(trxTotal), row[6] });
            }
            int trxCount = uniqueFakturs.size();
            lblCount.setText(String.valueOf(trxCount));
            lblTotal.setText("Rp" + nf.format(totalPendapatan));
            lblAvg.setText("Rp" + nf.format(trxCount > 0 ? (totalPendapatan / trxCount) : 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tampilkanDetailFaktur(String noFaktur) {
        Object[] header = dao.getHeaderTransaksi(noFaktur);
        if (header == null) {
            JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Object[]> details = dao.getDetailTransaksi(noFaktur);

        String fakturStr = header[0].toString();
        java.util.Date tanggal = (java.util.Date) header[1];
        String pelanggan = header[2].toString();
        double totalBayar = (double) header[3];
        String kasir = header[4].toString();

        java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("yyyy-MM-dd", new java.util.Locale("id", "ID"));
        java.text.SimpleDateFormat sdfTime = new java.text.SimpleDateFormat("HH:mm:ss", new java.util.Locale("id", "ID"));
        String tglStr = sdfDate.format(tanggal);
        String jamStr = sdfTime.format(tanggal);

        NumberFormat nf = NumberFormat.getNumberInstance(new java.util.Locale("id", "ID"));

        // Prepare logo URL
        java.net.URL logoUrl = getClass().getResource("/view/logo.png");
        String logoHtml = (logoUrl != null) ? "<img src=\"" + logoUrl.toString() + "\" width=\"64\" height=\"64\">" : "<div style=\"font-size: 32px;\">&#127976;</div>";

        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<body style=\"font-family: monospace, 'Courier New', Courier; font-size: 13px; margin: 0; padding: 15px; background-color: #ffffff; color: #000000;\">");
        
        // Header
        html.append("  <div style=\"text-align: center; margin-bottom: 10px;\">");
        html.append("    <div align=\"center\">").append(logoHtml).append("</div>");
        html.append("    <div style=\"font-size: 18px; font-weight: bold;\">Albar Berkah Jaya</div>");
        html.append("    <div>Jalan Raya Gandul PLN, Depok</div>");
        html.append("    <div>No. Telp 0812345678</div>");
        html.append("    <div>").append(fakturStr).append("</div>");
        html.append("  </div>");
        
        html.append("  <div style=\"border-bottom: 1px dashed #000000; margin: 10px 0;\"></div>");
        
        // Meta Info
        html.append("  <table style=\"width: 100%; font-family: monospace, 'Courier New', Courier; font-size: 13px; border-collapse: collapse;\">");
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left;\">").append(tglStr).append("</td>");
        html.append("      <td style=\"text-align: right;\">").append(kasir).append("</td>");
        html.append("    </tr>");
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left;\">").append(jamStr).append("</td>");
        html.append("      <td style=\"text-align: right;\">").append(pelanggan).append("</td>");
        html.append("    </tr>");
        html.append("  </table>");
        
        html.append("  <div style=\"border-bottom: 1px dashed #000000; margin: 10px 0;\"></div>");
        
        // Items Table
        html.append("  <table style=\"width: 100%; font-family: monospace, 'Courier New', Courier; font-size: 13px; border-collapse: collapse;\">");
        
        int totalQty = 0;
        int itemIndex = 1;
        for (Object[] d : details) {
            String namaBarang = d[0].toString();
            double harga = (double) d[1];
            int qty = (int) d[2];
            double sub = (double) d[3];
            totalQty += qty;
            
            html.append("    <tr><td colspan=\"2\" style=\"font-weight: bold; padding-top: 5px;\">").append(itemIndex).append(". ").append(namaBarang).append("</td></tr>");
            html.append("    <tr>");
            html.append("      <td style=\"padding-left: 15px;\">").append(qty).append(" x ").append(nf.format(harga)).append("</td>");
            html.append("      <td style=\"text-align: right;\">Rp ").append(nf.format(sub)).append("</td>");
            html.append("    </tr>");
            itemIndex++;
        }
        
        html.append("  </table>");
        
        html.append("  <div style=\"border-bottom: 1px dashed #000000; margin: 10px 0;\"></div>");
        
        // Total Section
        html.append("  <div style=\"margin-bottom: 10px;\">Total QTY : ").append(totalQty).append("</div>");
        
        html.append("  <table style=\"width: 100%; font-family: monospace, 'Courier New', Courier; font-size: 13px; border-collapse: collapse;\">");
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left;\">Sub Total</td>");
        html.append("      <td style=\"text-align: right;\">Rp ").append(nf.format(totalBayar)).append("</td>");
        html.append("    </tr>");
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left; font-weight: bold;\">Total</td>");
        html.append("      <td style=\"text-align: right; font-weight: bold;\">Rp ").append(nf.format(totalBayar)).append("</td>");
        html.append("    </tr>");
        html.append("  </table>");
        
        // Footer Message
        html.append("  <div style=\"text-align: center; margin-top: 20px;\">");
        html.append("    Terimakasih Telah Berbelanja<br><br>");
        html.append("    Link Kritik dan Saran:<br>");
        html.append("    albarberkahjaya.com/e-receipt/").append(fakturStr);
        html.append("  </div>");
        
        html.append("</body>");
        html.append("</html>");

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Struk Belanja", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(420, 600);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(html.toString());
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setBorder(null);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(8, 10, 14, 10));
        
        JButton btnClose = UITheme.createButton("Tutup", UITheme.MUTED, Color.WHITE);
        btnClose.setPreferredSize(new Dimension(100, 36));
        btnClose.addActionListener(ev -> dialog.dispose());

        JButton btnPrint = UITheme.createButton("Cetak", UITheme.SUCCESS, Color.WHITE);
        btnPrint.setPreferredSize(new Dimension(100, 36));
        btnPrint.addActionListener(ev -> {
            try {
                editorPane.print(null, null, true, null, null, true);
            } catch (java.awt.print.PrinterException ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal mencetak: " + ex.getMessage() + "\n\nPastikan printer Anda menyala dan terinstal dengan benar.", "Error Printer", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Terjadi kesalahan printer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnSave = UITheme.createButton("Simpan Gambar", UITheme.ACCENT, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(150, 36));
        btnSave.addActionListener(ev -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Simpan Struk");
            fileChooser.setSelectedFile(new java.io.File("Struk_" + fakturStr + ".png"));
            if (fileChooser.showSaveDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                try {
                    java.io.File file = fileChooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new java.io.File(file.getAbsolutePath() + ".png");
                    }
                    
                    java.awt.Dimension size = editorPane.getPreferredSize();
                    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                        size.width, size.height, java.awt.image.BufferedImage.TYPE_INT_RGB
                    );
                    java.awt.Graphics2D g = image.createGraphics();
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, size.width, size.height);
                    editorPane.setSize(size);
                    editorPane.paint(g);
                    g.dispose();
                    
                    javax.imageio.ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(dialog, "Struk berhasil disimpan ke:\n" + file.getAbsolutePath(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnPanel.add(btnSave);
        btnPanel.add(btnPrint);
        btnPanel.add(btnClose);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
