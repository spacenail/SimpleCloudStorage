package com.github.spacenail.SimpleCloudStorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Client extends Application {
    AuthController authController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth.fxml"));
        authController = loader.getController();
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("SimpleCloudStorage");
        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(
                        Client.class.getResourceAsStream("cloud.png"))));
        primaryStage.setOnCloseRequest(e->{authController.closeNetwork();});
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}