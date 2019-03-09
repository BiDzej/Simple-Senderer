package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Receiving multicast, used to find devices in local network.
 */
public class MulticastReceiv extends Thread {

    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];

    public void run()
    {
        try {
            socket = new MulticastSocket(4553);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String receive = new String(packet.getData(), 0, packet.getLength());


            if(receive.equals(new String("Request")))
            {
                MulticastAnswear multicastAnswear = new MulticastAnswear();
                multicastAnswear.start();
            }
        }

    }
}
