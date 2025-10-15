package com.parbonetti.gestorefilm.view;

import com.parbonetti.gestorefilm.model.ViewingStatus;

import javax.swing.*;
import java.awt.*;

public class FilterPanel extends JPanel {
    private JTextField searchField;
    private JComboBox<String> genreComboBox;
    private JComboBox<String> statusComboBox;
    private JButton applyFilterButton;
    private JButton clearFilterButton;

    public FilterPanel() {
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        // Campo ricerca
        searchField = new JTextField(20);
        searchField.setToolTipText("Cerca per titolo o regista");

        // ComboBox genere
        genreComboBox = new JComboBox<>(new String[]{
                "Tutti",
                "Azione",
                "Avventura",
                "Animazione",
                "Commedia",
                "Documentario",
                "Drammatico",
                "Fantasy",
                "Horror",
                "Musical",
                "Romantico",
                "Fantascienza",
                "Thriller",
                "Western"
        });
        genreComboBox.setSelectedItem("Tutti");

        // ComboBox stato visione
        statusComboBox = new JComboBox<>(new String[]{
                "Tutti",
                ViewingStatus.DA_VEDERE.getDisplayName(),
                ViewingStatus.IN_VISIONE.getDisplayName(),
                ViewingStatus.VISTO.getDisplayName()
        });
        statusComboBox.setSelectedItem("Tutti");

        // Bottoni
        applyFilterButton = new JButton("Applica Filtri");
        clearFilterButton = new JButton("Pulisci");
    }

    private void layoutComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        setBorder(BorderFactory.createTitledBorder("Ricerca e Filtri"));

        // Label e campo ricerca
        add(new JLabel("Cerca:"));
        add(searchField);

        // Label e ComboBox genere
        add(new JLabel("Genere:"));
        add(genreComboBox);

        // Label e ComboBox stato
        add(new JLabel("Stato:"));
        add(statusComboBox);

        // Bottoni
        add(applyFilterButton);
        add(clearFilterButton);
    }

    public String getSearchText() {
        String text = searchField.getText();
        return text != null ? text.trim() : "";
    }

    public String getSelectedGenre() {
        return (String) genreComboBox.getSelectedItem();
    }

    public String getSelectedStatus() {
        return (String) statusComboBox.getSelectedItem();
    }

    public ViewingStatus getSelectedStatusEnum() {
        String selected = getSelectedStatus();

        if (selected.equals("Tutti")) {
            return null;
        }

        // Converti display name in enum
        for (ViewingStatus status : ViewingStatus.values()) {
            if (status.getDisplayName().equals(selected)) {
                return status;
            }
        }

        return null;
    }

    public JButton getApplyFilterButton() {
        return applyFilterButton;
    }

    public JButton getClearFilterButton() {
        return clearFilterButton;
    }

    public JTextField getSearchField() {
        return searchField;
    }

    public void clearFilters() {
        searchField.setText("");
        genreComboBox.setSelectedItem("Tutti");
        statusComboBox.setSelectedItem("Tutti");
    }

    public void updateGenres(java.util.List<String> genres) {
        String currentSelection = getSelectedGenre();
        genreComboBox.removeAllItems();
        genreComboBox.addItem("Tutti");

        if (genres != null) {
            for (String genre : genres) {
                genreComboBox.addItem(genre);
            }
        }

        if (genres != null && genres.contains(currentSelection)) {
            genreComboBox.setSelectedItem(currentSelection);
        } else {
            genreComboBox.setSelectedItem("Tutti");
        }
    }
}
