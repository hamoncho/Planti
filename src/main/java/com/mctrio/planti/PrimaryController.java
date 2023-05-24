package com.mctrio.planti;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;

public class PrimaryController {

    @FXML
    private ProgressBar progressBarTemperatura;
    @FXML
    private ProgressBar progressBarHumedad;
    @FXML
    private ProgressBar progressBarHumedadAmb;
    @FXML
    private Button primaryButton;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void onActionLuz(ActionEvent event) {
    }

    @FXML
    private void onActionFan(ActionEvent event) {
    }
}
