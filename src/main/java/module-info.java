module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.samyukgu.what2wear to javafx.fxml;
    opens com.samyukgu.what2wear.post.controller to javafx.fxml;
    exports com.samyukgu.what2wear;
}