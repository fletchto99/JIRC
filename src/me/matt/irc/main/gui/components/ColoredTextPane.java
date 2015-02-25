package me.matt.irc.main.gui.components;

import java.awt.Color;
import java.util.LinkedList;

import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import me.matt.irc.main.util.IRCModifier;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.Style;

/**
 * Creates a text pane that can hold color.
 *
 * @author matthewlanglois
 *
 */
public class ColoredTextPane extends JTextPane {

    private static final long serialVersionUID = -5171292233885873672L;

    /**
     * Adds a message to the document.
     *
     * @param message
     *            The message to append.
     */
    public void append(String message) {
        message = IRCModifier.NORMAL.getModifier() + message;
        try {
            final int length = message.length();
            final LinkedList<Object> buffer = new LinkedList<Object>();
            for (int i = 0; i < length; i++) {
                final char ch = message.charAt(i);
                if ((byte) ch == 3) {
                    String color = "";
                    i++;
                    while (i < length) {// loop
                        final char d = message.charAt(i);
                        if ((d >= '0') && (d <= '9')) {
                            if (color.length() == 2) {
                                i--;
                                break;// break before stackoverflow!!!
                            }
                            color += d;
                            i++;
                        } else {
                            i--;
                            break;// break before stackoverflow!!!
                        }
                    }
                    for (final IRCModifier m : IRCModifier.values()) {
                        if (m.getCode().equalsIgnoreCase(color)
                                || m.getCode().replace("0", "")
                                        .equalsIgnoreCase(color)) {
                            buffer.add(m.getEffect());
                            break;
                        }
                    }
                } else if ((byte) ch == 15) {
                    buffer.add(IRCModifier.NORMAL.getEffect());
                } else if ((byte) ch == 2) {
                    buffer.add(IRCModifier.BOLD.getEffect());
                } else if ((byte) ch == 31) {
                    buffer.add(IRCModifier.UNDERLINE.getEffect());
                } else if ((byte) ch == 22) {
                    buffer.add(IRCModifier.ITALIC.getEffect());
                } else {
                    buffer.add(ch);
                }
            }
            final Document doc = this.getDocument();
            final SimpleAttributeSet aset = new SimpleAttributeSet();
            for (final Object obj : buffer) {
                if (obj instanceof Color) {
                    final Object oldColor = aset
                            .getAttribute(StyleConstants.Foreground);
                    if (oldColor != null) {
                        aset.removeAttribute(oldColor);
                    }
                    aset.addAttribute(StyleConstants.Foreground, obj);
                } else if (obj instanceof Style) {
                    if ((obj).equals(IRCModifier.NORMAL.getEffect())) {
                        aset.removeAttributes(aset);
                        aset.addAttribute(StyleConstants.Foreground,
                                IRCModifier.BLACK.getEffect());
                    } else if (obj.equals(IRCModifier.BOLD.getEffect())) {
                        if (aset.containsAttribute(StyleConstants.Bold, true)) {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Bold);
                            aset.removeAttribute(oldColor);
                            aset.addAttribute(StyleConstants.Bold, false);
                        } else {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Bold);
                            if (oldColor != null) {
                                aset.removeAttribute(oldColor);
                            }
                            aset.addAttribute(StyleConstants.Bold, true);
                        }
                    } else if (obj.equals(IRCModifier.ITALIC.getEffect())) {
                        if (aset.containsAttribute(StyleConstants.Italic, true)) {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Italic);
                            aset.removeAttribute(oldColor);
                            aset.addAttribute(StyleConstants.Italic, false);
                        } else {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Italic);
                            if (oldColor != null) {
                                aset.removeAttribute(oldColor);
                            }
                            aset.addAttribute(StyleConstants.Italic, true);
                        }
                    } else if (obj.equals(IRCModifier.UNDERLINE.getEffect())) {
                        if (aset.containsAttribute(StyleConstants.Underline,
                                true)) {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Underline);
                            aset.removeAttribute(oldColor);
                            aset.addAttribute(StyleConstants.Underline, false);
                        } else {
                            final Object oldColor = aset
                                    .getAttribute(StyleConstants.Underline);
                            if (oldColor != null) {
                                aset.removeAttribute(oldColor);
                            }
                            aset.addAttribute(StyleConstants.Underline, true);
                        }
                    }
                } else if (obj instanceof Character) {
                    doc.insertString(doc.getLength(), String.valueOf(obj), aset);
                }
            }
        } catch (final Exception e) {
            Methods.debug(e);
        }
    }
}
