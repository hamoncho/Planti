package com.mctrio.planti;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.fazecast.jSerialComm.*;
import javax.bluetooth.*;
import java.io.IOException;
import java.util.Arrays;
import javax.bluetooth.*;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        System.err.println(Arrays.toString(SerialPort.getCommPorts()));
        System.out.println(LocalDevice.isPowerOn());
        discoverDevices();
        
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        System.out.print(LocalDevice.isPowerOn());
        launch();
    }

    public void discoverDevices() {
        try {
            LocalDevice dev = LocalDevice.getLocalDevice();
            String mac = dev.getBluetoothAddress();
            System.out.println("Address:" + mac);
            DiscoveryAgent agent = dev.getDiscoveryAgent();
            System.out.println(dev.getFriendlyName());
        } catch (Exception e) {
            //log.error("MEK", e);
        }
    }
}
