package mouseapp;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 *
 * @author tsoglani
 */
public class MouseApp {

    final int port = 2000;
    private ServerSocket ss;
    private PrintWriter pw;
    private Socket s;

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
            Logger.getLogger(MouseApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    private void closeAll() {
        try {
            pw.close();
            s.close();
            ss.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        //    fr.createUI();
            
        }
    }

    public void receiver(Socket s) {
        try {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                while (true) {
                    Thread.sleep(10);
                    String line = br.readLine();
                    System.out.println(line);
                    processString(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            closeAll();
            Thread.sleep(1000);
            new MouseApp();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    
    
       public void receiver(BufferedReader br) {
        try {
            try {
           
                while (true) {
                    Thread.sleep(10);
                    String line = br.readLine();
                    System.out.println(line);
                    processString(line);
                }
            } catch (Exception ex) {
                ex.printStackTrace();

            }
            closeAll();
            Thread.sleep(1000);
            new MouseApp();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void processString(String line) throws AWTException {
        Robot robot = new Robot();
        if (line.startsWith("keyboard")) {
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

                default:
                    robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(symbol.charAt(0)));
                    robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(symbol.charAt(0)));

            }

        } else if (line.endsWith("LEFT_CLICK")) {
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
        } else if (line.equals("RIGHT_CLICK")) {
            robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
        } else if (line.startsWith("x:")) {

            //z==x
            //x==y
            String[] values = line.split("@@");
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();
            robot.mouseMove(width * Integer.parseInt(values[2].replace("z:", "")) / 5000, height - height * Integer.parseInt(values[1].replace("y:", "")) / 5000);

        }
    }
public static Fr fr;
    public static void main(String[] args) {
        fr=new Fr();
    }

    public class BluetoothDeviceDiscovery implements DiscoveryListener {

//object used for waiting
        private Object lock = new Object();

//vector containing the devices discovered
        private Vector vecDevices = new Vector();

//main method of the application
        public BluetoothDeviceDiscovery() {
        }

        public ArrayList<String> getNames() throws IOException {
            ArrayList<String> list = new ArrayList<String>();

//create an instance of this class
//display local device address and name
            LocalDevice localDevice = LocalDevice.getLocalDevice();
            System.out.println("Address: " + localDevice.getBluetoothAddress());
            System.out.println("Name: " + localDevice.getFriendlyName());

//find devices
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

//print all devices in vecDevices
            int deviceCount = vecDevices.size();

            if (deviceCount <= 0) {
                System.out.println("No Devices Found .");
            } else {
//print bluetooth device addresses and names in the format [ No. address (name) ]
                System.out.println("Bluetooth Devices:" + Integer.toString(deviceCount));
                for (int i = 0; i < deviceCount; i++) {
                    RemoteDevice remoteDevice = (RemoteDevice) vecDevices.elementAt(i);
                    System.out.println((i + 1) + ". “+remoteDevice.getBluetoothAddress()+ (" + remoteDevice.getFriendlyName(false) + ")");

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
        }//end method

        @Override
        public void deviceDiscovered(RemoteDevice btDevice, DeviceClass dc) {
            if (!vecDevices.contains(btDevice)) {
                vecDevices.addElement(btDevice);
            }
        }

        @Override
        public void servicesDiscovered(int arg0, ServiceRecord[] srs) {
        
            if(srs.length>0){
                System.out.println(srs[0].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false));
            }
        }

        @Override
        public void serviceSearchCompleted(int i, int i1) {

        }
    }//end 
  StreamConnection connection = null;
    void openBT() throws IOException {
        LocalDevice local = null;

        StreamConnectionNotifier notifier;
      

        // setup the server to listen for connection
        try {
            local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(80087355); // "04c6093b-0000-1000-8000-00805f9b34fb"
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        // waiting for connection
        while (true) {
            try {
                System.out.println("waiting for connection...");
                connection = notifier.acceptAndOpen();
                System.out.println("connected "+connection.toString());
                pw = new PrintWriter(connection.openOutputStream(), true);
                pw.println("works");
                new Thread() {

                        @Override
                        public void run() {

                            try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(connection.openInputStream(), "UTF-8"));
                            receiver(br);
                            } catch (Exception ex) {
                                Logger.getLogger(MouseApp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                          

                        }
                    }.start();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}//end class

