package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.AuthRequest;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private Network network;
    private AuthHandler authHandler;

    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Label failAuth;

    public void close() {
        network.close();
        Platform.exit();
    }


    public void logInButton() {
        network.send(new AuthRequest(
                login.getText(),
                password.getText())
        );
        login.clear();
        password.clear();
    }

    public void auth(boolean isAuth) {
        if (isAuth) {
            changeWindow();
        } else {
            Platform.runLater(() -> failAuth.setText("Bad credentials"));
        }
    }

    private void closeNetwork(WindowEvent event) {
        network.close();
    }

    private void changeWindow() {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("navigator.fxml"));
            Stage stage = (Stage) failAuth.getScene().getWindow();
            ClientController clientController = loader.getController();
            clientController.setNetwork(network);
            ClientHandler clientHandler = new ClientHandler();
            clientHandler.setClientController(clientController);
            network.deleteHandler(authHandler);
            network.addHandler(clientHandler);

            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.show();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Stage stage = (Stage) failAuth.getScene().getWindow();
        stage.setOnCloseRequest(this::closeNetwork);
        network = new Network();
        authHandler = new AuthHandler();
        authHandler.setAuthController(this);
        network.addHandler(authHandler);
        Thread networkThread = new Thread(network);
        networkThread.start();
    }
}
