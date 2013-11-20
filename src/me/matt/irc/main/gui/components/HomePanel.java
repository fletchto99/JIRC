package me.matt.irc.main.gui.components;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import me.matt.irc.main.gui.Chrome;
import me.matt.irc.main.util.IRCModifier;
import me.matt.irc.main.util.Methods;
import me.matt.irc.main.util.background.Beeper;

public class HomePanel extends JPanel {

    /**
     * Seiarilazible.
     */
    private static final long serialVersionUID = -1392049773335849422L;

    /**
     * Create an instance of the HomePanel.
     * 
     * @param parent
     *            The parent GUI.
     */
    public HomePanel(final Chrome parent) {
        init(parent);
    }

    /**
     * Initilize the HomePannel
     * 
     * @param parent
     *            The parent to add the homepannel to.
     */
    public void init(final Chrome parent) {
        messageField = new JTextField();
        chatTextPane = new ColoredTextPane();
        chatScrollPane = new AutoScrollPane(chatTextPane);
        setLayout(null);
        messageField.setBounds(5, 480, 705, 30);
        {
            chatScrollPane
                    .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            chatScrollPane.setViewportView(chatTextPane);
        }
        chatScrollPane.setBounds(5, 0, 705, 475);
        chatTextPane.setEditable(false);

        messageField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent e) {
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.isControlDown()) {
                    if (e.getKeyCode() == 75) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                JDialog.setDefaultLookAndFeelDecorated(false);
                                new SimpleColorChooser(new Point(messageField
                                        .getLocationOnScreen().x, messageField
                                        .getLocationOnScreen().y), messageField);
                            }
                        });
                    } else if (e.getKeyCode() == 66) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.BOLD.getModifier());
                    } else if (e.getKeyCode() == 85) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.UNDERLINE.getModifier());
                    } else if (e.getKeyCode() == 73) {
                        messageField.setText(messageField.getText()
                                + IRCModifier.ITALIC.getModifier());
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (messageField.getText().equalsIgnoreCase("")) {
                        return;
                    }
                    Methods.sendRawMessage(messageField.getText());
                    Methods.sendHomeMessage(messageField.getText());
                    messageField.setText("");
                }
            }

            @Override
            public void keyTyped(final KeyEvent e) {
            }
        });

        messageField.setDocument(new PlainDocument() {
            /**
		 *
		 */
            private static final long serialVersionUID = 1L;

            @Override
            public void insertString(final int offs, final String str,
                    final AttributeSet a) throws BadLocationException {
                if ((getLength() + str.length()) <= 512) {
                    super.insertString(offs, str, a);
                } else {
                    Beeper.beep();
                }
            }
        });

        add(messageField, BorderLayout.SOUTH);
        add(chatScrollPane, BorderLayout.NORTH);
    }

    /**
     * The message area.
     * 
     * @return The message area.
     */
    public ColoredTextPane getMessageArea() {
        return chatTextPane;
    }

    // Variables
    private JTextField messageField;
    private AutoScrollPane chatScrollPane;
    private ColoredTextPane chatTextPane;

}
