package mouseapp;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamPicker;

/**
 * Pieter van Os http://www.pietervanos.net
 * 
 * Library author Bartosz Firyn (SarXos)
 */
public class WebcamShot implements Runnable, WebcamListener, WindowListener,
		UncaughtExceptionHandler {

	private Webcam webcam = null;
	private WebcamPanel panel = null;
	private WebcamPicker picker = null;

	@Override
	public void run() {
		try{
		picker = new WebcamPicker();

		webcam = picker.getSelectedWebcam();

		if (webcam == null) {
			System.out.println("No webcams found...");
			
MouseApp.pw.println("NO CAMERA");

			return;
		}

		// webcam.setViewSize(WebcamResolution.CIF.getSize());
		webcam.addWebcamListener(WebcamShot.this);

		panel = new WebcamPanel(webcam, false);
		panel.setFPSDisplayed(true);
		Thread t = new Thread() {

			@Override
			public void run() {
				panel.start();
			}
		};
		t.setDaemon(true);
		t.setUncaughtExceptionHandler(this);
		t.start();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}

	}

	@Override
	public void webcamOpen(WebcamEvent we) {
		System.out.println("webcam open");
	}

	@Override
	public void webcamClosed(WebcamEvent we) {
		System.out.println("webcam closed");
	}

	@Override
	public void webcamDisposed(WebcamEvent we) {
		System.out.println("webcam disposed");
	}

	@Override
	public void webcamImageObtained(WebcamEvent we) {
		// do nothing
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		webcam.close();
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		System.out.println("webcam viewer resumed");
		panel.resume();
	}

	@Override
	public void windowIconified(WindowEvent e) {
		System.out.println("webcam viewer paused");
		panel.pause();
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.err
				.println(String.format("Exception in thread %s", t.getName()));
		e.printStackTrace();
	}

	public void closeCamera() {
		if (panel != null)
			panel.stop();
		if (webcam != null)
			webcam.close();
		panel = null;
		webcam = null;
	}

	public BufferedImage getCameraImage() {
		BufferedImage image = null;
		try {
			Webcam.setAutoOpenMode(true);
			image = Webcam.getDefault().getImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// System.out.println("actionPerformed(ActionEvent e) ");
	// if(e.getSource() == take){
	// System.out.println("knop gedrukt");
	// Webcam.setAutoOpenMode(true);
	// BufferedImage image = Webcam.getDefault().getImage();
	//
	// // save image to JPG file
	// try {
	// ImageIO.write(image, "JPG", new File("profielfoto.jpg"));
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	//
	// }
	// }
	
	
}