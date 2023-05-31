package lk.ijse.gdse.server;

import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.List;

public class LocalSocketManager implements Runnable {

    public Socket socket;
    public List<LocalSocketManager> localSocketManagerList;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public InputStream inputStream2;
    public String type;
    public String massage1 = "";
    public String userName;

    public LocalSocketManager(Socket socket, List<LocalSocketManager> localSocketManagerList) {

        this.socket = socket;
        this.localSocketManagerList = localSocketManagerList;
    }

    @Override
    public void run() {

        try {

            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            while (socket.isConnected()) {

                type = inputStream.readUTF();
                if (type.equalsIgnoreCase("text")) {
                    /*String s = inputStream.readUTF();
                    System.out.println(s);
                    String w = inputStream.readUTF();
                    System.out.println(w);*/
                    sendText();
                }else {
                    sendFile();
                }
                //sentMsg();



                /*byte[] sizeAr = new byte[4];
                inputStream.read(sizeAr);
                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                byte[] imageAr = new byte[size];
                inputStream.read(imageAr);



                for (LocalSocketManager localSocketManager : localSocketManagerList) {
                    localSocketManager.outputStream.write(sizeAr);
                    localSocketManager.outputStream.write(imageAr);
                    localSocketManager.outputStream.flush();
                }*/

              /*  byte[] sizeAr = new byte[4];
                inputStream.read(sizeAr);
                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                byte[] imageAr = new byte[size];
                inputStream.read(imageAr);

                BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

                System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                ImageIO.write(image, "png", new File("C:\\My Workind Directry\\Intellij IDEA Project\\Group-Chat-App\\src\\lk\\ijse\\gdse\\Client\\assets\\bak2222.png"));
*/
                //serverSocket.close();

            }
            inputStream.close();
            outputStream.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendFile() {

        try {
            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);

            for (LocalSocketManager localSocketManager : localSocketManagerList) {
                localSocketManager.outputStream.writeUTF(type);
                localSocketManager.outputStream.write(sizeAr);
                localSocketManager.outputStream.write(imageAr);
                localSocketManager.outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendText() {

        try {
            String userName = inputStream.readUTF();
            String message = inputStream.readUTF();
            System.out.println(userName);
            System.out.println(message);

            for (LocalSocketManager localSocketManager : localSocketManagerList) {
                localSocketManager.outputStream.writeUTF(type);
                localSocketManager.outputStream.writeUTF(userName);
                localSocketManager.outputStream.writeUTF(message);
                localSocketManager.outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
