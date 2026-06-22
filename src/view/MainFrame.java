package view;

import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeBtn;
    private JLabel lblClock;
    private Timer clockTimer;
    private JLabel lblPageTitle;

    public MainFrame() {
        setTitle("Albar Berkah Jaya POS");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_APP);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_APP);
        setContentPane(root);

        // ── SIDEBAR ─────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UITheme.BG_SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // right border line
                g2.setColor(UITheme.BORDER_COLOR);
                g2.fillRect(getWidth() - 1, 0, 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setOpaque(false);

        // top: logo
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(28, 20, 28, 20));

        // Load custom logo
        ImageIcon logoImg = null;
        try {
            java.net.URL imgUrl = MainFrame.class.getResource("/view/logo.png");
            if (imgUrl != null) {
                logoImg = new ImageIcon(imgUrl);
            } else {
                java.io.File file = new java.io.File("src/view/logo.png");
                if (file.exists()) {
                    logoImg = new ImageIcon(file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load logo: " + e.getMessage());
        }

        final ImageIcon finalLogo = logoImg;
        JPanel logoIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                if (finalLogo != null && finalLogo.getImage() != null) {
                    Image img = finalLogo.getImage();
                    int w = img.getWidth(this);
                    int h = img.getHeight(this);
                    
                    if (w > 0 && h > 0) {
                        int size = Math.min(w, h);
                        int sx1 = (w - size) / 2;
                        int sy1 = (h - size) / 2;
                        int sx2 = sx1 + size;
                        int sy2 = sy1 + size;
                        
                        // Clip to circle shape to prevent showing any square background
                        java.awt.geom.Ellipse2D.Double circle = new java.awt.geom.Ellipse2D.Double(0, 0, 40, 40);
                        g2.setClip(circle);
                        
                        g2.drawImage(img, 0, 0, 40, 40, sx1, sy1, sx2, sy2, this);
                    } else {
                        // Fallback draw if width/height are not loaded yet (should not happen with ImageIcon)
                        java.awt.geom.Ellipse2D.Double circle = new java.awt.geom.Ellipse2D.Double(0, 0, 40, 40);
                        g2.setClip(circle);
                        g2.drawImage(img, 0, 0, 40, 40, this);
                    }
                } else {
                    // Fallback to classic rounded rect
                    g2.setColor(UITheme.ACCENT);
                    g2.fillRoundRect(0, 0, 40, 40, 10, 10);
                }
                g2.dispose();
            }
        };
        logoIcon.setOpaque(false);
        logoIcon.setPreferredSize(new Dimension(40, 40));
        logoIcon.setLayout(new GridBagLayout());
        
        if (finalLogo == null || finalLogo.getImage() == null) {
            JLabel iconLbl = new JLabel("BJ");
            iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            iconLbl.setForeground(Color.WHITE);
            logoIcon.add(iconLbl);
        }

        JPanel logoText = new JPanel();
        logoText.setOpaque(false);
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        JLabel lblLogoMain = new JLabel("Albar Berkah Jaya");
        lblLogoMain.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblLogoMain.setForeground(UITheme.TEXT_PRIMARY);
        JLabel lblLogoSub = new JLabel("Transaksi");
        lblLogoSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLogoSub.setForeground(UITheme.TEXT_MUTED);
        logoText.add(lblLogoMain);
        logoText.add(lblLogoSub);

        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        sidebar.add(logoPanel, BorderLayout.NORTH);

        // nav items
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel navSection = new JLabel("   MENU UTAMA");
        navSection.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navSection.setForeground(UITheme.TEXT_MUTED);
        navSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        nav.add(navSection);
        nav.add(Box.createRigidArea(new Dimension(0, 10)));

        boolean isAdmin = "Admin".equalsIgnoreCase(UserDAO.loggedInUserLevel);

        JButton btnTransaksi = createNavBtn("[T]", "Transaksi", "Transaksi", nav);
        
        JButton btnBarang = null;
        if (isAdmin) {
            btnBarang = createNavBtn("[B]", "Stok Barang", "Barang", nav);
        }
        
        JButton btnKategori = null;
        if (isAdmin) {
            btnKategori = createNavBtn("[K]", "Kategori Barang", "Kategori", nav);
        }
        
        JButton btnCustomer = createNavBtn("[P]", "Pelanggan", "Customer", nav);

        JButton btnUser = null;
        if (isAdmin) {
            btnUser = createNavBtn("[U]", "Manajemen User", "User", nav);
        }

        sidebar.add(nav, BorderLayout.CENTER);

        // bottom: user info + logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 14, 20, 14));

        JButton btnLaporan = null;
        if (isAdmin) {
            btnLaporan = createNavBtn("[L]", "Laporan", "Laporan", bottomPanel);
            bottomPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        JButton btnDetailPenjualan = createNavBtn("[D]", "Detail Penjualan", "DetailPenjualan", bottomPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Clock
        lblClock = new JLabel();
        lblClock.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblClock.setForeground(UITheme.TEXT_MUTED);
        lblClock.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblClock.setBorder(BorderFactory.createEmptyBorder(0, 10, 8, 0));
        updateClock();
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();

        JSeparator sep = UITheme.createSeparator();
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLogout = createNavBtn("[X]", "Keluar", "LOGOUT", bottomPanel);
        btnLogout.setForeground(UITheme.DANGER);

        bottomPanel.add(lblClock);
        bottomPanel.add(sep);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        bottomPanel.add(btnLogout);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        root.add(sidebar, BorderLayout.WEST);

        // ── TOPBAR ──────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.BG_APP);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(14, 28, 14, 28)));
        lblPageTitle = new JLabel(isAdmin ? "Stok Barang" : "Transaksi");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblPageTitle.setForeground(UITheme.TEXT_PRIMARY);
        topBar.add(lblPageTitle, BorderLayout.WEST);

        JLabel lblUser = new JLabel(UserDAO.loggedInUserFullName + " (" + UserDAO.loggedInUserLevel + ")   ");
        lblUser.setFont(UITheme.FONT_BODY);
        lblUser.setForeground(UITheme.TEXT_SECONDARY);
        topBar.add(lblUser, BorderLayout.EAST);

        // ── CONTENT ─────────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BG_APP);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(new PanelBarang(), "Barang");
        contentPanel.add(new PanelCustomer(), "Customer");
        contentPanel.add(new PanelTransaksi(), "Transaksi");
        contentPanel.add(new PanelLaporan(), "Laporan");
        contentPanel.add(new PanelDetailPenjualan(), "DetailPenjualan");
        contentPanel.add(new PanelKategori(), "Kategori");
        contentPanel.add(new PanelUser(), "User");

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(UITheme.BG_APP);
        mainArea.add(topBar, BorderLayout.NORTH);
        mainArea.add(contentPanel, BorderLayout.CENTER);

        root.add(mainArea, BorderLayout.CENTER);

        // default selection
        if (isAdmin && btnBarang != null) {
            btnBarang.doClick();
        } else {
            btnTransaksi.doClick();
        }
    }

    private JButton createNavBtn(String icon, String label, String card, JPanel parent) {
        JButton btn = new JButton() {
            boolean active = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (active) {
                    // Deep Navy active card background
                    g2.setColor(UITheme.BG_SIDEBAR_ACTIVE);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                } else if (getModel().isRollover()) {
                    // Rollover soft slate/gray background
                    g2.setColor(UITheme.BG_SIDEBAR_HOVER);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                }
                g2.dispose();
                super.paintComponent(g);
            }

            public void setActive(boolean b) {
                active = b;
                putClientProperty("active", b);
                repaint();
            }
        };
        btn.putClientProperty("active", false);
        btn.setText("    " + label);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(UITheme.TEXT_SECONDARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) {
                    btn.setForeground(UITheme.TEXT_PRIMARY);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!Boolean.TRUE.equals(btn.getClientProperty("active"))) {
                    btn.setForeground(UITheme.TEXT_SECONDARY);
                }
            }
        });

        btn.addActionListener(e -> {
            if ("LOGOUT".equals(card)) {
                clockTimer.stop();
                new FormLogin().setVisible(true);
                dispose();
                return;
            }
            // deactivate previous
            if (activeBtn != null) {
                try {
                    activeBtn.getClass().getMethod("setActive", boolean.class).invoke(activeBtn, false);
                } catch (Exception ex) {
                }
                activeBtn.setForeground(UITheme.TEXT_SECONDARY);
                activeBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            }
            // activate this
            try {
                btn.getClass().getMethod("setActive", boolean.class).invoke(btn, true);
            } catch (Exception ex) {
            }
            btn.setForeground(Color.WHITE); // White text on navy active background
            btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
            activeBtn = btn;
            
            if (lblPageTitle != null) {
                lblPageTitle.setText(label);
            }
            
            cardLayout.show(contentPanel, card);
        });

        parent.add(btn);
        parent.add(Box.createRigidArea(new Dimension(0, 4)));
        return btn;
    }

    private void updateClock() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("  EEEE, dd MMM yyyy  |  HH:mm:ss"));
        lblClock.setText(time);
    }
}
