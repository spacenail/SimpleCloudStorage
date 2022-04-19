package com.github.spacenail.SimpleCloudStorage.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void close() {
    }

    public void createUserButton() {
    }

    public void cancelButton() {
    }

    public void initNetwork(Network network) {
        this.network = network;
    }

    public void closeNetwork(){
        network.close();
    }
}
