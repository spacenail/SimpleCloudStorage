package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.CloudMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileMessage;
import com.github.spacenail.SimpleCloudStorage.model.FileRequestMessage;
import com.github.spacenail.SimpleCloudStorage.model.ListMessage;
import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class ClientController implements Initializable {
    @FXML
    private ListView<String> clientView;
    @FXML
    private ListView<String> serverView;
    @FXML
    private TextField clientPath;
    @FXML
    private TextField serverPath;
    private ObjectEncoderOutputStream objectEncoderOutputStream;
    private ObjectDecoderInputStream objectDecoderInputStream;
    private Path clientDirectory;

    public void download() throws IOException {
        objectEncoderOutputStream.writeObject(new FileRequestMessage(serverView.getSelectionModel().getSelectedItem()));
    }

    public void upload() throws IOException {
        objectEncoderOutputStream.writeObject(new FileMessage(clientDirectory.resolve(clientView.getSelectionModel().getSelectedItem())));
    }

    public void close() {
        Platform.exit();
    }

    public void updateClientView() {
        Platform.runLater(() -> {
            clientPath.setText(clientDirectory.toAbsolutePath().toString());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems().addAll(clientDirectory.toFile().list());
        });
    }

    private void read() {
        while (true) {
            try {
                CloudMessage message = (CloudMessage) objectDecoderInputStream.readObject();
                switch (message.getMessageType()) {
                    case FILE:
                        FileMessage fileMessage = (FileMessage) message;
                        Files.write(clientDirectory.resolve(fileMessage.getName()), fileMessage.getBytes());
                        updateClientView();
                        break;
                    case LIST:
                        ListMessage listMessage = (ListMessage) message;
                        Platform.runLater(() -> {
                            serverPath.setText(listMessage.getPath());
                            serverView.getItems().clear();
                            serverView.getItems().add("...");
                            serverView.getItems().addAll(listMessage.getList());
                        });
                        break;
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clientViewHandler(MouseEvent event){
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String item = clientView.getSelectionModel().getSelectedItem();
            if ("...".equals(item)) {
                if (clientDirectory.getParent() != null) {
                    clientDirectory = clientDirectory.getParent();
                }
            } else if (clientDirectory.resolve(item).toFile().isDirectory()) {
                clientDirectory = clientDirectory.resolve(item);
            }
            updateClientView();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            objectEncoderOutputStream = new ObjectEncoderOutputStream(socket.getOutputStream());
            objectDecoderInputStream = new ObjectDecoderInputStream(socket.getInputStream());
            clientDirectory = Paths.get("ClientDirectory").toAbsolutePath();

            clientView.setOnMouseClicked(this::clientViewHandler);

            updateClientView();
            Thread readThread = new Thread(() -> read());
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
