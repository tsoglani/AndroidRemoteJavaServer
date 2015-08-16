/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * @author gaitanesnikos
 */
public class Fr extends JFrame {

	private MouseApp mouseApp = new MouseApp();
public static boolean isNotClosing=false;
	public Fr() {

		createUI();
	
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void createUI() {
		getContentPane().removeAll();
		setSize(300, 300);
		setLayout(new FlowLayout());
		JButton bluetooth = new JButton("Bluetooth");
		JButton wlan = new JButton("WLAN-Internet");
		add(bluetooth);
		add(wlan);

		bluetooth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread() {

					@Override
					public void run() {
						try {
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
				mouseApp.internetConnection();
				getContentPane().removeAll();
			
				getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				JButton back = new JButton("Go Back");
				back.addActionListener(goHome);

				add(back);
				URL whatismyip;
				try {
					whatismyip = new URL("http://checkip.amazonaws.com");
					BufferedReader in = new BufferedReader(
							new InputStreamReader(whatismyip.openStream()));
					String ip = in.readLine(); // you get the IP as a String
					final JCheckBox spam = new JCheckBox(
							"Run on Backround ");
					JLabel label= new JLabel("close ONLY when connected and disconnected from your mobile");
					spam.setToolTipText("Can be used for spying in a computer \n the \"spam\" close when exit the application in the mobile phone  ");
					add(spam);
					add(label);
					spam.addItemListener(new ItemListener() {
						
						@Override
						public void itemStateChanged(ItemEvent arg0) {
							if (spam.isSelected()) {
								setDefaultCloseOperation(1);
								System.out.println("works not closing");
								isNotClosing=true;
							} else {
								setDefaultCloseOperation(EXIT_ON_CLOSE);
								isNotClosing=false;
							}
						}
					});
					JTextField exIP = new JTextField("your public ip is " + ip);
					exIP.setEditable(false);
					add(exIP);
					setSize(500, 300);
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
