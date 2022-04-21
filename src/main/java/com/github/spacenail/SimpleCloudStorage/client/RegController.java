package com.github.spacenail.SimpleCloudStorage.client;

import com.github.spacenail.SimpleCloudStorage.model.RegRequest;
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

public class RegController implements Initializable {
    @FXML
    private TextField login;
    @FXML
    private TextField password;
    @FXML
    private Label existUser;
    private Network network;
    private AuthController authController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void close() {
        Platform.exit();
    }

    @FXML
    private void createUserButton() {
        network.send(
                new RegRequest(login.getText(), password.getText())
        );
    }

    @FXML
    private void cancelButton() {
        closeWindow();
    }

    void closeWindow() {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("auth.fxml"));
            loader.setController(authController);
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

    public void showErrorMessage(String message) {
        existUser.setText(message);
    }

    void setNetwork(Network network) {
        this.network = network;
    }

    void setAuthController(AuthController authController) {
        this.authController = authController;
    }
}
