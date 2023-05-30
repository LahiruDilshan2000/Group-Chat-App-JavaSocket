package lk.ijse.gdse.server;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class LocalSocketManager implements Runnable {

    public Socket socket;
    public List<LocalSocketManager> localSocketManagerList;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public InputStream inputStream2;
    public String massage = "";
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
            inputStream2 = socket.getInputStream();

            while (socket.isConnected()) {

                /*massage = inputStream.readUTF();
                sentMsg();*/

                ByteArrayOutputStream bye = new ByteArrayOutputStream();

                int n;
                byte[] data = new byte[1024];
                while ((n = inputStream2.read(data, 0, data.length)) != -1){
                    bye.write(data, 0, n);
                }
                bye.flush();
                byte[] imageData = bye.toByteArray();

                FileOutputStream fileOutputStream = new FileOutputStream("adooo.png");
                fileOutputStream.write(imageData);

                fileOutputStream.close();

            }
            inputStream.close();
            outputStream.close();
            socket.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sentMsg() {

        try {
            for (LocalSocketManager localSocketManager : localSocketManagerList) {
                if (localSocketManager.socket.equals(this.socket)) {
                    String[] arr = massage.split(":");
                    localSocketManager.outputStream.writeUTF("Me : "+arr[1].trim());
                } else {
                    localSocketManager.outputStream.writeUTF(massage.trim());
                }
                localSocketManager.outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
