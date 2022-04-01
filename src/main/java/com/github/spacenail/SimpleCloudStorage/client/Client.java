package com.github.spacenail.SimpleCloudStorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
       Parent parent = FXMLLoader.load(
               Objects.requireNonNull(getClass().getResource("layout.fxml"))
        );
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("SimpleCloudStorage");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(Client.class.getResourceAsStream("cloud.png"))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}