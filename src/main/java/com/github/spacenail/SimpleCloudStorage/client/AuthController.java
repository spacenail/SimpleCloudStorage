package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.AuthRequest;
import com.github.spacenail.SimpleCloudStorage.model.ListRequestMessage;
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

    @FXML
    private void close() {
        closeNetwork();
        Platform.exit();
    }

    @FXML
    private void logInButton() {
        network.send(new AuthRequest(
                login.getText(),
                password.getText())
        );
        login.clear();
        password.clear();
    }

    void auth(boolean isAuth) {
        if (isAuth) {
            openManagerWindow();
        } else {
            Platform.runLater(() -> failAuth.setText("Bad credentials"));
        }
    }

    void closeNetwork() {
        network.close();
    }

    private void openManagerWindow() {
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
        stage.setOnCloseRequest(e->clientController.closeNetwork());
        stage.show();
        network.send(new ListRequestMessage()); //first request for show server catalog
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network(this);
        Thread networkThread = new Thread(network);
        networkThread.start();
    }

    @FXML
    private void signUpButton() {
        openRegWindow();
    }

    private void openRegWindow() {
        Platform.runLater(()->{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            Stage stage = (Stage) login.getScene().getWindow();
            try {
                stage.setScene(
                        new Scene(
                                loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            RegController regController = loader.getController();
            regController.initNetwork(network);
            stage.setOnCloseRequest(e->regController.closeNetwork());
            stage.show();
        });
    }
}
