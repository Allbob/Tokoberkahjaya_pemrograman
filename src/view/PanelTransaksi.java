package view;

import dao.BarangDAO;
import dao.CustomerDAO;
import dao.PenjualanDAO;
import dao.UserDAO;
import model.Barang;
import model.Customer;
import model.Penjualan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PanelTransaksi extends JPanel {

    private JComboBox<String> cbCustomer, cbBarang;
    private JTextField txtHarga, txtJumlah;
    private JLabel lblTotalDisplay;
    private List<Customer> customers;
    private List<Barang> items;
    private BarangDAO barangDAO = new BarangDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private PenjualanDAO penjualanDAO = new PenjualanDAO();

    private JTable table;
    private DefaultTableModel model;
    private double totalBelanja = 0;
    private JTextField txtUangBayar;
    private JLabel lblKembalianDisplay;
    private double uangBayar = 0;

    private JButton btnBayar;

    public PanelTransaksi() {
        setLayout(new BorderLayout(16, 16));
        setOpaque(false);

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = UITheme.createSectionTitle("Kasir & Transaksi");
        JLabel sub = new JLabel("Pilih barang, masukkan ke keranjang, lalu bayar");
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
            refreshData();
            txtJumlah.setText("");
            resetKeranjang();
        });
        header.add(btnRefresh, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── LEFT: FORM CARD ────────────────────────────────────────────────
        JPanel formCard = UITheme.createCard();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        formCard.setPreferredSize(new Dimension(340, 0));

        JLabel formTitle = new JLabel("Input Barang");
        formTitle.setFont(UITheme.FONT_HEADING);
        formTitle.setForeground(UITheme.TEXT_PRIMARY);
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(formTitle);
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Pelanggan (Apply to all items in cart)
        cbCustomer = UITheme.<String>createCombo();
        addComboField(formCard, "PILIH PELANGGAN", cbCustomer);

        // Barang
        cbBarang = UITheme.<String>createCombo();
        addComboField(formCard, "PILIH BARANG", cbBarang);

        // Harga Satuan (read-only)
        txtHarga = UITheme.createField("-");
        txtHarga.setEditable(false);
        txtHarga.setBackground(new Color(241, 245, 249));
        txtHarga.setForeground(UITheme.TEXT_SECONDARY);
        txtHarga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtHarga.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHarga = UITheme.createLabel("HARGA SATUAN (Rp)");
        lblHarga.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblHarga);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(txtHarga);
        formCard.add(Box.createRigidArea(new Dimension(0, 16)));

        // Jumlah
        txtJumlah = UITheme.createField("Masukkan jumlah...");
        txtJumlah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblJumlah = UITheme.createLabel("JUMLAH BELI");
        lblJumlah.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(lblJumlah);
        formCard.add(Box.createRigidArea(new Dimension(0, 5)));
        formCard.add(txtJumlah);
        formCard.add(Box.createRigidArea(new Dimension(0, 24)));
        formCard.add(UITheme.createSeparator());
        formCard.add(Box.createRigidArea(new Dimension(0, 20)));

        // Tambah Keranjang Button
        JButton btnTambah = UITheme.createButton("TAMBAH KE KERANJANG", UITheme.SUCCESS, Color.WHITE);
        btnTambah.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTambah.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnTambah.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.add(btnTambah);

        add(formCard, BorderLayout.WEST);

        // ── RIGHT: KERANJANG TABLE ─────────────────────────────────────────
        JPanel rightPanel = new JPanel(new BorderLayout(0, 16));
        rightPanel.setOpaque(false);

        JPanel tableCard = UITheme.createCard();
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel tblTitle = new JLabel("Keranjang Belanja");
        tblTitle.setFont(UITheme.FONT_HEADING);
        tblTitle.setForeground(UITheme.TEXT_PRIMARY);
        tblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        tableCard.add(tblTitle, BorderLayout.NORTH);

        String[] headers = { "ID Barang", "Nama Barang", "Harga", "Jumlah", "Subtotal" };
        model = new DefaultTableModel(headers, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        tableCard.add(UITheme.createScrollPane(table), BorderLayout.CENTER);

        JButton btnHapusRow = UITheme.createButton("Hapus Baris Terpilih", UITheme.DANGER, Color.WHITE);
        btnHapusRow.addActionListener(e -> hapusItemKeranjang());
        JPanel pnlTableAction = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTableAction.setOpaque(false);
        pnlTableAction.add(btnHapusRow);
        tableCard.add(pnlTableAction, BorderLayout.SOUTH);

        rightPanel.add(tableCard, BorderLayout.CENTER);

        // -- BOTTOM RIGHT: TOTAL & PAY --
        JPanel payCard = UITheme.createCard();
        payCard.setLayout(new GridBagLayout());
        payCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Column 0: Total Box
        JPanel totalBox = new JPanel(new BorderLayout());
        totalBox.setOpaque(false);
        JLabel lblTotalTitle = new JLabel("TOTAL PEMBAYARAN");
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTotalTitle.setForeground(UITheme.TEXT_SECONDARY);
        lblTotalDisplay = new JLabel("Rp0");
        lblTotalDisplay.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTotalDisplay.setForeground(UITheme.ACCENT);
        totalBox.add(lblTotalTitle, BorderLayout.NORTH);
        totalBox.add(lblTotalDisplay, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 15);
        payCard.add(totalBox, gbc);

        // Column 1: Uang Bayar
        JPanel bayarBox = new JPanel(new BorderLayout());
        bayarBox.setOpaque(false);
        JLabel lblBayarTitle = new JLabel("UANG BAYAR (Rp)");
        lblBayarTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBayarTitle.setForeground(UITheme.TEXT_SECONDARY);
        txtUangBayar = UITheme.createField("0");
        txtUangBayar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtUangBayar.setPreferredSize(new Dimension(160, 40));
        bayarBox.add(lblBayarTitle, BorderLayout.NORTH);
        bayarBox.add(txtUangBayar, BorderLayout.CENTER);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        gbc.insets = new Insets(0, 0, 0, 15);
        payCard.add(bayarBox, gbc);

        // Column 2: Kembalian
        JPanel kembalianBox = new JPanel(new BorderLayout());
        kembalianBox.setOpaque(false);
        JLabel lblKembalianTitle = new JLabel("KEMBALIAN");
        lblKembalianTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblKembalianTitle.setForeground(UITheme.TEXT_SECONDARY);
        lblKembalianDisplay = new JLabel("Rp0");
        lblKembalianDisplay.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblKembalianDisplay.setForeground(UITheme.SUCCESS);
        kembalianBox.add(lblKembalianTitle, BorderLayout.NORTH);
        kembalianBox.add(lblKembalianDisplay, BorderLayout.CENTER);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 20);
        payCard.add(kembalianBox, gbc);

        // Column 3: Bayar Button
        btnBayar = UITheme.createButton("BAYAR SEKARANG", UITheme.ACCENT, Color.WHITE);
        btnBayar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBayar.setPreferredSize(new Dimension(180, 48));

        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        payCard.add(btnBayar, gbc);

        rightPanel.add(payCard, BorderLayout.SOUTH);

        add(rightPanel, BorderLayout.CENTER);

        // ── Logic ────────────────────────────────────────────────────────────
        cbBarang.addActionListener(e -> updateHarga());
        btnTambah.addActionListener(e -> tambahKeKeranjang());
        btnBayar.addActionListener(e -> prosesPembayaran());

        // Uang Bayar input listener
        txtUangBayar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateKembalian(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateKembalian(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateKembalian(); }
        });

        refreshData();
    }

    private void addComboField(JPanel panel, String labelText, JComboBox<String> combo) {
        JLabel lbl = UITheme.createLabel(labelText);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(combo);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
    }

    private double hargaSatuan = 0;

    private void updateHarga() {
        int idx = cbBarang.getSelectedIndex();
        if (idx >= 0 && items != null && idx < items.size()) {
            hargaSatuan = items.get(idx).getHargaJual();
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            txtHarga.setText("Rp" + nf.format(hargaSatuan));
        }
    }

    private void tambahKeKeranjang() {
        try {
            int itemIdx = cbBarang.getSelectedIndex();
            if (itemIdx < 0)
                return;
            Barang b = items.get(itemIdx);

            int jumlah = Integer.parseInt(txtJumlah.getText().trim());
            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!");
                return;
            }

            // Cek stok apakah cukup (Stok - jumlah yg sudah di keranjang)
            int stokTersedia = b.getStok();
            int jumlahDiKeranjang = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(b.getIdBarang())) {
                    jumlahDiKeranjang += Integer.parseInt(model.getValueAt(i, 3).toString());
                }
            }

            if (jumlah + jumlahDiKeranjang > stokTersedia) {
                JOptionPane.showMessageDialog(this, "Stok tidak mencukupi!\nSisa stok asli: " + stokTersedia
                        + "\nSudah di keranjang: " + jumlahDiKeranjang, "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double subtotal = hargaSatuan * jumlah;
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));

            model.addRow(new Object[] {
                    b.getIdBarang(),
                    b.getNamaBarang(),
                    "Rp" + nf.format(hargaSatuan),
                    jumlah,
                    "Rp" + nf.format(subtotal)
            });

            txtJumlah.setText("");
            kalkulasiTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Periksa kembali input jumlah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusItemKeranjang() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            kalkulasiTotal();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris di keranjang yang ingin dihapus!");
        }
    }

    private void kalkulasiTotal() {
        totalBelanja = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String subStr = model.getValueAt(i, 4).toString().replace("Rp", "").replace(".", "");
            totalBelanja += Double.parseDouble(subStr);
        }
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        lblTotalDisplay.setText("Rp" + nf.format(totalBelanja));
        
        if (cbCustomer != null) {
            cbCustomer.setEnabled(model.getRowCount() == 0);
        }

        // Update kembalian setelah total belanja dihitung ulang
        updateKembalian();
    }

    private void prosesPembayaran() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Keranjang masih kosong!");
            return;
        }

        if (uangBayar < totalBelanja) {
            JOptionPane.showMessageDialog(this, "Uang pembayaran kurang!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int custIdx = cbCustomer.getSelectedIndex();
        String idCustomer = "C000"; // Default
        if (custIdx > 0 && cbCustomer.getSelectedItem() != null) {
            String selectedText = cbCustomer.getSelectedItem().toString();
            String[] parts = selectedText.split(" - ");
            if (parts.length > 0) {
                idCustomer = parts[0].trim();
            }
        }

        Date tglTransaksi = new Date();
        int userId = UserDAO.loggedInUserId;

        List<model.DetailPenjualan> details = new java.util.ArrayList<>();
        double totalBayar = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String idBarang = model.getValueAt(i, 0).toString();
            double harga = Double
                    .parseDouble(model.getValueAt(i, 2).toString().replace("Rp", "").replace(".", "").trim());
            int jumlah = Integer.parseInt(model.getValueAt(i, 3).toString());
            double subtotal = Double
                    .parseDouble(model.getValueAt(i, 4).toString().replace("Rp", "").replace(".", "").trim());

            model.DetailPenjualan d = new model.DetailPenjualan();
            d.setIdBarang(idBarang);
            d.setHargaSatuan(harga);
            d.setJumlahBeli(jumlah);
            d.setSubtotal(subtotal);
            details.add(d);

            totalBayar += subtotal;
        }

        Penjualan p = new Penjualan();
        p.setNoFaktur(penjualanDAO.generateNoFaktur());
        p.setTglTransaksi(tglTransaksi);
        p.setIdCustomer(idCustomer);
        p.setTotalBayar(totalBayar);
        p.setIdUser(userId);

        if (penjualanDAO.simpanTransaksi(p, details)) {
            double kembalian = uangBayar - totalBelanja;
            
            // Tampilkan struk pembayaran langsung secara otomatis
            tampilkanStruk(p.getNoFaktur(), uangBayar, kembalian);
            
            resetKeranjang();
            refreshData();
        } else {
            JOptionPane.showMessageDialog(this, "Terjadi kesalahan saat memproses transaksi!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilkanStruk(String noFaktur, double bayar, double kembalian) {
        Object[] header = penjualanDAO.getHeaderTransaksi(noFaktur);
        if (header == null) {
            JOptionPane.showMessageDialog(this, "Data transaksi tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Object[]> details = penjualanDAO.getDetailTransaksi(noFaktur);

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
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left;\">Bayar (Cash)</td>");
        html.append("      <td style=\"text-align: right;\">Rp ").append(nf.format(bayar)).append("</td>");
        html.append("    </tr>");
        html.append("    <tr>");
        html.append("      <td style=\"text-align: left;\">Kembali</td>");
        html.append("      <td style=\"text-align: right;\">Rp ").append(nf.format(kembalian)).append("</td>");
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
        dialog.setSize(420, 640);
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

    private void resetKeranjang() {
        model.setRowCount(0);
        if (txtUangBayar != null) {
            txtUangBayar.setText("0");
        }
        kalkulasiTotal();
    }

    private void updateKembalian() {
        if (txtUangBayar == null || lblKembalianDisplay == null) return;
        try {
            String text = txtUangBayar.getText().trim();
            if (text.isEmpty()) {
                uangBayar = 0;
            } else {
                text = text.replace(".", "").replace(",", "");
                uangBayar = Double.parseDouble(text);
            }

            double kembalian = uangBayar - totalBelanja;
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("id", "ID"));
            if (kembalian < 0) {
                lblKembalianDisplay.setText("Rp0");
                lblKembalianDisplay.setForeground(UITheme.DANGER);
            } else {
                lblKembalianDisplay.setText("Rp" + nf.format(kembalian));
                lblKembalianDisplay.setForeground(UITheme.SUCCESS);
            }
        } catch (NumberFormatException e) {
            lblKembalianDisplay.setText("Rp0");
            lblKembalianDisplay.setForeground(UITheme.DANGER);
        }
    }

    public void refreshData() {
        try {
            java.sql.Connection conn = config.Koneksi.configDB();
            conn.createStatement().executeUpdate(
                    "INSERT IGNORE INTO tb_customer (id_customer, nama_customer, alamat, telepon) " +
                            "VALUES ('C000', 'Umum (Tanpa Nama)', '-', '-')");
        } catch (Exception ignored) {
        }

        customers = customerDAO.getAll();
        items = barangDAO.getAll();

        cbCustomer.removeAllItems();
        cbCustomer.addItem("- Umum (Tanpa Nama) -");
        if (customers != null)
            for (Customer c : customers) {
                // Jangan tampilkan C000 di daftar bawah karena sudah ada pilihan "- Umum -" di
                // atas
                if (!c.getIdCustomer().equals("C000")) {
                    cbCustomer.addItem(c.getIdCustomer() + " - " + c.getNamaCustomer());
                }
            }

        cbBarang.removeAllItems();
        if (items != null)
            for (Barang b : items)
                cbBarang.addItem(b.getIdBarang() + " - " + b.getNamaBarang() + "  (Stok: " + b.getStok() + ")");

        updateHarga();
    }
}
