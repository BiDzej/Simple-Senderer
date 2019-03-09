package sample;

import javafx.application.Platform;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Server waiting for new photos.
 */
public class Serwer extends Thread{

    private String  path;
    private Controller controller;

    public Serwer (String path, Controller controller)
    {
        this.path = path;
        this.controller = controller;

    }

    public void run()
    {
        ServerSocket servSocket = null;

        try {
            servSocket = new ServerSocket(1313);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true)
        {
            Socket socket = null;
            String photoName = "";
            try {
                socket = servSocket.accept();

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                photoName = objectInputStream.readObject().toString();

                byte [] buffer = (byte[]) objectInputStream.readObject();

                FileOutputStream fileOutputStream = new FileOutputStream(path+ photoName);
                fileOutputStream.write(buffer);

                fileOutputStream.close();
                objectInputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date date = new Date();

            final String log = "" + dateFormat.format(date) + " Received new photo: " + photoName+"\n";
            Platform.runLater(()->controller.addLog(log));

        }
    }

    //method to changing destination path for photos
    public void changePath(String path)
    {
        this.path = path;
    }
}
