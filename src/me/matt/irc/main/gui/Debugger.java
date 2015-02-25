package me.matt.irc.main.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.ScrollPaneConstants;

import me.matt.irc.main.Configuration;
import me.matt.irc.main.gui.components.AutoScrollPane;
import me.matt.irc.main.gui.components.ColoredTextPane;
import me.matt.irc.main.util.ImageUtil;

public class Debugger extends JFrame {

    private static final long serialVersionUID = -7273108132881476625L;

    private AutoScrollPane debugAS;

    private ColoredTextPane debugTextArea;

    /**
     * Create a debugger that we can print information on.
     */
    public Debugger() {
        this.init();
    }

    /**
     * Fetch the debug area.
     *
     * @return The debug area.
     */
    public ColoredTextPane getDebugArea() {
        return debugTextArea;
    }

    /**
     * Init the debugger.
     */
    private void init() {
        debugTextArea = new ColoredTextPane();
        debugAS = new AutoScrollPane(debugTextArea);

        this.setResizable(false);
        this.setAlwaysOnTop(true);
        this.setTitle("Debugger");

        this.setIconImage(ImageUtil
                .getImage(Configuration.Paths.Resources.ICON_BUG));

        debugTextArea.setEditable(false);
        debugAS.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        debugAS.setViewportView(debugTextArea);
        this.add(debugAS, BorderLayout.CENTER);
        debugAS.setBounds(0, 0, 400, 400);

        this.setPreferredSize(new Dimension(400, 400));
        this.setSize(400, 400);
    }
}
