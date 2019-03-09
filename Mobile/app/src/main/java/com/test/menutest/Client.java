package com.test.menutest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client application. It makes connection with choosen device and send a photo. After sending the photo can be deleted
 * if there was chosen "delete after sending" checkbox in settings.
 */
public class Client extends Thread{

    String ipAddr;
    int port;
    String path;
    boolean shouldDelete;

    public Client(String ipAddr, int port, String path, boolean shouldDelete) {
        this.ipAddr = ipAddr;
        this.port = port;
        this.path = path;
        this.shouldDelete = shouldDelete;
    }

    public void run()
    {
        Socket socket;
        try {
            socket = new Socket(ipAddr, port);

            File file = new File(path);

            //Sending a picture
            FileInputStream fileInputStream = new FileInputStream(path);
            byte [] buffer = new byte[fileInputStream.available()];
            fileInputStream.read(buffer);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(file.getName());
            objectOutputStream.writeObject(buffer);
            objectOutputStream.close();
            socket.close();
            if(shouldDelete)
                MyApplication.deletePhoto(path);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
