module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.samyukgu.what2wear to javafx.fxml;
    opens com.samyukgu.what2wear.wardrobe to javafx.fxml;
    exports com.samyukgu.what2wear;
    opens com.samyukgu.what2wear.wardrobe.controller to javafx.fxml;
}