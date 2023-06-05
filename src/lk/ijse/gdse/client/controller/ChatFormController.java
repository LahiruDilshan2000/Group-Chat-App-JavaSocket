package lk.ijse.gdse.client.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ChatFormController {
    public VBox vbox;
    public JFXTextField txtMsg;
    public ScrollPane ScrollPane;

    public Socket socket;
    public DataInputStream inputStream;
    public DataOutputStream outputStream;
    public String userName;
    public String type;
    public File file;
    public Label lblName;
    public ImageView img;
    public JFXButton btnCancel;

    public void initialize() {

        new Thread(() -> {
            try {

                socket = new Socket("localhost", 3002);

                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());


                while (socket.isConnected()) {

                    type = inputStream.readUTF();

                    if (type.equalsIgnoreCase("text")) {
                        setText();
                    } else {
                        setFile();
                    }


                }
                inputStream.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        img.setVisible(false);
        btnCancel.setVisible(false);
        vbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ScrollPane.setVvalue((Double) newValue);
            }
        });
    }

    private void setFile() {

        try {

            String userName = inputStream.readUTF();

            byte[] sizeAr = new byte[4];
            inputStream.read(sizeAr);
            int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

            byte[] imageAr = new byte[size];
            inputStream.read(imageAr);


            HBox hBox = new HBox();

            if (userName.equalsIgnoreCase(this.userName)) {
                hBox.setAlignment(Pos.CENTER_RIGHT);
            } else {
                hBox.setAlignment(Pos.CENTER_LEFT);
            }

            hBox.setPadding(new Insets(5, 5, 5, 10));


            Image image1 = new Image(new ByteArrayInputStream(imageAr));

            ImageView imageView = new ImageView(image1);

            imageView.setFitWidth(200);
            imageView.setPreserveRatio(true);

            hBox.getChildren().add(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vbox.getChildren().add(hBox);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setText() {

        try {

            String userName = inputStream.readUTF();
            String message = inputStream.readUTF();

            if (!message.isEmpty()) {
                HBox hBox = new HBox();

                Text text = new Text(message);
                TextFlow textFlow = new TextFlow(text);

                if (userName.equalsIgnoreCase(this.userName)) {

                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    textFlow.setStyle("-fx-color : rgb(239, 242, 255);" +
                            "-fx-background-color: rgb(15, 125, 242);" +
                            "-fx-background-radius: 20px");

                    text.setFill(Color.color(0.934, 0.945, 0.996));

                } else {

                    text.setText(userName + " : " + message);

                    hBox.setAlignment(Pos.CENTER_LEFT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                            "-fx-background-radius: 20px");

                }

                text.setStyle("-fx-font-size: 20px;");
                textFlow.setPadding(new Insets(5, 10, 8, 10));
                hBox.getChildren().add(textFlow);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        vbox.getChildren().add(hBox);
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    public void setUserName(String userName) {

        this.userName = userName;
        lblName.setText(this.userName);

    }

    public void btnSendOnAction(ActionEvent actionEvent) {

        try {

            if (!txtMsg.getText().isEmpty()) {

                outputStream.writeUTF("text".trim());
                outputStream.writeUTF(this.userName.trim());
                outputStream.writeUTF(txtMsg.getText().trim());
                outputStream.flush();
                txtMsg.clear();

            } else if (null != this.file) {

                BufferedImage bufferedImage = ImageIO.read(this.file);
                System.out.println(this.file);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

                byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

                outputStream.writeUTF("picture".trim());
                outputStream.writeUTF(this.userName.trim());
                outputStream.write(size);
                outputStream.write(byteArrayOutputStream.toByteArray());

                outputStream.flush();

                txtMsg.setVisible(true);
                btnCancel.setVisible(true);
                img.setVisible(false);
                btnCancel.setVisible(false);
                file = null;

                System.out.println("Flushed: " + System.currentTimeMillis());

                System.out.println("Closing: " + System.currentTimeMillis());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnAddOnAction(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) lblName.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();


        File defaultPath = new File("C:\\Users\\RedSparrow_YT\\Downloads\\Pics");
        fileChooser.setInitialDirectory(defaultPath);

        this.file = fileChooser.showOpenDialog(stage);

        if (null != this.file) {


            BufferedImage bufferedImage = ImageIO.read(this.file);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            byte[] array = byteArrayOutputStream.toByteArray();
            Image image = new Image(new ByteArrayInputStream(array));
            img.setImage(image);
            txtMsg.setVisible(false);
            img.setVisible(true);
            btnCancel.setVisible(true);
        }
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {

        this.file = null;
        img.setVisible(false);
        btnCancel.setVisible(false);
        txtMsg.setVisible(true);
    }
}
