package com.test.menutest;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MulticastRequest extends Thread{


    private DatagramSocket socket = null;
    private InetAddress group;
    private byte[] buf;


    public void run() {

        try {
            socket = new DatagramSocket();
            group = InetAddress.getByName("230.0.0.0");
            buf = "Request".getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4553);
            socket.send(packet);
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
