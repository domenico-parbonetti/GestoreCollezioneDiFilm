package com.parbonetti.gestorefilm.model;

public enum ViewingStatus {
    DA_VEDERE,
    IN_VISIONE,
    VISTO;

    public String getDisplayName() {
        if (this == VISTO) {
            return "Visto";
        } else if (this == IN_VISIONE) {
            return "In visione";
        }
        return "Da vedere";
    }
}
