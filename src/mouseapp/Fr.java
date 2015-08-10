/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

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
		JButton wlan = new JButton("WLAN");
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

				JButton back = new JButton("Go Back");
				back.addActionListener(goHome);
				
				add(back);
				URL whatismyip;
				try {
					whatismyip = new URL("http://checkip.amazonaws.com");
			    	BufferedReader in = new BufferedReader(new InputStreamReader( whatismyip.openStream()));
			    	String ip = in.readLine(); //you get the IP as a String
			    JTextField exIP= new JTextField("your public ip is "+ip);
			    exIP.setEditable(false);
			    add(exIP);
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
			mouseApp.closeAll();

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
