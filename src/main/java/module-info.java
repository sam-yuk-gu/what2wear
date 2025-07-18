module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.samyukgu.what2wear.codi.controller to javafx.fxml;
    opens com.samyukgu.what2wear to javafx.fxml;
    exports com.samyukgu.what2wear;
}