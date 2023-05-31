package lk.ijse.gdse.client.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatUiFormController {
    public JFXTextArea txtDisplay;
    public JFXTextField txtMsg;

    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public String userName;
    public String message = "";
    public String imagePath;

    public void initialize(){
        new Thread(() ->{
            try {

                socket = new Socket("localhost",3002);

                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                txtDisplay.setEditable(false);

                while (socket.isConnected()){
                    /*byte[] arr = new byte[1024];
                    inputStream.read(arr);
                    message = new String(arr, "UTF-8");
                    System.out.println(message);*/
                    message = inputStream.readUTF();
//                    txtDisplay.setStyle("-fx-font-alignment: right");
                    txtDisplay.appendText("\n"+message);

                }
                /*inputStream.close();
                outputStream.close();
                socket.close();*/

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
                /*String srt = userName+" : "+txtMsg.getText().trim();
                byte[] array = srt.getBytes(StandardCharsets.UTF_8);
                outputStream.write(array);*/
                outputStream.flush();
                txtMsg.clear();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else if (!imagePath.isEmpty()) {
        }
    }



    public void btnBackOnAction(ActionEvent actionEvent) {
        try{

            BufferedImage bufferedImage = ImageIO.read(new File("C:\\My Workind Directry\\Intellij IDEA Project\\Group-Chat-App\\src\\lk\\ijse\\gdse\\Client\\assets\\bak.png"));

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

            outputStream.write(size);
            outputStream.write(byteArrayOutputStream.toByteArray());

            outputStream.flush();

            System.out.println("Flushed: " + System.currentTimeMillis());

            System.out.println("Closing: " + System.currentTimeMillis());

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
