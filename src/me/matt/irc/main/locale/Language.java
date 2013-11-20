package me.matt.irc.main.locale;

/**
 * An enum containing all compatible languages within the program.
 * 
 * @author matthewlanglois
 * 
 */
public enum Language {
    ENGLISH("en"), FRENCH("fr"), SPANISH("sp");

    /**
     * Create a language.
     * 
     * @param name
     *            The name of the language.
     */
    Language(final String name) {
        this.name = name;
    }

    /**
     * Fetch the name of the language.
     * 
     * @return The name of the language.
     */
    public String getName() {
        return name;
    }

    private String name;
}
