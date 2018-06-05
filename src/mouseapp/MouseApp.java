package mouseapp;

import java.awt.event.*;
import javax.swing.filechooser.*;
import java.io.*;
import javax.imageio.metadata.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.microedition.io.*;
import javax.bluetooth.*;

public class MouseApp
{
    private WebcamShot webCam;
    static int port;
    private ServerSocket ss;
    public static PrintWriter pw;
    static Socket s;
    private StreamConnection connection;
    private BufferedReader br;
    public static boolean runConnetions;
    private StreamConnectionNotifier notifier;
    static float qualityPad;
    static float qualitycamera;
    private static boolean isLoggedIn;
    private Audio audioController;
    Mic mic;
    Thread shutDownThread;
    private Wind w;
    boolean isSignedIn;
    private int timeCounter;
    private int seconds;
    private int timeToShutDown;
    private Thread thread;
    private static Robot robot;
    private static int scWidth;
    private static int scHeight;
    private int percentZoom;
    private static boolean showMouseIcon;
    public static Fr fr;
    private static OsCheck.OSType ostype;
    
    static {
        MouseApp.port = 2000;
        MouseApp.runConnetions = false;
        MouseApp.qualityPad = 0.1f;
        MouseApp.qualitycamera = 0.1f;
        MouseApp.isLoggedIn = true;
        MouseApp.scWidth = 1000;
        MouseApp.scHeight = 1000;
        MouseApp.showMouseIcon = false;
    }
    
    public MouseApp() {
        this.connection = null;
        this.isSignedIn = false;
        this.timeCounter = 0;
        this.seconds = 0;
        this.percentZoom = 100;
    }
    
    public void internetConnection() {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("waiting ..");
                    MouseApp.access$0(MouseApp.this, new ServerSocket(MouseApp.port));
                    MouseApp.s = MouseApp.this.ss.accept();
                    MouseApp.pw = new PrintWriter(MouseApp.s.getOutputStream(), true);
                    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                    final int width = (int)screenSize.getWidth();
                    final int height = (int)screenSize.getHeight();
                    final int maxResolution = Math.min(width, height);
                    MouseApp.pw.println("works:maxResolution:" + maxResolution + "@@qualityPad:" + (int)(MouseApp.qualityPad * 100.0f) + "@@qualityCam:" + (int)(MouseApp.qualitycamera * 100.0f));
                    System.out.println("connected " + MouseApp.s.getInetAddress());
                    new Thread() {
                        @Override
                        public void run() {
                            MouseApp.this.receiver(MouseApp.s);
                        }
                    }.start();
                }
                catch (BindException ex) {
                    ex.printStackTrace();
                }
                catch (NullPointerException ex2) {
                    ex2.printStackTrace();
                }
                catch (Exception ex3) {
                    ex3.printStackTrace();
                }
            }
        }.start();
    }
    
    public ArrayList<String> bluetoothConnection() {
        ArrayList<String> list = null;
        try {
            final BluetoothDeviceDiscovery bl = new BluetoothDeviceDiscovery();
            list = bl.getNames();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    protected void closeAll() {
        this.percentZoom = 100;
        this.isSignedIn = false;
        if (this.webCam != null) {
            this.webCam.closeCamera();
        }
        try {
            if (this.br != null) {
                this.br.close();
                this.br = null;
            }
            if (MouseApp.pw != null) {
                MouseApp.pw.close();
                MouseApp.pw = null;
            }
            if (MouseApp.s != null) {
                MouseApp.s.close();
                MouseApp.s = null;
            }
            if (this.ss != null) {
                this.ss.close();
                this.ss = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (this.notifier != null) {
                try {
                    this.notifier.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (this.connection != null) {
                this.connection.close();
                this.connection = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.gc();
    }
    
    public void receiver(final Socket s) {
        try {
            try {
                this.br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                try {
                    while (MouseApp.runConnetions) {
                        final String line = this.br.readLine();
                        this.processString(line);
                    }
                }
                catch (SocketException e) {
                    if (Fr.isNotClosing) {
                        e.printStackTrace();
                        this.closeAll();
                        MouseApp.runConnetions = false;
                        System.exit(1);
                    }
                    else {
                        e.printStackTrace();
                        this.closeAll();
                        Thread.sleep(1000L);
                        this.internetConnection();
                    }
                }
                catch (NullPointerException e2) {
                    if (Fr.isNotClosing) {
                        MouseApp.runConnetions = false;
                        e2.printStackTrace();
                        this.closeAll();
                        System.exit(1);
                    }
                    else {
                        e2.printStackTrace();
                        this.closeAll();
                        Thread.sleep(1000L);
                        this.internetConnection();
                    }
                }
            }
            catch (Exception ex) {
                if (Fr.isNotClosing) {
                    MouseApp.runConnetions = false;
                    ex.printStackTrace();
                    this.closeAll();
                    System.exit(1);
                }
                else {
                    Thread.sleep(1000L);
                    this.internetConnection();
                }
                ex.printStackTrace();
            }
            Thread.sleep(1000L);
        }
        catch (InterruptedException ex2) {
            ex2.printStackTrace();
            ex2.printStackTrace();
            MouseApp.runConnetions = false;
            MouseApp.fr.createUI();
        }
    }
    
    public void receiver(final BufferedReader br) {
        try {
            try {
                this.br = br;
                while (MouseApp.runConnetions) {
                    final String line = br.readLine();
                    this.processString(line);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            this.closeAll();
            Thread.sleep(1000L);
            try {
                this.openBT();
            }
            catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }
        catch (InterruptedException ex3) {
            ex3.printStackTrace();
        }
    }
    
    public void processString(String line) throws AWTException {
        final Robot robot = new Robot();
        System.out.println(line);
        if (line == null || line.replaceAll(" ", "").equals("")) {
            throw new NullPointerException();
        }
      if (line.startsWith("keyboard:")) {
			String symbol = line.replace("keyboard:", "");
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
			case "SHIFT_START":
				robot.keyPress(KeyEvent.VK_SHIFT);
				break;
			case "SHIFT_STOP":
				robot.keyRelease(KeyEvent.VK_SHIFT);
				break;

			case "VOL. ++":
				switch (ostype) {
				case Windows:
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_UP);
					robot.keyRelease(KeyEvent.VK_UP);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					break;

				default:

					try {

						Audio.setMasterOutputVolume(Audio
								.getMasterOutputVolume() + 0.05f);
					} catch (Exception e) {
					}
					break;
				}
				break;
		
				
				
			case "VOL. --":
				
				switch (ostype) {
				case Windows:
					
					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_DOWN);
					robot.keyRelease(KeyEvent.VK_DOWN);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					break;

				default:

					try {

						Audio.setMasterOutputVolume(Audio
								.getMasterOutputVolume() - 0.05f);
					} catch (Exception e) {
					}
					break;
				}
				break;
			case "ALT+F4":
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_ALT);
				break;
			case "ESC":
				robot.keyPress(KeyEvent.VK_ESCAPE);
				robot.keyRelease(KeyEvent.VK_ESCAPE);
				break;
			case "MIN.":
			case "MINIMIZE":

				switch (ostype) {

				case MacOS:
					robot.keyPress(KeyEvent.VK_META);
					robot.keyPress(KeyEvent.VK_M);

					robot.keyRelease(KeyEvent.VK_M);
					robot.keyRelease(KeyEvent.VK_META);
					break;
				case Windows:

					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_SPACE);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_SPACE);

					try {
						Thread.sleep(200);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					robot.keyPress(KeyEvent.VK_N);

					robot.keyRelease(KeyEvent.VK_N);
					break;
				case Linux:

					robot.keyPress(KeyEvent.VK_CONTROL);
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_NUMPAD0);
					robot.keyRelease(KeyEvent.VK_NUMPAD0);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_CONTROL);

					break;
				case Other:

					break;
				}

				break;

			case "DELETE":
				robot.keyPress(KeyEvent.VK_DELETE);
				robot.keyRelease(KeyEvent.VK_DELETE);
				break;

			case "ALT_START":

				robot.keyPress(KeyEvent.VK_ALT);

				break;

			case "CTRL_START":

				robot.keyPress(KeyEvent.VK_CONTROL);

				break;
			case "CTRL_STOP":

				robot.keyRelease(KeyEvent.VK_CONTROL);

				break;
			case "ALT_STOP":
				robot.keyRelease(KeyEvent.VK_ALT);
				break;
			case "CRL+Z":

				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_Z);
				robot.keyRelease(KeyEvent.VK_Z);
				robot.keyRelease(KeyEvent.VK_CONTROL);

				break;
			case "PRT SCR":
			case "PRINT SCREEN":
				robot.keyPress(KeyEvent.VK_PRINTSCREEN);
				robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
				break;

			case "F1":
				robot.keyPress(KeyEvent.VK_F1);
				robot.keyRelease(KeyEvent.VK_F1);
				break;
			case "F2":
				robot.keyPress(KeyEvent.VK_F2);
				robot.keyRelease(KeyEvent.VK_F2);
				break;
			case "F3":
				robot.keyPress(KeyEvent.VK_F3);
				robot.keyRelease(KeyEvent.VK_F3);
				break;
			case "F4":
				robot.keyPress(KeyEvent.VK_F4);
				robot.keyRelease(KeyEvent.VK_F4);
				break;
			case "F5":
				robot.keyPress(KeyEvent.VK_F5);
				robot.keyRelease(KeyEvent.VK_F5);
				break;
			case "F6":
				robot.keyPress(KeyEvent.VK_F6);
				robot.keyRelease(KeyEvent.VK_F6);
				break;

			case "F7":
				robot.keyPress(KeyEvent.VK_F7);
				robot.keyRelease(KeyEvent.VK_F7);
				break;
			case "F8":
				robot.keyPress(KeyEvent.VK_F8);
				robot.keyRelease(KeyEvent.VK_F8);
				break;
			case "F9":
				robot.keyPress(KeyEvent.VK_F9);
				robot.keyRelease(KeyEvent.VK_F9);
				break;
			case "F10":
				robot.keyPress(KeyEvent.VK_F10);
				robot.keyRelease(KeyEvent.VK_F10);
				break;
			case "F11":
				robot.keyPress(KeyEvent.VK_F11);
				robot.keyRelease(KeyEvent.VK_F11);
				break;

			case "F12":
				robot.keyPress(KeyEvent.VK_F12);
				robot.keyRelease(KeyEvent.VK_F12);
				break;
			case "CHECKBOXES_STOP":
				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.keyRelease(KeyEvent.VK_SHIFT);
				robot.keyRelease(KeyEvent.VK_ALT);
				break;
			case "ALT+CTR+DEL":
				robot.keyPress(KeyEvent.VK_CONTROL);
				robot.keyPress(KeyEvent.VK_ALT);

				robot.keyPress(KeyEvent.VK_DELETE);
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				robot.keyRelease(KeyEvent.VK_CONTROL);
				robot.keyRelease(KeyEvent.VK_ALT);
				robot.keyRelease(KeyEvent.VK_DELETE);

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

					robot.keyPress(ks.getKeyCode());
					robot.keyRelease(ks.getKeyCode());
				}
			}
} 
            else if (line.startsWith("Send:")) {
                final int numberOfImages = Integer.parseInt(line.replace("Send:", ""));
                try {
                    MouseApp.pw.println("ok");
                    System.out.println(numberOfImages);
                    final String[] title = this.br.readLine().split("@@@@@@");
                    String appFolder = title[title.length - 1];
                    System.out.println("appFolder= " + appFolder);
                    if (appFolder == null || !appFolder.matches(".*[A-Za-z].*")) {
                        appFolder = "remoteDesktop_Files";
                    }
                    for (int j = 0; j < numberOfImages; ++j) {
                        System.out.println("title= " + title[j]);
                        final byte[] buffer = new byte[1045];
                        String desktop = "";
                        switch (MouseApp.ostype) {
                            case Windows: {
                                final FileSystemView filesys = FileSystemView.getFileSystemView();
                                final File[] roots = filesys.getRoots();
                                desktop = filesys.getHomeDirectory().getPath();
                                break;
                            }
                            case MacOS:
                            case Linux: {
                                desktop = new File(System.getProperty("user.home"), "Desktop").getPath();
                                break;
                            }
                        }
                        File f;
                        if (desktop == null || desktop == "") {
                            f = new File(String.valueOf(appFolder) + File.separator + title[j]);
                        }
                        else {
                            f = new File(String.valueOf(desktop) + File.separator + appFolder + File.separator + title[j]);
                        }
                        final File directory = new File(String.valueOf(desktop) + File.separator + appFolder);
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        Desktop.getDesktop().open(new File(directory.getAbsolutePath()));
                        final FileOutputStream fos = new FileOutputStream(f);
                        int bytes = 0;
                        boolean eof = false;
                        while (!eof) {
                            bytes = MouseApp.s.getInputStream().read(buffer);
                            final int offset = bytes - 11;
                            byte[] eofByte = new byte[11];
                            String message = null;
                            try {
                                eofByte = Arrays.copyOfRange(buffer, offset, bytes);
                                message = new String(eofByte, 0, 11);
                            }
                            catch (ArrayIndexOutOfBoundsException e10) {
                                message = "end of file";
                            }
                            if (message.equalsIgnoreCase("end of file")) {
                                eof = true;
                            }
                            else {
                                fos.write(buffer, 0, bytes);
                                fos.flush();
                            }
                        }
                        fos.close();
                    }
                    MouseApp.pw.println("send_over");
                }
                catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            else if (line.startsWith("Resolution:")) {
                try {
                    final String ln = line.replace("Resolution:", "");
                    final int resolution = MouseApp.scHeight = (MouseApp.scWidth = Integer.parseInt(ln));
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            else if (line.startsWith("QualityPad:")) {
                final String str = line.replace("QualityPad:", "");
                MouseApp.qualityPad = Integer.parseInt(str) / 100.0f;
            }
            else if (line.startsWith("QualityCam:")) {
                final String str = line.replace("QualityCam:", "");
                MouseApp.qualitycamera = Integer.parseInt(str) / 100.0f;
            }
            else if (line.startsWith("GLOBAL_IP:")) {
                MouseApp.qualityPad = 0.3f;
                this.isSignedIn = true;
                MouseApp.qualitycamera = 0.1f;
                MouseApp.scWidth = 580;
                MouseApp.scHeight = 580;
                final String userName = line.replace("GLOBAL_IP:", "");
                try {
                    MouseApp.isLoggedIn = true;
                    final String localUserName = Fr.userName.getText();
                    if (!userName.replaceAll(" ", "").equals(localUserName.replaceAll(" ", ""))) {
                        MouseApp.isLoggedIn = false;
                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    try {
                                        MouseApp.writeJPG(MouseApp.getErrorImage(), MouseApp.s.getOutputStream(), 0.5f);
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    Thread.sleep(2000L);
                                }
                                catch (InterruptedException e2) {
                                    e2.printStackTrace();
                                }
                                MouseApp.this.closeAll();
                                try {
                                    Thread.sleep(1000L);
                                }
                                catch (InterruptedException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }.start();
                    }
                }
                catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            else if (line.equalsIgnoreCase("Bluetooth")) {
                MouseApp.isLoggedIn = true;
                this.isSignedIn = true;
                MouseApp.qualityPad = 0.3f;
                MouseApp.scWidth = 400;
                MouseApp.qualitycamera = 0.1f;
                MouseApp.scHeight = 400;
            }
            else if (line.equalsIgnoreCase("LOCAL_IP")) {
                MouseApp.isLoggedIn = true;
                this.isSignedIn = true;
                MouseApp.qualityPad = 0.5f;
                MouseApp.qualitycamera = 0.1f;
                MouseApp.scWidth = 900;
                MouseApp.scHeight = 900;
            }
            else if (line.equalsIgnoreCase("LEFT_CLICK")) {
                robot.mousePress(16);
                robot.mouseRelease(16);
            }
            else if (line.equalsIgnoreCase("LEFT_CLICK_UP")) {
                robot.mouseRelease(16);
            }
            else if (line.equalsIgnoreCase("LEFT_CLICK_DOWN")) {
                robot.mousePress(16);
            }
            else if (line.equalsIgnoreCase("RIGHT_CLICK")) {
                robot.mousePress(4);
                robot.mouseRelease(4);
            }
            else if (line.equalsIgnoreCase("SCROLL_DOWN")) {
                robot.mouseWheel(1);
            }
            else if (line.equalsIgnoreCase("SCROLL_UP")) {
                robot.mouseWheel(-1);
            }
            else if (line.startsWith("ZOOM:")) {
                try {
                    final String str = line.replace("ZOOM:", "");
                    this.percentZoom = 100 - Integer.parseInt(str);
                }
                catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            else {
                if (line.equals("NULL")) {
                    throw new NullPointerException("Closing stream");
                }
                if (line.startsWith("SHUT_DOWN_IN:")) {
                    final String shut_down_in = line.replace("SHUT_DOWN_IN:", "");
                    this.timeToShutDown = Integer.parseInt(shut_down_in);
                    if (this.shutDownThread != null && this.shutDownThread.isAlive()) {
                        this.timeCounter = 0;
                        this.seconds = 0;
                        if (this.w != null) {
                            this.w.hide();
                            this.w.setVisible(false);
                            this.w = null;
                        }
                        return;
                    }
                    (this.shutDownThread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while (MouseApp.this.timeCounter < MouseApp.this.timeToShutDown) {
                                    if (MouseApp.fr.timeLab != null) {
                                        MouseApp.fr.timeLab.setText("Time to shutDown: " + (MouseApp.this.timeToShutDown - MouseApp.this.timeCounter));
                                    }
                                    Thread.sleep(1000L);
                                    final MouseApp this$0 = MouseApp.this;
                                    MouseApp.access$6(this$0, this$0.seconds + 1);
                                    if (MouseApp.this.seconds >= 60) {
                                        final MouseApp this$2 = MouseApp.this;
                                        MouseApp.access$7(this$2, this$2.timeCounter + 1);
                                        MouseApp.access$6(MouseApp.this, 0);
                                        if (MouseApp.this.timeToShutDown - MouseApp.this.timeCounter <= 3) {
                                            Toolkit.getDefaultToolkit().beep();
                                        }
                                    }
                                    if (MouseApp.this.timeToShutDown - MouseApp.this.timeCounter <= 15) {
                                        if (MouseApp.this.w == null) {
                                            MouseApp.access$9(MouseApp.this, new Wind(null, MouseApp.this));
                                        }
                                        MouseApp.this.w.message = "Shut Down in " + ((MouseApp.this.timeToShutDown - MouseApp.this.timeCounter - 1) * 60 + (60 - MouseApp.this.seconds));
                                        MouseApp.this.w.repaint();
                                    }
                                }
                                try {
                                    MouseApp.shutdown();
                                }
                                catch (RuntimeException e) {
                                    e.printStackTrace();
                                }
                                catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            catch (InterruptedException e3) {
                                e3.printStackTrace();
                            }
                        }
                    }).start();
                }
                else if (line.equalsIgnoreCase("SHUT DOWN")) {
                    try {
                        shutdown();
                    }catch (RuntimeException e5) {
                        e5.printStackTrace();
                    } catch (Exception e6) {
                        e6.printStackTrace();
                    }
                }
                else if (line.equalsIgnoreCase("SLEEP")) {
                    try {
                        final String operatingSystem = System.getProperty("os.name");
                        String shutdownCommand = null;
                        if ("Linux".equalsIgnoreCase(operatingSystem) || "Mac OS X".equalsIgnoreCase(operatingSystem)) {
                            shutdownCommand = "shutdown -h now";
                        }
                        else {
                            if (!"Windows".equalsIgnoreCase(operatingSystem)) {
                                Runtime.getRuntime().exec("shutdown -s -f");
                                Runtime.getRuntime().exec("shutdown -h now");
                                System.exit(0);
                                return;
                            }
                            shutdownCommand = "shutdown -s -f";
                        }
                        Runtime.getRuntime().exec(shutdownCommand);
                    }
                    catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                else if (line.equalsIgnoreCase("RESTART")) {
                    try {
                        final String operatingSystem = System.getProperty("os.name");
                        String shutdownCommand = null;
                        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
                            shutdownCommand = "shutdown -r now";
                        }
                        else {
                            if (!"Windows".equals(operatingSystem)) {
                                throw new RuntimeException("Unsupported operating system.");
                            }
                            shutdownCommand = "shutdown -r";
                        }
                        Runtime.getRuntime().exec(shutdownCommand);
                    }
                    catch (IOException e6) {
                        e6.printStackTrace();
                    }
                }
                else if (line.startsWith("CommandLine:")) {
                    line = line.replace("CommandLine:", "");
                    final ArrayList<String> cmd = CMD.Excecute(line);
                    if (cmd != null) {
                        MouseApp.pw.println(String.valueOf(line) + "_Command");
                        for (final String s : cmd) {
                            MouseApp.pw.println(s);
                            System.out.println(s);
                        }
                        MouseApp.pw.println("@END@");
                    }
                }
                else if (line.startsWith("Motion:")) {
                    line = line.replace("Motion:", "");
                    final String[] values = line.split("@@");
                    final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    final int width = MouseInfo.getPointerInfo().getLocation().x;
                    final int height = MouseInfo.getPointerInfo().getLocation().y;
                    int moveHeight = Integer.parseInt(values[1].replace("y:", ""));
                    if (moveHeight == 0) {
                        moveHeight = Integer.parseInt(values[0].replace("x:", ""));
                    }
                    robot.mouseMove(width + Integer.parseInt(values[2].replace("z:", "")), height - moveHeight);
                }
                else if (line.startsWith("Move:")) {
                    line = line.replace("Move:", "").replace("=", "").replace("x", "").replace("y", "");
                    final String[] integers = line.split(":");
                    robot.mouseMove(MouseInfo.getPointerInfo().getLocation().x + (int)Float.parseFloat(integers[0]), MouseInfo.getPointerInfo().getLocation().y + (int)Float.parseFloat(integers[1]));
                }
                else if (line.startsWith("MoveTo:")) {
                    final GraphicsDevice gd2 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    final int width2 = gd2.getDisplayMode().getWidth();
                    final int height2 = gd2.getDisplayMode().getHeight();
                    line = line.replace("MoveTo:", "").replace("=", "").replace("x", "").replace("y", "");
                    final String[] integers2 = line.split(":");
                    robot.mouseMove((int)(Float.parseFloat(integers2[0]) * width2), (int)(Float.parseFloat(integers2[1]) * height2));
                }
                else if (line.startsWith("x:")) {
                    final String[] values = line.split("@@");
                    final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                    final int width = gd.getDisplayMode().getWidth();
                    final int height = gd.getDisplayMode().getHeight();
                    robot.mouseMove(width * Integer.parseInt(values[2].replace("z:", "")) / 5000, height - height * Integer.parseInt(values[1].replace("y:", "")) / 5000);
                }
                else if (line.equalsIgnoreCase("START_CAMERA")) {
                    try {
                        this.webCam = new WebcamShot();
                        new Thread(this.webCam).start();
                    }
                    catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
                else if (line.equalsIgnoreCase("START_AUDIO_RECORDING")) {
                    try {
                        if (this.webCam != null) {
                            try {
                                this.webCam.closeCamera();
                                this.webCam = null;
                            }
                            catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        }
                        if (this.mic != null) {
                            Mic.stopped = true;
                            this.mic = null;
                        }
                        Thread.sleep(20L);
                        System.out.println("Start Recording");
                        (this.mic = new Mic()).writeAndSend();
                    }
                    catch (Exception e3) {
                        e3.printStackTrace();
                    }
                    catch (Error e7) {
                        e7.printStackTrace();
                    }
                }
                else if (line.equalsIgnoreCase("STOP_AUDIO_RECORDING")) {
                    if (this.mic != null) {
                        System.out.println("Stop Recording");
                        Mic.stopped = true;
                        this.mic = null;
                    }
                }
                else if (line.equalsIgnoreCase("SCREENSHOT") && MouseApp.s != null && !MouseApp.s.isClosed()) {
                    if (this.thread != null && this.thread.isAlive()) {
                        return;
                    }
                    (this.thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                final OutputStream out = MouseApp.s.getOutputStream();
                                if (MouseApp.isLoggedIn) {
                                    MouseApp.writeJPG(MouseApp.this.getComputerScreenshot(), out, MouseApp.qualityPad);
                                }
                                else {
                                    MouseApp.writeJPG(MouseApp.getErrorImage(), out, MouseApp.qualityPad);
                                }
                                out.flush();
                            }
                            catch (IOException ex) {
                                ex.printStackTrace();
                                MouseApp.this.closeAll();
                                try {
                                    Thread.sleep(1000L);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MouseApp.this.internetConnection();
                            }
                            catch (Exception ex2) {
                                ex2.printStackTrace();
                                MouseApp.this.closeAll();
                                try {
                                    Thread.sleep(1000L);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MouseApp.this.internetConnection();
                            }
                            catch (Error ex3) {
                                ex3.printStackTrace();
                                MouseApp.this.closeAll();
                                try {
                                    Thread.sleep(1000L);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                MouseApp.this.internetConnection();
                            }
                        }
                    }).start();
                }
                else if (line.equalsIgnoreCase("CAM_SCREENSHOT")) {
                    try {
                        if (this.webCam == null) {
                            return;
                        }
                        final OutputStream out = MouseApp.s.getOutputStream();
                        writeJPG(this.webCam.getCameraImage(), out, MouseApp.qualitycamera);
                        out.flush();
                    }
                    catch (Exception e4) {
                        e4.printStackTrace();
                        this.closeAll();
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        this.internetConnection();
                    }
                    catch (Error e8) {
                        e8.printStackTrace();
                        this.closeAll();
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        this.internetConnection();
                    }
                }
                else if (line.equalsIgnoreCase("STOP_CAM")) {
                    if (this.webCam == null) {
                        return;
                    }
                    try {
                        this.webCam.closeCamera();
                        this.webCam = null;
                    }
                    catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            
            try {
                if (MouseApp.s != null) {
                    MouseApp.s.getOutputStream().flush();
                }
            }
            catch (IOException e6) {
                e6.printStackTrace();
            }
        }
        try {
            if (!this.isSignedIn) {
                this.closeAll();
            }
        }
        catch (Exception e3) {
            e3.printStackTrace();
        }
}
    
    public static void shutdown() throws RuntimeException, IOException {
        final String operatingSystem = System.getProperty("os.name");
        String shutdownCommand;
        if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
            shutdownCommand = "shutdown -h now";
        }
        else {
            if (!"Windows".equals(operatingSystem)) {
                throw new RuntimeException("Unsupported operating system.");
            }
            shutdownCommand = "shutdown -s";
        }
        Runtime.getRuntime().exec(shutdownCommand);
        System.exit(0);
    }
    
    public static void writeJPG(final BufferedImage bufferedImage, final OutputStream outputStream, final float quality) throws IOException {
        final Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("jpg");
        final ImageWriter imageWriter = iterator.next();
        final ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();
        imageWriteParam.setCompressionMode(2);
        imageWriteParam.setCompressionQuality(quality);
        final ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
        imageWriter.setOutput(imageOutputStream);
        final IIOImage iioimage = new IIOImage(bufferedImage, null, null);
        imageWriter.write(null, iioimage, imageWriteParam);
        imageOutputStream.flush();
        outputStream.flush();
    }
    
    public BufferedImage getComputerScreenshot() {
        final Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage capture = null;
        try {
            if (MouseApp.robot == null) {
                MouseApp.robot = new Robot();
            }
            capture = MouseApp.robot.createScreenCapture(screenRect);
            final Graphics2D graphics2D = capture.createGraphics();
            final int x = MouseInfo.getPointerInfo().getLocation().x;
            final int y = MouseInfo.getPointerInfo().getLocation().y;
            if (MouseApp.showMouseIcon) {
                graphics2D.setColor(Color.BLUE);
                final int length = 30;
                graphics2D.fillOval(x - length / 3, y - length / 3, length, length);
                graphics2D.setColor(Color.BLACK);
                graphics2D.fillRect(x, y, 10, 10);
                graphics2D.setColor(Color.WHITE);
                graphics2D.fillRect(x + 2, y + 2, 7, 7);
                graphics2D.setColor(Color.RED);
                graphics2D.fillRect(x + 2, y, 6, 6);
                graphics2D.dispose();
            }
            try {
                final int cropWidth = (int)(capture.getWidth() * this.percentZoom / 100.0);
                final int cropHeight = (int)(capture.getHeight() * this.percentZoom / 100.0);
                int cropPosX = x - cropWidth / 2;
                int cropPosY = y - cropHeight / 2;
                if (cropPosX < 0) {
                    cropPosX = 0;
                }
                else if (cropPosX + cropWidth > capture.getWidth()) {
                    cropPosX = capture.getWidth() - cropWidth;
                }
                if (cropPosY < 0) {
                    cropPosY = 0;
                }
                else if (cropPosY + cropHeight > capture.getHeight()) {
                    cropPosY = capture.getHeight() - cropHeight;
                }
                if (capture != null) {
                    capture = this.cropImage(capture, cropPosX, cropPosY, cropWidth, cropHeight);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final Image image = capture.getScaledInstance(MouseApp.scWidth, MouseApp.scHeight, 2);
            final BufferedImage bf2 = new BufferedImage(MouseApp.scWidth, MouseApp.scHeight, 1);
            final Graphics2D g2d = bf2.createGraphics();
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();
            return bf2;
        }
        catch (AWTException ex) {
            ex.printStackTrace();
            return capture;
        }
    }
    
    private BufferedImage cropImage(final BufferedImage src, final int x, final int y, final int width, final int height) {
        final BufferedImage dest = src.getSubimage(x, y, width, height);
        return dest;
    }
    
    public static BufferedImage getErrorImage() throws IOException {
        final URL url = MouseApp.class.getResource("/resources/denied.png");
        final ImageIcon icon = new ImageIcon(url);
        final Image image = icon.getImage();
        final BufferedImage bf2 = new BufferedImage(MouseApp.scWidth + 100, MouseApp.scHeight + 100, 1);
        final Graphics2D g2d = bf2.createGraphics();
        g2d.drawImage(image, 100, 50, null);
        g2d.dispose();
        return bf2;
    }
    
    public static void main(final String[] args) {
        MouseApp.fr = new Fr();
        MouseApp.ostype = OsCheck.getOperatingSystemType();
    }
    
    void openBT() {
          LocalDevice local = null;
    try
    {
        UUID uuid; // "04c6093b-0000-1000-8000-00805f9b34fb"
              uuid = new UUID(80087355);
			String url = "btspp://localhost:" + uuid.toString()
					+ ";name=RemoteBluetooth";
notifier = (StreamConnectionNotifier) Connector.open(url);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      fr.createUI();
      return;
    }
    while (runConnetions) {
      try
      {
        System.out.println("waiting for connection...");
        this.connection = this.notifier.acceptAndOpen();
        System.out.println("connected " + this.connection.toString());
        pw = new PrintWriter(this.connection.openOutputStream(), true);
        Dimension screenSize = Toolkit.getDefaultToolkit()
          .getScreenSize();
        int width = (int)screenSize.getWidth();
        int height = (int)screenSize.getHeight();
        int maxResolution = Math.min(width, height);
        pw.println("works:maxResolution:" + maxResolution + 
          "@@quality:" + qualityPad * 100.0F);
        new Thread()
        {
          public void run()
          {
            try
            {
              MouseApp.this.br = new BufferedReader(new InputStreamReader(
                MouseApp.this.connection.openInputStream(), "UTF-8"));
              MouseApp.this.receiver(MouseApp.this.br);
            }
            catch (Exception ex)
            {
              ex.printStackTrace();
            }
          }
        }.start();
      }
      catch (Exception e)
      {
        e.printStackTrace();
        return;
      }
    }
    }
    
    public Wind getW() {
        return this.w;
    }
    
    public void setW(final Wind w) {
        this.w = w;
    }
    
    static /* synthetic */ void access$0(final MouseApp mouseApp, final ServerSocket ss) {
        mouseApp.ss = ss;
    }
    
    static /* synthetic */ void access$6(final MouseApp mouseApp, final int seconds) {
        mouseApp.seconds = seconds;
    }
    
    static /* synthetic */ void access$7(final MouseApp mouseApp, final int timeCounter) {
        mouseApp.timeCounter = timeCounter;
    }
    
    static /* synthetic */ void access$9(final MouseApp mouseApp, final Wind w) {
        mouseApp.w = w;
    }
    
    static /* synthetic */ void access$12(final MouseApp mouseApp, final BufferedReader br) {
        mouseApp.br = br;
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

	

}// end class