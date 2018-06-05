/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Wind extends Window
{
    String message;
    
    public Wind(final Frame owner, final MouseApp mouseApp) {
        super(owner);
        this.message = null;
        final int width = 500;
        final int height = 100;
        this.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width - width, 0, width, height);
        final JButton cancel = new JButton("Cancel");
        final JPanel pan = new JPanel();
        pan.add(cancel);
        this.add(pan, "Last");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                Wind.this.hide();
                MouseApp.fr.timeLab.setText("");
                Wind.this.setVisible(false);
                mouseApp.shutDownThread.suspend();
                mouseApp.shutDownThread = null;
                mouseApp.setW(null);
                System.gc();
            }
        });
        this.setOpacity(0.5f);
        this.setAlwaysOnTop(true);
        this.setFocusable(false);
        this.setBackground(Color.DARK_GRAY);
        this.setVisible(true);
    }
    
    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        if (this.message == null) {
            return;
        }
        final Font font = this.getFont().deriveFont(48.0f);
        g.setFont(font);
        g.setColor(Color.RED);
        final FontMetrics metrics = g.getFontMetrics();
        g.drawString(this.message, 0, this.getHeight() / 2);
    }
}

