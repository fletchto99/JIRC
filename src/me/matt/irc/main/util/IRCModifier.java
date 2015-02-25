package me.matt.irc.main.util;

import java.awt.Color;

/**
 * An enum containing all of the Effects used within IRC protocall
 *
 * @author Matt
 *
 */
public enum IRCModifier {

    NORMAL("\u000f", Style.NORMAL), BOLD("\u0002", Style.BOLD), UNDERLINE(
            "\u001f", Style.UNDERLINE), ITALIC("\u0016", Style.ITALIC), WHITE(
            "\u000300", "00", Color.WHITE), BLACK("\u000301", "01", Color.BLACK), DARK_BLUE(
            "\u000302", "02", new Color(0, 0, 127)), DARK_GREEN("\u000303",
            "03", Color.GREEN), RED("\u000304", "04", Color.RED), BROWN(
            "\u000305", "05", new Color(139, 69, 19)), PURPLE("\u000306", "06",
            new Color(160, 32, 240)), OLIVE("\u000307", "07", new Color(85,
            107, 47)), YELLOW("\u000308", "08", Color.YELLOW), GREEN(
            "\u000309", "09", Color.GREEN), TEAL("\u000310", "10", new Color(0,
            128, 128)), CYAN("\u000311", "11", Color.CYAN), BLUE("\u000312",
            "12", Color.BLUE), MAGENTA("\u000313", "13", new Color(238, 130,
            238)), DARK_GRAY("\u000314", "14", Color.DARK_GRAY), LIGHT_GRAY(
            "\u000315", "15", Color.LIGHT_GRAY);

    private final Object effect;

    private final String modifier;

    private final String code;

    IRCModifier(final String modifier, final Object effect) {
        this.modifier = modifier;
        this.effect = effect;
        code = "";
    }

    IRCModifier(final String modifier, final String code, final Object effect) {
        this.modifier = modifier;
        this.effect = effect;
        this.code = code;
    }

    /**
     * The 2 digit code representing the effect.
     *
     * @return The code that represents the effect.
     */
    public String getCode() {
        return code;
    }

    /**
     * The effect to apply.
     *
     * @return The effect.
     */
    public Object getEffect() {
        return effect;
    }

    /**
     * Fetch the IRC modifier used within the IRC protocall.
     *
     * @return The modifier to send to the server.
     */
    public String getModifier() {
        return modifier;
    }
}
