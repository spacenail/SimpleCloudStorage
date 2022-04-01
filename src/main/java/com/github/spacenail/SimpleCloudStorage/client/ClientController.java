package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
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
    private Path clientDirectory;
    private Network network;

    public void closeNetwork(){
        network.close();
    }

    public void download() {
        network.send(new FileRequestMessage(
                getPath(serverPath.getText(), serverView.getSelectionModel().getSelectedItem())
                        .normalize().toAbsolutePath().toString(),
                clientPath.getText())
        );
    }

    public void upload() throws IOException {
        network.send(new FileMessage(
                getPath(clientPath.getText(), clientView.getSelectionModel().getSelectedItem()),
                serverPath.getText())
        );
    }

    private Path getPath(String dir, String file) {
        return Paths.get(dir).resolve(file);
    }

    @FXML
    private void close() {
        Platform.exit();
    }

    public void updateView(Path path) {
        Platform.runLater(() -> {
            clientPath.setText(path.toAbsolutePath().toString());
            clientView.getItems().clear();
            clientView.getItems().add("...");
            clientView.getItems().addAll(path.toFile().list());
        });
    }

    public void updateView(ListMessage listMessage) {
        Platform.runLater(() -> {
            serverPath.setText(listMessage.getPath());
            serverView.getItems().clear();
            serverView.getItems().add("...");
            serverView.getItems().addAll(listMessage.getList());
        });
    }

    private void clientViewHandler(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String item = clientView.getSelectionModel().getSelectedItem();
            if ("...".equals(item)) {
                if (clientDirectory.getParent() != null) {
                    clientDirectory = clientDirectory.getParent();
                }
            } else if (clientDirectory.resolve(item).toFile().isDirectory()) {
                clientDirectory = clientDirectory.resolve(item);
            }
            updateView(clientDirectory);
        }
    }

    private void serverViewHandler(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            String item = serverView.getSelectionModel().getSelectedItem();
            if ("...".equals(item)) {
                network.send(new ListRequestMessage(serverPath.getText(), ".."));
            } else if (item != null) {
                network.send(new ListRequestMessage(serverPath.getText(), item));
            }
        }
    }

    public Path getClientDirectory() {
        return clientDirectory;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network(this);
        clientDirectory = Paths.get("ClientDirectory").toAbsolutePath();
        clientView.setOnMouseClicked(this::clientViewHandler);
        serverView.setOnMouseClicked(this::serverViewHandler);
        Thread networkThread = new Thread(network);
        networkThread.start();
        updateView(clientDirectory);
    }
}