package com.parbonetti.gestorefilm;

public final class AppConfiguration {
    private AppConfiguration() {
        throw new AssertionError("La classe di configurazione non può essere inizializzata");
    }

    // ========== COMMAND HISTORY ==========

    public static final int MAX_COMMAND_HISTORY_SIZE = 50;

    // ========== PERSISTENCE ==========

    public static final String AUTO_SAVE_FILENAME = "movies_autosave";

    public static final String DEFAULT_FILENAME = "movies";

    // ========== MOVIE VALIDATION ==========

    public static final int MIN_RATING = 1;

    public static final int MAX_RATING = 5;

    public static final int MIN_YEAR = 1888;

    public static final int MAX_YEAR = java.time.Year.now().getValue() + 5;
}
