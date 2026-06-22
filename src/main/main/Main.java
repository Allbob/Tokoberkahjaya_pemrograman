package main.main;

import view.FormLogin;
import javax.swing.SwingUtilities;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Aktifkan FlatMacLightLaf - Tema terang modern paling bagus untuk Java
            FlatMacLightLaf.setup();

            // Kustomisasi agar sudut membulat (Rounded)
            UIManager.put("Button.arc", 20);
            UIManager.put("Component.arc", 20);
            UIManager.put("ProgressBar.arc", 20);
            UIManager.put("TextComponent.arc", 20);
        } catch (Exception e) {
            System.err.println("Gagal memuat desain modern.");
        }

        SwingUtilities.invokeLater(() -> {
            new FormLogin().setVisible(true);
        });
    }
}
