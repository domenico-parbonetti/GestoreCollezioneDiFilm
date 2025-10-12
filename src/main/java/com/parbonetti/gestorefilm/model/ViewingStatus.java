package com.parbonetti.gestorefilm.model;

public enum ViewingStatus {
    DA_VEDERE("Da vedere"),
    IN_VISIONE("In visione"),
    VISTO("Visto");

    private final String displayName;

    ViewingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
