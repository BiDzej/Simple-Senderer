package sample;

import java.io.IOException;
import java.net.*;
import java.util.Random;

/**
 * After multicast request, it sends multicast answear to inform all devices in local area that it's active.
 */
public class MulticastAnswear extends Thread {

    private DatagramSocket socket = null;
    private InetAddress group = null;
    private byte[] buf;

    public void run()
    {
        try {
            socket = new DatagramSocket();
            group = InetAddress.getByName("230.0.0.1");

            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostAddr = inetAddress.getHostAddress();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
            byte[] mac = networkInterface.getHardwareAddress();
            StringBuilder stringBuilder = new StringBuilder();

            for(int i = 0; i < mac.length; ++i)
                stringBuilder.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            String hostMAC = stringBuilder.toString();

            String answear = hostAddr+";"+hostMAC;
            buf = answear.getBytes();
            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4554);

            Random random = new Random();
            int time = random.nextInt() % 1000;
            time = (time > 0) ? time : -time;
            sleep(time);
            socket.send(packet);
            socket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException | InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
