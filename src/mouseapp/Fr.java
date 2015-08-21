/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author gaitanesnikos
 */
public class Fr extends JFrame {

	private MouseApp mouseApp = new MouseApp();
	public static boolean isNotClosing = false;
	static ArrayList<String> LOCAL_ADRESSES = new ArrayList<String>();
	static String EXTERNAL_IP = "";
	public static JTextField userName;
	private KindOfDatabase kod = new KindOfDatabase();

	public Fr() {

		createUI();

		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void createUI() {
		getContentPane().removeAll();
		setSize(300, 200);

		JPanel Jpanel = new JPanel();
		JButton bluetooth = new JButton("Bluetooth");
		JButton wlan = new JButton("WLAN-Internet");
		JButton inf0 = new JButton("Info");

		JPanel usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.Y_AXIS));

		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		Jpanel.add(bluetooth);
		Jpanel.add(wlan);
		add(Jpanel);
		add(usernamePanel);

		JPanel lastLinePanel = new JPanel();
		JLabel lab = new JLabel("Directed By Tsoglani");
		lab.setForeground(Color.BLUE);
		lastLinePanel.add(lab);
		lastLinePanel.add(inf0);
		add(lastLinePanel, BorderLayout.AFTER_LAST_LINE);
		inf0.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane
						.showMessageDialog(
								Fr.this,
								"  This application makes possible to control your computer's Device from your mobile phone, you are able to move your mouse from your android device.\n"
										+ "works via Wifi (Wlan-Hotspot) or Bluetooth, you have to choose the type of connection you want to use(Wlan, bluetooth)."
										+ " -Parameters for Wlan connection :\n"
										+ "1) The computer must share a local network.\n"
										+ "2) The android device must be connected to the computer's network.\n"
										+ "\n"
										+

										"-Parameters for Bluetooth connection :\n"
										+ "1) You must have already done the connection (You must have the computer on my devices before use it).\n"
										+ "2) Much more slower than network connection and needs time to connect (so, not recommended).\n"
										+

										" -Parameters for Internet connection (from another network):\n"
										+ "1) You have to change your router's preferences  .\n"
										+ "GOTO : Router(might need to press this 192.168.1.1 on your Browser )->Nat ->Virtual Server -> Lan ip Address = your pc local address->Lan port=2000, public port=2000 "
										+ "\n2) Example -> https://raw.githubusercontent.com/tsoglani/AndroidRemoteJavaServer/master/Internet%20Image%20Example/Screenshot%202.png "
										+ "\n"
										+ "\n"
										+ "\n\n Directed By Tsoglani");
			}
		});
		bluetooth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread() {

					@Override
					public void run() {
						try {
							MouseApp.size = 0.000001f;
							MouseApp.runConnetions = true;
							getContentPane().removeAll();
							JButton back = new JButton("Go Back");
							back.addActionListener(goHome);
							add(back);
							revalidate();
							repaint();
							mouseApp.openBT();
							revalidate();
							repaint();
						} catch (IOException ex) {
							ex.printStackTrace();
						}

					}

				}.start();

				revalidate();

				repaint();
			}
		});

		wlan.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				MouseApp.runConnetions = true;
				setLayout(new FlowLayout());
				mouseApp.internetConnection();
				getContentPane().removeAll();

				JPanel panel = new JPanel();
				add(panel);
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

				JButton back = new JButton("Go Back");

				back.addActionListener(goHome);
				userName = new JTextField();
				userName.setColumns(10);
				// String textName;
				// try {
				// textName = (kod.getUserName() == null) ? "" :
				// kod.getUserName();
				// userName.setText(textName);
				// } catch (FileNotFoundException e2) {
				// // TODO Auto-generated catch block
				// userName.setText("");
				// e2.printStackTrace();
				// }

				userName.setText(System.getProperty("user.name"));
				JPanel usernamePanel = new JPanel();

				JLabel lab = new JLabel("Computer's Name");
				lab.setForeground(Color.red);

				usernamePanel.add(lab);
				usernamePanel.setBackground(Color.darkGray);
				usernamePanel.add(userName);
				panel.add(back);
				add(usernamePanel);
				URL whatismyip;
				try {
					whatismyip = new URL("http://checkip.amazonaws.com");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(whatismyip.openStream()));
					EXTERNAL_IP = in.readLine(); // you get the IP as a String
					final JCheckBox spam = new JCheckBox("Run on Backround ");
					JLabel label = new JLabel(
							"close ONLY when connected and disconnected from your mobile");
					spam.setToolTipText("Can be used for spying in a computer \n the \"spam\" close when exit the application in the mobile phone  ");
					panel.add(spam);
					panel.add(label);
					spam.addItemListener(new ItemListener() {

						@Override
						public void itemStateChanged(ItemEvent arg0) {
							if (spam.isSelected()) {
								setDefaultCloseOperation(1);
								System.out.println("works not closing");
								isNotClosing = true;
							} else {
								setDefaultCloseOperation(EXIT_ON_CLOSE);
								isNotClosing = false;
							}
						}
					});
					JTextArea exIP = new JTextArea();

					exIP.setText("----------Information for INTERNET connection (with public/External Ip, NOT WLAN) ----- ");
					exIP.append("\n1)Your public ip is " + EXTERNAL_IP
							+ " (need to parse it in your android Device)"
							+ "\n2)Your local Ip might be one of theese: ");

					Enumeration<NetworkInterface> n = NetworkInterface
							.getNetworkInterfaces();
					int counter = 0;
					LOCAL_ADRESSES.removeAll(LOCAL_ADRESSES);
					for (; n.hasMoreElements();) {
						NetworkInterface ee = n.nextElement();

						Enumeration<InetAddress> a = ee.getInetAddresses();

						for (; a.hasMoreElements();) {
							InetAddress addr = a.nextElement();
							if (addr.isSiteLocalAddress()) {
								String s = " - ";
								if (counter++ == 0) {
									s = " ";
								}
								exIP.append(s + addr.getHostAddress());
								LOCAL_ADRESSES.add(addr.getHostAddress());
							}

						}
					}

					exIP.setEditable(false);
					exIP.append("\n if there is not one of them , run \"ipconfig\" (Windows) or \"ifconfig\"(Linux) on command line to see local IP address \n(will use it for port forwarding). \n\n 3) Edit your router's preferences (port forwarding)  "
							+ "example -> \nhttps://raw.githubusercontent.com/tsoglani/AndroidRemoteJavaServer/master/Internet%20Image%20Example/Screenshot%202.png ");
					add(exIP);
					setSize(900, 300);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				revalidate();
				repaint();
			}
		});
		revalidate();

		repaint();
	}

	ActionListener goHome = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			MouseApp.runConnetions = false;
			try {
				mouseApp.closeAll();
			} catch (Exception ex) {

			}
			createUI();
		}
	};

	public KindOfDatabase getKod() {
		return kod;
	}
	// @Override
	// public void paint(Graphics g) {
	// super.paint(g); //To change body of generated methods, choose Tools |
	// Templates.
	// Graphics2D g2d=(Graphics2D) g;
	// BufferedImage bf=getComputerScreenshot();
	// g2d.scale((double)getWidth()/bf.getWidth(),(double)getHeight()/bf.getHeight());
	// g2d.drawImage(getComputerScreenshot(), null, 0, 0);
	// }
}
