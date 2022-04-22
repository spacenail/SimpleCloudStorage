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
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Label failAuth;
    private MainController mainController;
    private RegController regController;
    private Network network;

    public AuthController(MainController mainController, RegController regController) {
        this.mainController = mainController;
        this.regController = regController;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    @FXML
    private void close() {
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
            openMainWindow();
        } else {
            Platform.runLater(() -> failAuth.setText("Bad credentials"));
        }
    }


    private void openMainWindow() {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            loader.setController(mainController);
            Stage stage = (Stage) login.getScene().getWindow();
            try {
                stage.setScene(
                        new Scene(
                                loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            stage.show();
            network.send(new ListRequestMessage()); //first request for show server catalog
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void signUpButton() {
        openRegWindow();
    }

    private void openRegWindow() {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
            loader.setController(regController);
            regController.setAuthController(this);
            Stage stage = (Stage) login.getScene().getWindow();
            try {
                stage.setScene(
                        new Scene(
                                loader.load()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.show();
        });
    }
}
