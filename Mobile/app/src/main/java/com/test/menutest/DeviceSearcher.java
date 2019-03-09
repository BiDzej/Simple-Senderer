package com.test.menutest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Device searcher class. Devices are being searched by multicast and response from them.
 */
public class DeviceSearcher extends Thread {

    MulticastSocket socket = null;
    byte[] buf = new byte[256];


    public DeviceSearcher() {

    }


    public void run() {

        try {
            socket = new MulticastSocket(4554);
            InetAddress group = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(group);


        } catch (IOException e) {
            e.printStackTrace();
        }

        ActiveDevicesList list = ActiveDevicesList.getInstance();

        while(true)
        {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            String[] parts = received.split(";");
            list.addDevice(parts[0], parts[1]);
        }

    }

}
