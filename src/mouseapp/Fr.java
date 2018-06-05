package mouseapp;

import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;
import java.util.*;

public class Fr extends JFrame
{
    private MouseApp mouseApp;
    public static boolean isNotClosing;
    static ArrayList<String> LOCAL_ADRESSES;
    static String EXTERNAL_IP;
    public static JTextField userName;
    private KindOfDatabase kod;
    JLabel timeLab;
    JTextField portField;
    ActionListener goHome;
    
    static {
        Fr.isNotClosing = false;
        Fr.LOCAL_ADRESSES = new ArrayList<String>();
        Fr.EXTERNAL_IP = "";
    }
    
    public Fr() {
        this.mouseApp = new MouseApp();
        this.kod = new KindOfDatabase();
        this.goHome = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                MouseApp.runConnetions = false;
                try {
                    new Thread() {
                        @Override
                        public void run() {
                            Fr.this.mouseApp.closeAll();
                        }
                    }.start();
                }
                catch (Exception ex) {}
                Fr.this.createUI();
            }
        };
//        this.createUI();

        this.setVisible(true);
        this.setDefaultCloseOperation(3);
                this.setLayout(new BoxLayout(this.getContentPane(), 1));

        wlacConnection();
 this.revalidate();
        this.repaint();
    }
    
    public void createUI() {
        this.getContentPane().removeAll();
        this.setSize(300, 200);
        final JPanel Jpanel = new JPanel();
        final JButton bluetooth = new JButton("Bluetooth");
        final JButton wlan = new JButton("WLAN-Internet");
        final JButton inf0 = new JButton("Info");
        final JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BoxLayout(usernamePanel, 1));
        this.setLayout(new BoxLayout(this.getContentPane(), 1));
        Jpanel.add(bluetooth);
        Jpanel.add(wlan);
        this.add(Jpanel);
        this.add(usernamePanel);
        final JPanel lastLinePanel = new JPanel();
        final JLabel lab = new JLabel("Directed By Tsoglani");
        lab.setForeground(Color.BLUE);
        lastLinePanel.add(lab);
        lastLinePanel.add(inf0);
        this.portField = new JTextField(Integer.toString(MouseApp.port));
        final JPanel portPanel = new JPanel();
        portPanel.add(new JLabel("Port : "));
        portPanel.add(this.portField);
        this.add(portPanel);
        this.add(lastLinePanel, "Last");
        inf0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                JOptionPane.showMessageDialog(Fr.this, "  This application makes possible to control your computer's Device from your mobile phone, you are able to move your mouse from your android device.\nworks via Wifi (Wlan-Hotspot) or Bluetooth, you have to choose the type of connection you want to use(Wlan, bluetooth). -Parameters for Wlan connection :\n1) The computer must share a local network.\n2) The android device must be connected to the computer's network.\n\n-Parameters for Bluetooth connection :\n1) You must have already done the connection (You must have the computer on my devices before use it).\n2) Much more slower than network connection and needs time to connect (so, not recommended).\n -Parameters for Internet connection (from another network):\n1) You have to change your router's preferences  .\nGOTO : Router(might need to press this 192.168.1.1 on your Browser )->Nat ->Virtual Server -> Lan ip Address = your pc local address->Lan port=2000, public port=2000 \n2) Example -> https://raw.githubusercontent.com/tsoglani/AndroidRemoteJavaServer/master/Internet%20Image%20Example/Screenshot%202.png \n\n\n\n Directed By Tsoglani");
            }
        });
        bluetooth.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            MouseApp.qualityPad = 1.0E-6f;
                            MouseApp.runConnetions = true;
                            Fr.this.getContentPane().removeAll();
                            final JButton back = new JButton("Go Back");
                            back.addActionListener(Fr.this.goHome);
                            Fr.this.add(back);
                            Fr.this.revalidate();
                            Fr.this.repaint();
                            try {
                                MouseApp.port = Integer.parseInt(Fr.this.portField.getText().replaceAll(" ", ""));
                            }
                            catch (Exception e) {
                                MouseApp.port = 2000;
                            }
                            Fr.this.mouseApp.openBT();
                            Fr.this.revalidate();
                            Fr.this.repaint();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }.start();
                Fr.this.revalidate();
                Fr.this.repaint();
            }
        });
        wlan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                
           wlacConnection();
            }
        });
        this.revalidate();
        this.repaint();
    }
    
    public void wlacConnection(){
       MouseApp.runConnetions = true;
                try {
                    MouseApp.port = Integer.parseInt(Fr.this.portField.getText().replaceAll(" ", ""));
                }
                catch (Exception ex) {
                    MouseApp.port = 2000;
                }
                Fr.this.mouseApp.internetConnection();
                Fr.this.getContentPane().removeAll();
                final JPanel panel = new JPanel();
                Fr.this.add(panel);
                panel.setLayout(new BoxLayout(panel, 1));
                final JButton back = new JButton("Go Back");
                back.addActionListener(Fr.this.goHome);
                (Fr.userName = new JTextField()).setColumns(10);
                Fr.userName.setText(System.getProperty("user.name"));
                final JPanel usernamePanel = new JPanel();
                final JLabel lab = new JLabel("Computer's Name");
                lab.setForeground(Color.red);
                usernamePanel.add(lab);
                usernamePanel.setBackground(Color.darkGray);
                usernamePanel.add(Fr.userName);
                final JLabel waitingLabel = new JLabel("Waiting ... ");
                waitingLabel.setForeground(Color.green);
                waitingLabel.setBackground(Color.white);
                usernamePanel.add(waitingLabel);
                final JPanel panel2 = new JPanel();
                panel2.add(back);
                panel2.add(usernamePanel);
                panel.add(panel2);
                try {
                    final URL whatismyip = new URL("http://checkip.amazonaws.com");
                    final BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                    Fr.EXTERNAL_IP = in.readLine();
                    final JCheckBox spam = new JCheckBox("Run on Backround (works until you close the connection from mobile device) ");
                    spam.setToolTipText("Can be used for spying in a computer \n the \"spam\" close when exit the application in the mobile phone  ");
                    panel.add(spam);
                    spam.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(final ItemEvent arg0) {
                            if (spam.isSelected()) {
                                Fr.this.setDefaultCloseOperation(1);
                                System.out.println("works not closing");
                                Fr.isNotClosing = true;
                            }
                            else {
                                Fr.this.setDefaultCloseOperation(3);
                                Fr.isNotClosing = false;
                            }
                        }
                    });
                    final JTextArea exIP = new JTextArea();
                    exIP.setText("----------Information for INTERNET connection (with public/External Ip, NOT WLAN) ----- ");
                    exIP.append("\n Port :" + MouseApp.port + "\n1)Your public ip is " + Fr.EXTERNAL_IP + " (need to parse it in your android Device)" + "\n2)Your local Ip might be one of theese: ");
                    final Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                    int counter = 0;
                    Fr.LOCAL_ADRESSES.removeAll(Fr.LOCAL_ADRESSES);
                    while (n.hasMoreElements()) {
                        final NetworkInterface ee = n.nextElement();
                        final Enumeration<InetAddress> a = ee.getInetAddresses();
                        while (a.hasMoreElements()) {
                            final InetAddress addr = a.nextElement();
                            if (addr.isSiteLocalAddress()) {
                                String s = " - ";
                                if (counter++ == 0) {
                                    s = " ";
                                }
                                exIP.append(String.valueOf(s) + addr.getHostAddress());
                                Fr.LOCAL_ADRESSES.add(addr.getHostAddress());
                            }
                        }
                    }
                    exIP.setEditable(false);
                    exIP.append("\n if there is not one of them , run \"ipconfig\" (Windows) or \"ifconfig\"(Linux) on command line to see local IP address \n(will use it for port forwarding). \n\n 3) Edit your router's preferences (port forwarding)  example -> \nhttps://raw.githubusercontent.com/tsoglani/AndroidRemoteJavaServer/master/Internet%20Image%20Example/Screenshot%202.png ");
                    Fr.this.add(exIP);
                    Fr.this.setSize(900, 250);
                }
                catch (Exception ex2) {}
                Fr.this.timeLab = new JLabel("");
                final Button exit = new Button("Exit");
                final JPanel exitPanel = new JPanel();
                exitPanel.add(exit);
                Fr.this.add(Fr.this.timeLab);
                Fr.this.add(exitPanel);
                exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent arg0) {
                        try {
                            MouseApp.pw.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            MouseApp.s.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            System.exit(1);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                   Fr.this.revalidate();
                Fr.this.repaint();
                panel.revalidate();
                panel.repaint();
                panel2.revalidate();
                panel2.repaint();
                usernamePanel.revalidate();
                usernamePanel.repaint();
                        exitPanel.revalidate();
                        exitPanel.repaint();
                     
                getContentPane().revalidate();
                getContentPane().repaint();
            
    }
    
    public KindOfDatabase getKod() {
        return this.kod;
    }
}