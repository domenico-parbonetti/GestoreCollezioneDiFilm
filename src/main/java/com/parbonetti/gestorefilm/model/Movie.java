package com.parbonetti.gestorefilm.model;

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
        if (annoUscita < 1888 || annoUscita > 2100) {
            throw new IllegalArgumentException("Anno non valido");
        }
        if (valutazione < 1 || valutazione > 5) {
            throw new IllegalArgumentException("La valutazione deve essere tra 1 e 5");
        }
        if (statoVisione == null) {
            throw new IllegalArgumentException("Lo stato visione non può essere null");
        }
    }

    public Movie() {
        this.id = UUID.randomUUID().toString();
    }
}
