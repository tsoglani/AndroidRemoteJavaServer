package mouseapp;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.security.auth.login.Configuration;
import javax.swing.KeyStroke;

/**
 * 
 * @author tsoglani
 */
public class MouseApp {

	final int port = 2000;
	private ServerSocket ss;
	private PrintWriter pw;
	private static Socket s;
	private StreamConnection connection = null;
	private BufferedReader br;
	public static boolean runConnetions = false;
	private StreamConnectionNotifier notifier;
	float size = 0.2f;

	public MouseApp() {

	}

	/**
	 * enables internet connection
	 */
	public void internetConnection() {
		new Thread() {

			@Override
			public void run() {
				try {

					// while (true) {
					System.out.println("waiting ..");
					ss = new ServerSocket(port);
					// ss.bind(new InetSocketAddress("0.0.0.0", port));
					s = ss.accept();

					pw = new PrintWriter(s.getOutputStream(), true);
					pw.println("works");
					System.out.println("Acceppttt");
					new Thread() {

						@Override
						public void run() {

							receiver(s);

						}
					}.start();
				} catch (java.net.BindException ex) {

					ex.printStackTrace();
					System.exit(1);
				} catch (NullPointerException ex) {
					ex.printStackTrace();

				} catch (Exception ex) {
					ex.printStackTrace();

				}
			}

		}.start();
	}

	public ArrayList<String> bluetoothConnection() {
		ArrayList<String> list = null;
		try {
			BluetoothDeviceDiscovery bl = new BluetoothDeviceDiscovery();
			list = bl.getNames();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return list;
	}

	protected void closeAll() {

		try {
			if (br != null) {
				br.close();
				br = null;
			}
			if (pw != null) {
				pw.close();
				pw = null;
			}
			if (s != null) {
				s.close();
				s = null;
			}
			if (ss != null) {
				ss.close();
				ss = null;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			// fr.createUI();

		}
		try {
			if (notifier != null) {
				try {
					notifier.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.close();
				connection = null;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// fr.createUI();

		}
		System.gc();
	}

	public void receiver(Socket s) {
		try {
			try {
				br = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				try {
					while (runConnetions) {

						Thread.sleep(10);
						String line = br.readLine();
						// System.out.println(line);
						processString(line);
					}
				} catch (java.net.SocketException e) {
					if (Fr.isNotClosing) {
						e.printStackTrace();
						// runConnetions=false;
						closeAll();
						runConnetions = false;
						System.exit(1);

					} else {
						e.printStackTrace();
						// runConnetions=false;
						closeAll();
						Thread.sleep(500);
						internetConnection();
					}
				} catch (NullPointerException e) {

					if (Fr.isNotClosing) {
						runConnetions = false;
						e.printStackTrace();
						// runConnetions=false;
						closeAll();
						System.exit(1);

					} else {
						e.printStackTrace();
						// runConnetions=false;
						closeAll();
						Thread.sleep(1000);
						internetConnection();
					}
				}
			} catch (Exception ex) {
				if (Fr.isNotClosing) {
					runConnetions = false;
					ex.printStackTrace();
					// runConnetions=false;
					closeAll();
					System.exit(1);

				} else {
					Thread.sleep(1000);
					internetConnection();
				}
				ex.printStackTrace();
				// runConnetions = false;
				// fr.createUI();
				// ex.printStackTrace();

			}
			Thread.sleep(1000);
			// internetConnection();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			ex.printStackTrace();
			runConnetions = false;
			fr.createUI();
		}

	}

	public void receiver(BufferedReader br) {
		try {
			try {
				this.br = br;
				while (runConnetions) {
					Thread.sleep(10);
					String line = br.readLine();

					// System.out.println(line);
					processString(line);
				}
			} catch (Exception ex) {
				ex.printStackTrace();

			}
			closeAll();
			Thread.sleep(1000);
			try {
				openBT();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	public void processString(String line) throws AWTException {
		Robot robot = new Robot();
		if (line == null) {
			return;
		}
		if (line.startsWith("keyboard:")) {
			String symbol = line.replace("keyboard:", "");
			System.out.println(symbol);
			switch (symbol) {
			case "SPACE":
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);
				break;
			case "BACKSPACE":
				robot.keyPress(KeyEvent.VK_BACK_SPACE);
				robot.keyRelease(KeyEvent.VK_BACK_SPACE);
				break;
			case "ENTER":
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				break;
			case "TAB":
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);

				break;
			case "DOWN":
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);

				break;
			case "UP":
				robot.keyPress(KeyEvent.VK_UP);
				robot.keyRelease(KeyEvent.VK_UP);

				break;
			case "RIGHT":
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);

				break;
			case "LEFT":
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				break;
			case "SHIFT":
				robot.keyPress(KeyEvent.VK_SHIFT);
				robot.keyRelease(KeyEvent.VK_SHIFT);
				break;
				
			case "ALT+F4":
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_ALT);
				break;

			case "DELETE":
				robot.keyPress(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_DELETE);
				break;

			case "PRINT SCREEN":
				robot.keyPress(KeyEvent.VK_PRINTSCREEN);
				robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
				break;
			case "ALT+CTR+DEL":
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.keyRelease(KeyEvent.VK_ALT);

				break;

			default:
				robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(symbol
						.charAt(0)));
				robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(symbol
						.charAt(0)));

			}

		} else if (line.startsWith("keyboard Word:")) {
			String word = line.replace("keyboard Word:", "");
			for (int i = 0; i < word.length(); i++) {
				try {
					robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(word
							.charAt(i)));
					robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(word
							.charAt(i)));
					
				} catch (java.lang.IllegalArgumentException e) {
					KeyStroke ks = KeyStroke.getKeyStroke(word.charAt(i), 0);
					System.out.println(ks.getKeyCode());
				
					robot.keyPress(ks.getKeyCode());
					robot.keyRelease(ks.getKeyCode());
					System.out.println(word.charAt(i));
				}
			}
		} else if (line.equalsIgnoreCase("GLOBAL_IP")) {
			size = 0.0001f;
			System.out.println("size= " + size);
		} else if (line.equalsIgnoreCase("LOCAL_IP")) {
			size = 0.1f;
			System.out.println("size= " + size);
		} else if (line.endsWith("LEFT_CLICK")) {
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} else if (line.endsWith("LEFT_CLICK_UP")) {
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
		} else if (line.endsWith("LEFT_CLICK_DOWN")) {
			robot.mousePress(InputEvent.BUTTON1_MASK);
		} else if (line.equals("RIGHT_CLICK")) {
			robot.mousePress(InputEvent.BUTTON3_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
		} else if (line.endsWith("SCROLL_DOWN")) {
			robot.mouseWheel(1);
		} else if (line.endsWith("SCROLL_UP")) {
			robot.mouseWheel(-1);
		} else if (line.equals("Null")) {
			throw new NullPointerException("Closing stream");
		} else if (line.equalsIgnoreCase("SHUT DOWN")) {
			try {
				shutdown();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (line.equalsIgnoreCase("SLEEP")) {
			try {
				Runtime.getRuntime().exec("cmd /c shutdown -l");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (line.equalsIgnoreCase("RESTART")) {
			try {
				Runtime.getRuntime().exec("shutdown -r");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (line.startsWith("Motion:")) {
			line = line.replace("Motion:", "");
			String[] values = line.split("@@");
			GraphicsDevice gd = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			int width = MouseInfo.getPointerInfo().getLocation().x;
			int height = MouseInfo.getPointerInfo().getLocation().y;
			System.out.println(line);
			int moveHeight = Integer.parseInt(values[1].replace("y:", ""));
			if (moveHeight == 0) {
				moveHeight = Integer.parseInt(values[0].replace("x:", ""));
			}
			robot.mouseMove(
					width + Integer.parseInt(values[2].replace("z:", "")),
					height - moveHeight);

		} else if (line.startsWith("x:")) {

			// z==x
			// x==y
			String[] values = line.split("@@");
			GraphicsDevice gd = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			int width = gd.getDisplayMode().getWidth();
			int height = gd.getDisplayMode().getHeight();
			robot.mouseMove(
					width * Integer.parseInt(values[2].replace("z:", ""))
							/ 5000,
					height - height
							* Integer.parseInt(values[1].replace("y:", ""))
							/ 5000);

		} else if (line.equalsIgnoreCase("SCREENSHOT") && s != null
				&& !s.isClosed()) {

			new Thread() {
				public void run() {
					try {
						OutputStream out = s.getOutputStream();
						writeJPG(getComputerScreenshot(), out, size);
						// ImageIO.write(getComputerScreenshot(), "PNG", out);
						// out.close();
						out.flush();
						
					
					} catch (IOException ex) {
						ex.printStackTrace();
						ex.printStackTrace();
						closeAll();
						internetConnection();
						// runConnetions = false;
						// fr.createUI();
					} catch (Exception ex) {
						ex.printStackTrace();
						ex.printStackTrace();
						closeAll();
						internetConnection();
						// runConnetions = false;
						// fr.createUI();
					} catch (Error ex) {
						ex.printStackTrace();
						ex.printStackTrace();
						closeAll();
						internetConnection();
						// runConnetions = false;
						// fr.createUI();
					}
				}
			}.start();

		}
	}

	public static void shutdown() throws RuntimeException, IOException {
		String shutdownCommand;
		String operatingSystem = System.getProperty("os.name");

		if ("Linux".equals(operatingSystem)
				|| "Mac OS X".equals(operatingSystem)) {
			shutdownCommand = "shutdown -h now";
		} else if ("Windows".equals(operatingSystem)) {
			shutdownCommand = "shutdown.exe -s -t 0";
		} else {
			throw new RuntimeException("Unsupported operating system.");
		}

		Runtime.getRuntime().exec(shutdownCommand);
		System.exit(0);
	}

	public static void writeJPG(BufferedImage bufferedImage,
			OutputStream outputStream, float quality) throws IOException {
		try {
			Iterator<ImageWriter> iterator = ImageIO
					.getImageWritersByFormatName("jpg");
			ImageWriter imageWriter = iterator.next();
			ImageWriteParam imageWriteParam = imageWriter
					.getDefaultWriteParam();
			imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			imageWriteParam.setCompressionQuality(quality);
			ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(
					outputStream);
			imageWriter.setOutput(imageOutputStream);
		
			IIOImage iioimage = new IIOImage(bufferedImage, null, null);
		
			imageWriter.write(null, iioimage, imageWriteParam);
			imageOutputStream.flush();
			outputStream.flush();
		} catch (java.net.SocketException e) {
		}

	}

	public static BufferedImage getComputerScreenshot() {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit()
				.getScreenSize());
		BufferedImage capture = null;
		try {
			capture = new Robot().createScreenCapture(screenRect);
			Graphics2D graphics2D = capture.createGraphics();
			int x = MouseInfo.getPointerInfo().getLocation().x;
			int y = MouseInfo.getPointerInfo().getLocation().y;
			graphics2D.setColor(Color.white);
			graphics2D.fillRect(x, y, 10, 10); // cursor.gif is 16x16 size.
			graphics2D.setColor(Color.black);
			graphics2D.drawRect(x, y, 10, 10);
			
		} catch (AWTException ex) {
			ex.printStackTrace();
		}
		return capture;
	}

	public static Fr fr;

	public static void main(String[] args) {
		fr = new Fr();
	}

	public class BluetoothDeviceDiscovery implements DiscoveryListener {

		// object used for waiting
		private Object lock = new Object();

		// vector containing the devices discovered
		private Vector vecDevices = new Vector();

		// main method of the application
		public BluetoothDeviceDiscovery() {
		}

		public ArrayList<String> getNames() throws IOException {
			ArrayList<String> list = new ArrayList<String>();

			// create an instance of this class
			// display local device address and name
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			// System.out.println("Address: " +
			// localDevice.getBluetoothAddress());
			// System.out.println("Name: " + localDevice.getFriendlyName());

			// find devices
			DiscoveryAgent agent = localDevice.getDiscoveryAgent();

			System.out.println("Starting device inquiry…");
			agent.startInquiry(DiscoveryAgent.GIAC, this);

			try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Device Inquiry Completed. ");

			// print all devices in vecDevices
			int deviceCount = vecDevices.size();

			if (deviceCount <= 0) {
				System.out.println("No Devices Found .");
			} else {
				// print bluetooth device addresses and names in the format [
				// No. address (name) ]
				System.out.println("Bluetooth Devices:"
						+ Integer.toString(deviceCount));
				for (int i = 0; i < deviceCount; i++) {
					RemoteDevice remoteDevice = (RemoteDevice) vecDevices
							.elementAt(i);
					System.out.println((i + 1)
							+ ". “+remoteDevice.getBluetoothAddress()+ ("
							+ remoteDevice.getFriendlyName(false) + ")");

					list.add(remoteDevice.getFriendlyName(false));
				}

			}

			return list;
		}

		public void inquiryCompleted(int discType) {
			synchronized (lock) {
				lock.notify();
			}

			switch (discType) {
			case DiscoveryListener.INQUIRY_COMPLETED:
				System.out.println("INQUIRY_COMPLETED");
				break;

			case DiscoveryListener.INQUIRY_TERMINATED:
				System.out.println("INQUIRY_TERMINATED");
				break;

			case DiscoveryListener.INQUIRY_ERROR:
				System.out.println("INQUIRY_ERROR");
				break;

			default:
				System.out.println("Unknown Response Code");
				break;
			}
		}// end method

		@Override
		public void deviceDiscovered(RemoteDevice btDevice, DeviceClass dc) {
			if (!vecDevices.contains(btDevice)) {
				vecDevices.addElement(btDevice);
			}
		}

		@Override
		public void servicesDiscovered(int arg0, ServiceRecord[] srs) {

			if (srs.length > 0) {
				System.out.println(srs[0].getConnectionURL(
						ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
			}
		}

		@Override
		public void serviceSearchCompleted(int i, int i1) {

		}
	}// end

	void openBT() throws IOException {
		LocalDevice local = null;

		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);

			UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
			notifier = (StreamConnectionNotifier) Connector.open(url);
		} catch (Exception e) {
			e.printStackTrace();
			fr.createUI();
			return;
		}
		// waiting for connection
		while (runConnetions) {
			try {
				System.out.println("waiting for connection...");
				connection = notifier.acceptAndOpen();
				System.out.println("connected " + connection.toString());
				pw = new PrintWriter(connection.openOutputStream(), true);
				pw.println("works");
				new Thread() {

					@Override
					public void run() {

						try {
							br = new BufferedReader(new InputStreamReader(
									connection.openInputStream(), "UTF-8"));
							receiver(br);
						} catch (Exception ex) {
							ex.printStackTrace();
						}

					}
				}.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
	}

}// end class

