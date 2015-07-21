/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author gaitanesnikos
 */
public class Fr extends JFrame {

    private MouseApp mouseApp = new MouseApp();

    public Fr() {

        createUI();
        setSize(300, 300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void createUI() {
        getContentPane().removeAll();
        setLayout(new FlowLayout());
        JButton bluetooth = new JButton("Bluetooth");
        JButton internet = new JButton("Internet");
        add(bluetooth);
        add(internet);
        bluetooth.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                ArrayList<String> list = mouseApp.bluetoothConnection();
                //  if (list.size() <= 0) {
                System.out.println("No devices");
               //     return;
                //  }
                getContentPane().removeAll();
                JComboBox combo = new JComboBox(list.toArray());
                JLabel label = new JLabel("Bluetooth Devices :");
                add(label);
                add(combo);
                JButton connect = new JButton("Activate Server ");
                add(connect);
                connect.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            mouseApp.openBT();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                revalidate();
                repaint();
            }
        });
        internet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mouseApp.internetConnection();
                getContentPane().removeAll();
                revalidate();
                repaint();
            }
        });
    }

//    @Override
//    public void paint(Graphics g) {
//        super.paint(g); //To change body of generated methods, choose Tools | Templates.
//Graphics2D g2d=(Graphics2D) g;
//BufferedImage bf=getComputerScreenshot();
//g2d.scale((double)getWidth()/bf.getWidth(),(double)getHeight()/bf.getHeight());
//g2d.drawImage(getComputerScreenshot(), null, 0, 0);
//    }

   

}
