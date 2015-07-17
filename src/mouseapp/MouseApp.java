/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mouseapp;

import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;

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
        try {
            internetConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * enables internet connection
     */
    private void internetConnection() {
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

    private void bluetoothConnection() {
        try {
            LocalDevice ld = LocalDevice.getLocalDevice();
        } catch (BluetoothStateException ex) {
            ex.printStackTrace();
        }
    }

    private void closeAll() {
        try {
            pw.close();
            s.close();
            ss.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public static void main(String[] args) {
        new MouseApp();
    }

}
