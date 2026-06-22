package view;

import dao.UserDAO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class FormLogin extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private UserDAO userDAO = new UserDAO();
    private JButton btnLogin;

    public FormLogin() {
        setTitle("Albar Berkah Jaya Login");
        setSize(900, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);

        JPanel root = new JPanel(new BorderLayout());
        setContentPane(root);

        // ── LEFT HERO PANEL (Premium dark navy with glows) ───────────────────
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Deep navy to dark indigo gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 23, 42),
                        0, getHeight(), new Color(30, 27, 75));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Glowing geometric glassmorphic rings
                g2.setColor(new Color(255, 255, 255, 6));
                g2.setStroke(new BasicStroke(40));
                g2.drawOval(-100, -100, 400, 400);
                
                g2.setColor(new Color(99, 102, 241, 15)); // Indigo glow
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(-40, -40, 280, 280);
                
                g2.setColor(new Color(255, 255, 255, 4));
                g2.fillOval(50, getHeight() - 250, 350, 350);

                g2.dispose();
            }
        };
        hero.setPreferredSize(new Dimension(420, 0));
        hero.setLayout(new GridBagLayout());

        JPanel heroContent = new JPanel();
        heroContent.setOpaque(false);
        heroContent.setLayout(new BoxLayout(heroContent, BoxLayout.Y_AXIS));
        heroContent.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        // Tiny top category
        JLabel lblSystem = new JLabel("POINT OF SALE SYSTEM");
        lblSystem.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSystem.setForeground(new Color(129, 140, 248)); // soft purple-indigo
        lblSystem.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblBrand = new JLabel("Albar Berkah Jaya");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTagline = new JLabel("Sistem Kasir Untuk Bisnis Saya.");
        lblTagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblTagline.setForeground(new Color(148, 163, 184)); // muted slate
        lblTagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Modern Glass Tags/Badges
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsRow.setOpaque(false);
        statsRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsRow.add(createGlassBadge("Stok Realtime"));
        statsRow.add(createGlassBadge("Laporan Mudah"));

        heroContent.add(lblSystem);
        heroContent.add(Box.createRigidArea(new Dimension(0, 8)));
        heroContent.add(lblBrand);
        heroContent.add(Box.createRigidArea(new Dimension(0, 8)));
        heroContent.add(lblTagline);
        heroContent.add(Box.createRigidArea(new Dimension(0, 28)));
        heroContent.add(statsRow);

        hero.add(heroContent);
        root.add(hero, BorderLayout.WEST);

        // ── RIGHT FORM PANEL (Clean crisp white) ─────────────────────────────
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(340, 450));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel lblWelcome = new JLabel("Selamat Datang");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(new Color(15, 23, 42)); // Slate 900
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Masuk ke sistem kasir Anda");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 116, 139)); // Slate 500
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Fields
        JLabel lblUser = UITheme.createLabel("USERNAME");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblUser.setForeground(new Color(71, 85, 105));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtUsername = UITheme.createField("Masukkan username");
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = UITheme.createLabel("PASSWORD");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPass.setForeground(new Color(71, 85, 105));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        txtPassword = UITheme.createPasswordField("Masukkan password");
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin = UITheme.createButton("Masuk Sekarang", new Color(15, 23, 42), Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHint = new JLabel("BJ Kasir v2.0");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint.setForeground(new Color(148, 163, 184));
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble Form
        formPanel.add(lblWelcome);
        formPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        formPanel.add(lblSub);
        formPanel.add(Box.createRigidArea(new Dimension(0, 36)));
        formPanel.add(lblUser);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(txtUsername);
        formPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        formPanel.add(lblPass);
        formPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 28)));
        formPanel.add(btnLogin);
        formPanel.add(Box.createRigidArea(new Dimension(0, 16)));
        formPanel.add(lblHint);

        right.add(formPanel);
        root.add(right, BorderLayout.CENTER);

        // ── Actions ──────────────────────────────────────────────────────────
        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        // hover effect on button
        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(new Color(51, 65, 85)); }
            public void mouseExited(MouseEvent e)  { btnLogin.setBackground(new Color(15, 23, 42)); }
        });
    }

    private JLabel createGlassBadge(String text) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Glassmorphic semi-transparent background
                g2.setColor(new Color(255, 255, 255, 16));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                
                // Thin glass border
                g2.setColor(new Color(255, 255, 255, 30));
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        lbl.setOpaque(false);
        return lbl;
    }

    private void login() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        if (user.isEmpty() || pass.isEmpty()) {
            shakeWindow();
            return;
        }
        btnLogin.setText("Memeriksa...");
        btnLogin.setEnabled(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return userDAO.login(user, pass);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        new MainFrame().setVisible(true);
                        dispose();
                    } else {
                        shakeWindow();
                        JOptionPane.showMessageDialog(FormLogin.this,
                                "Username atau password salah!", "Akses Ditolak",
                                JOptionPane.ERROR_MESSAGE);
                        btnLogin.setText("Masuk Sekarang");
                        btnLogin.setEnabled(true);
                    }
                } catch (Exception ex) {
                    btnLogin.setText("Masuk Sekarang");
                    btnLogin.setEnabled(true);
                    JOptionPane.showMessageDialog(FormLogin.this,
                            "Terjadi kesalahan: " + ex.getMessage() + "\n\nPastikan MySQL sedang berjalan!",
                            "Error Koneksi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void shakeWindow() {
        final int[] moves = { -8, 8, -6, 6, -4, 4, -2, 2, 0 };
        final Point origin = getLocation();
        Timer t = new Timer(30, null);
        final int[] i = { 0 };
        t.addActionListener(e -> {
            if (i[0] < moves.length) {
                setLocation(origin.x + moves[i[0]++], origin.y);
            } else {
                ((Timer) e.getSource()).stop();
                setLocation(origin);
            }
        });
        t.start();
    }
}
