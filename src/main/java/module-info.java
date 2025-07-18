module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.samyukgu.what2wear to javafx.fxml;
    exports com.samyukgu.what2wear;
}