package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * UITheme — Centralized Premium Design System for Berkah Jaya POS
 * Dark Navy + Indigo Accent palette
 */
public class UITheme {

    // ─── Color Palette ────────────────────────────────────────────────────────
    public static final Color BG_APP = new Color(241, 245, 249); // clean light slate background
    public static final Color BG_SIDEBAR = new Color(255, 255, 255); // pure white sidebar
    public static final Color BG_SIDEBAR_HOVER = new Color(241, 245, 249); // soft light slate hover
    public static final Color BG_SIDEBAR_ACTIVE = new Color(15, 23, 42); // Deep Navy Blue active nav
    public static final Color BG_CARD = new Color(255, 255, 255); // white card
    public static final Color BG_INPUT = new Color(255, 255, 255); // white input
    public static final Color BG_TABLE_ROW = new Color(255, 255, 255);
    public static final Color BG_TABLE_ALT = new Color(248, 250, 252);
    public static final Color BG_TABLE_HEADER = new Color(15, 23, 42); // Deep Navy Blue header background
    public static final Color BG_TABLE_SEL = new Color(15, 23, 42, 25); // Soft navy selection

    public static final Color ACCENT = new Color(15, 23, 42); // Deep Navy Blue
    public static final Color ACCENT_HOVER = new Color(51, 65, 85);
    public static final Color SUCCESS = new Color(16, 185, 129); // Modern Emerald Green
    public static final Color DANGER = new Color(239, 68, 68); // Modern Rose Red
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color MUTED = new Color(100, 116, 139);

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42); // slate dark primary text
    public static final Color TEXT_SECONDARY = new Color(71, 85, 105); // slate secondary text
    public static final Color TEXT_MUTED = new Color(148, 163, 184); // slate light muted text
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // clean slate border

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 22);

    // ─── Rounded Button ───────────────────────────────────────────────────────
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(FONT_BTN);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return btn;
    }

    // ─── Styled Text Field ────────────────────────────────────────────────────
    public static JTextField createField(String placeholder) {
        JTextField f = new JTextField();
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    public static JPasswordField createPasswordField(String placeholder) {
        JPasswordField f = new JPasswordField();
        f.setBackground(BG_INPUT);
        f.setForeground(TEXT_PRIMARY);
        f.setCaretColor(TEXT_PRIMARY);
        f.setFont(FONT_BODY);
        f.setOpaque(true);
        f.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(8, BORDER_COLOR),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.putClientProperty("JTextField.placeholderText", placeholder);
        return f;
    }

    // ─── Styled ComboBox ──────────────────────────────────────────────────────
    public static <T> JComboBox<T> createCombo() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setBackground(BG_INPUT);
        cb.setForeground(TEXT_PRIMARY);
        cb.setFont(FONT_BODY);
        cb.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
                super.getListCellRendererComponent(l, v, i, sel, foc);
                setBackground(sel ? ACCENT : BG_INPUT);
                setForeground(TEXT_PRIMARY);
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                return this;
            }
        });
        return cb;
    }

    // ─── Styled Table ─────────────────────────────────────────────────────────
    public static void styleTable(JTable table) {
        table.setBackground(BG_TABLE_ROW);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(44);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(BG_TABLE_SEL);
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        // Alternating rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row,
                    int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(BG_TABLE_SEL);
                    setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? BG_TABLE_ROW : BG_TABLE_ALT);
                    setForeground(TEXT_PRIMARY);
                }
                return this;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row,
                    int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBackground(BG_TABLE_HEADER); // Deep Navy
                setForeground(Color.WHITE); // White text
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });
    }

    // ─── Card Panel ───────────────────────────────────────────────────────────
    public static JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        return card;
    }

    // ─── Section Label ────────────────────────────────────────────────────────
    public static JLabel createSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_SECONDARY);
        return lbl;
    }

    // ─── Scrollpane ───────────────────────────────────────────────────────────
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(BG_TABLE_ROW);
        sp.setBackground(BG_CARD);
        sp.getVerticalScrollBar().setUI(new DarkScrollBarUI());
        return sp;
    }

    // ─── Separator ────────────────────────────────────────────────────────────
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ─── Rounded Border ───────────────────────────────────────────────────────
    public static class RoundedBorder extends AbstractBorder {
        private int radius;
        private Color color;

        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.draw(new RoundRectangle2D.Float(x, y, w - 1, h - 1, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 2, radius + 2, radius + 2, radius + 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets i) {
            i.left = i.right = i.top = i.bottom = radius + 2;
            return i;
        }
    }

    // ─── Dark Scrollbar UI ────────────────────────────────────────────────────
    public static class DarkScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(203, 213, 225);
            trackColor = BG_CARD;
        }

        @Override
        protected JButton createDecreaseButton(int o) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int o) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fill(new RoundRectangle2D.Float(r.x + 2, r.y + 2, r.width - 4, r.height - 4, 8, 8));
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }
    }
}
