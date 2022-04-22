package com.github.spacenail.SimpleCloudStorage.client;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class Client extends Application {
    private AuthController authController;
    private MainController mainController;
    private RegController regController;
    private Network network;

    @Override
    public void init() throws Exception {
        mainController = new MainController();
        regController = new RegController();
        authController = new AuthController(mainController, regController);
        network = new Network(
                new ClientHandler(mainController, authController, regController)
        );
        mainController.setNetwork(network);
        regController.setNetwork(network);
        authController.setNetwork(network);
        Thread networkThread = new Thread(network);
        networkThread.start();
    }

    @Override
    public void stop() throws Exception {
        network.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("auth.fxml"));
        loader.setController(authController);
        Parent parent = loader.load();
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("SimpleCloudStorage");
        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(
                        Client.class.getResourceAsStream("cloud.png"))));
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}