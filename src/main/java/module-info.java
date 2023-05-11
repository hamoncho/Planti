module com.mctrio.planti {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;
    requires bluecove;

    opens com.mctrio.planti to javafx.fxml;
    exports com.mctrio.planti;
}
