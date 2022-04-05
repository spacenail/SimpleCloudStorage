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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    private Network network;

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

    public void closeNetwork() {
        network.close();
    }

    private void changeWindow() {
        Platform.runLater(()->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("navigator.fxml"));
            Stage stage = (Stage) login.getScene().getWindow();
        try {
            stage.setScene(
                    new Scene(
                            loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClientController clientController = loader.getController();
        clientController.initNetwork(network);
        stage.show();
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network(this);
        Thread networkThread = new Thread(network);
        networkThread.start();
    }
}
