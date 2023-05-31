package lk.ijse.gdse.server;

import lk.ijse.gdse.server.controller.LocalSocketManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerInitializer {
    public static void main(String[] args) {

        List<LocalSocketManager> localSocketManagerList = new ArrayList<>();
        ServerSocket serverSocket;
        Socket socket;
        try {

            serverSocket = new ServerSocket(3002);

            while (!serverSocket.isClosed()) {

                System.out.println("Waiting");
                socket = serverSocket.accept();
                System.out.println("accept");

                LocalSocketManager localSocketManager = new LocalSocketManager(socket, localSocketManagerList
                        /*new DataInputStream(socket.getInputStream()).readUTF()*/ );
                localSocketManagerList.add(localSocketManager);

                Thread thread =new Thread(localSocketManager);
                System.out.println("Thread start");
                thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
