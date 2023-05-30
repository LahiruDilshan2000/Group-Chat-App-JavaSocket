package lk.ijse.gdse.client.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatUiFormController {
    public JFXTextArea txtDisplay;
    public JFXTextField txtMsg;

    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public OutputStream outputStream2;
    public String userName;
    public String message = "";

    public void initialize(){
        new Thread(() ->{
            try {

                socket = new Socket("localhost",3002);

                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());
                outputStream2 = socket.getOutputStream();

                txtDisplay.setEditable(false);

                while (socket.isConnected()){
                    message = inputStream.readUTF();
                    txtDisplay.setStyle("-fx-font-alignment: right");
                    txtDisplay.appendText("\n"+message);

                }
                inputStream.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    public void setUserName(String userName){

        this.userName = userName;
    }

    public void btnSendOnAction(ActionEvent actionEvent) {

        if (!txtMsg.getText().isEmpty()){
            try {
                outputStream.writeUTF(userName+" : "+txtMsg.getText().trim());
                outputStream.flush();
                txtMsg.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnBackOnAction(ActionEvent actionEvent) {

        try{

            byte [] image = Files.readAllBytes(Paths.get("C:\\My Workind Directry\\Intellij IDEA Project\\Group-Chat-App\\src\\lk\\ijse\\gdse\\Client\\assets\\bak.png"));

            outputStream.write(image);

            outputStream.flush();

            outputStream.close();


        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
