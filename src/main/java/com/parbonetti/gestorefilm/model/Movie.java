package com.parbonetti.gestorefilm.model;

import com.parbonetti.gestorefilm.AppConfiguration;

import java.util.UUID;

public class Movie {
    private final String id;
    private String titolo;
    private String regista;
    private int annoUscita;
    private String genere;
    private int valutazione; // 1-5 stelle
    private ViewingStatus statoVisione;

    public Movie(String regista, ViewingStatus statoVisione, int valutazione, String genere, int annoUscita, String titolo) {
        this.id = UUID.randomUUID().toString();
        this.regista = regista;
        this.statoVisione = statoVisione;
        this.valutazione = valutazione;
        this.genere = genere;
        this.annoUscita = annoUscita;
        this.titolo = titolo;
        validate();
    }

    public String getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getRegista() {
        return regista;
    }

    public void setRegista(String regista) {
        this.regista = regista;
    }

    public int getAnnoUscita() {
        return annoUscita;
    }

    public void setAnnoUscita(int annoUscita) {
        this.annoUscita = annoUscita;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public int getValutazione() {
        return valutazione;
    }

    public void setValutazione(int valutazione) {
        this.valutazione = valutazione;
    }

    public ViewingStatus getStatoVisione() {
        return statoVisione;
    }

    public void setStatoVisione(ViewingStatus statoVisione) {
        this.statoVisione = statoVisione;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", titolo='" + titolo + '\'' +
                ", regista='" + regista + '\'' +
                ", annoUscita=" + annoUscita +
                ", genere='" + genere + '\'' +
                ", valutazione=" + valutazione +
                ", statoVisione=" + statoVisione +
                '}';
    }

    private void validate() {
        if (titolo == null || titolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il titolo non può essere vuoto");
        }
        if (regista == null || regista.trim().isEmpty()) {
            throw new IllegalArgumentException("Il regista non può essere vuoto");
        }
        if (annoUscita < AppConfiguration.MIN_YEAR || annoUscita > AppConfiguration.MAX_YEAR) {
            throw new IllegalArgumentException("Anno non valido");
        }
        if (valutazione < AppConfiguration.MIN_RATING || valutazione > AppConfiguration.MAX_RATING) {
            throw new IllegalArgumentException("La valutazione deve essere tra 1 e 5");
        }
        if (statoVisione == null) {
            throw new IllegalArgumentException("Lo stato visione non può essere null");
        }
    }

    public Movie() {
        this.id = UUID.randomUUID().toString();
    }

    public Movie(Movie other) {
        this.id = other.id;
        this.titolo = other.titolo;
        this.regista = other.regista;
        this.annoUscita = other.annoUscita;
        this.genere = other.genere;
        this.valutazione = other.valutazione;
        this.statoVisione = other.statoVisione;
    }

    public static class Memento {
        private final String id;
        private final String titolo;
        private final String regista;
        private final int annoUscita;
        private final String genere;
        private final int valutazione;
        private final ViewingStatus statoVisione;

        private Memento(Movie movie) {
            this.id = movie.id;
            this.titolo = movie.titolo;
            this.regista = movie.regista;
            this.annoUscita = movie.annoUscita;
            this.genere = movie.genere;
            this.valutazione = movie.valutazione;
            this.statoVisione = movie.statoVisione;
        }
    }

    public Memento createMemento() {
        return new Memento(this);
    }

    public void restoreFromMemento(Memento memento) {
        if (memento == null) {
            throw new IllegalArgumentException("Memento non può essere null");
        }

        if (!this.id.equals(memento.id)) {
            throw new IllegalArgumentException(
                    "Non posso ripristinare il memento: ID diversi (film: " + this.id +
                            ", memento: " + memento.id + ")"
            );
        }

        this.titolo = memento.titolo;
        this.regista = memento.regista;
        this.annoUscita = memento.annoUscita;
        this.genere = memento.genere;
        this.valutazione = memento.valutazione;
        this.statoVisione = memento.statoVisione;
    }

}
