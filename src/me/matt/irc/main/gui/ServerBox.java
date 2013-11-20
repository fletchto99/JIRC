package me.matt.irc.main.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import me.matt.irc.Application;
import me.matt.irc.main.Configuration;
import me.matt.irc.main.locale.Messages;
import me.matt.irc.main.util.ImageUtil;

public class ServerBox extends JFrame {

    private static final long serialVersionUID = 2053636315187421222L;

    public ServerBox() {
        initComponents();
    }

    private void identBoxChecked(final ActionEvent e) {
        if (passBox.isEnabled()) {
            passBox.setEnabled(false);
        } else {
            passBox.setEnabled(true);
        }
    }

    private void cancelAction(final ActionEvent e) {
        this.setVisible(false);
        System.exit(0);
    }

    private void button1ActionPerformed(final ActionEvent e) {
        Application.getInstance().setup(userBox.getText(),
                passBox.getPassword().toString(), serverIPBox.getText(),
                identCheck.isSelected());
        dispose();
    }

    /**
     * Set up and add all of the components to the frame.
     */
    private void initComponents() {
        setIconImage(ImageUtil.getImage(Configuration.Paths.Resources.ICON));
        titleLable = new JLabel();
        connectButton = new JButton();
        cancelButton = new JButton();
        serverIPBox = new JTextField();
        ipLabel = new JLabel();
        nameLabel = new JLabel();
        userBox = new JTextField();
        identCheck = new JCheckBox();
        passBox = new JPasswordField();
        passLabel = new JLabel();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                System.exit(1);
            }
        });

        setTitle(Configuration.NAME);
        final Container contentPane = getContentPane();
        contentPane.setLayout(null);
        this.setResizable(false);

        titleLable.setText("JIRC - A Java IRC Client.");
        titleLable.setFont(new Font("Tahoma", Font.PLAIN, 18));
        titleLable.setForeground(Color.red);
        contentPane.add(titleLable);
        titleLable.setBounds(120, 5, 145, 65);

        connectButton.setText(Messages.CONNECT);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                button1ActionPerformed(e);
            }
        });
        contentPane.add(connectButton);
        connectButton.setBounds(220, 200, 105, 45);

        cancelButton.setText(Messages.CANCEL);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                cancelAction(e);
            }
        });
        contentPane.add(cancelButton);
        cancelButton.setBounds(55, 200, 105, 45);
        contentPane.add(serverIPBox);
        serverIPBox.setBounds(140, 65, 175,
                serverIPBox.getPreferredSize().height);

        ipLabel.setText(Messages.IP);
        contentPane.add(ipLabel);
        ipLabel.setBounds(75, 65, ipLabel.getPreferredSize().width, 20);

        nameLabel.setText(Messages.NAME);
        contentPane.add(nameLabel);
        nameLabel.setBounds(70, 100, 65, 20);
        contentPane.add(userBox);
        userBox.setBounds(140, 100, 175, userBox.getPreferredSize().height);

        identCheck.setText(Messages.IDENTIFY);
        identCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                identBoxChecked(e);
            }
        });
        contentPane.add(identCheck);
        identCheck.setBounds(new Rectangle(new Point(165, 130), identCheck
                .getPreferredSize()));

        passBox.setEnabled(false);
        contentPane.add(passBox);
        passBox.setBounds(145, 170, 170, passBox.getPreferredSize().height);

        passLabel.setText(Messages.PASSWORD);
        contentPane.add(passLabel);
        passLabel.setBounds(85, 170, passLabel.getPreferredSize().width, 20);

        serverIPBox.setText("localhost");
        userBox.setText("test");

        contentPane.setPreferredSize(new Dimension(400, 300));
        pack();
        setLocationRelativeTo(getOwner());
    }

    // variables
    private JLabel titleLable;
    private JButton connectButton;
    private JButton cancelButton;
    private JTextField serverIPBox;
    private JLabel ipLabel;
    private JLabel nameLabel;
    private JTextField userBox;
    private JCheckBox identCheck;
    private JPasswordField passBox;
    private JLabel passLabel;
}
