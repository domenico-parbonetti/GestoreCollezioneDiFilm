package com.parbonetti.gestorefilm;

import com.parbonetti.gestorefilm.controller.MovieController;
import com.parbonetti.gestorefilm.view.MainView;

import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Usa SwingUtilities per garantire thread-safety della GUI
        SwingUtilities.invokeLater(() -> {
            try {
                // Imposta Look and Feel nativo del sistema operativo
                setSystemLookAndFeel();

                // Inizializza e avvia l'applicazione
                initializeApplication();

            } catch (Exception e) {
                System.err.println("Errore nell'avvio dell'applicazione: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Errore nell'avvio dell'applicazione:\n" + e.getMessage(),
                        "Errore Fatale",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void setSystemLookAndFeel() {
        try {
            // Usa l'aspetto nativo del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            System.out.println("Look and Feel impostato: " +
                    UIManager.getLookAndFeel().getName());

        } catch (Exception e) {
            // Se fallisce, continua con Look and Feel di default
            System.err.println("Impossibile impostare Look and Feel di sistema: " +
                    e.getMessage());
            System.out.println("Utilizzo Look and Feel di default");
        }
    }

    private static void initializeApplication() {
        System.out.println("=== Avvio Gestione Film ===");
        System.out.println("Inizializzazione componenti MVC...");

        System.out.println("- Creazione View...");
        MainView view = new MainView();

        // 2. Crea il Controller (collega automaticamente View e Model)
        System.out.println("- Creazione Controller...");
        MovieController controller = new MovieController(view);

        // 3. Mostra la finestra principale
        System.out.println("- Visualizzazione GUI...");
        view.display();

        System.out.println("=== Applicazione avviata con successo! ===");
    }
}