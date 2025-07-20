module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires jakarta.mail;

    opens com.samyukgu.what2wear to javafx.fxml;
    opens com.samyukgu.what2wear.common.controller to javafx.fxml;
    opens com.samyukgu.what2wear.codi.controller to javafx.fxml;
    opens com.samyukgu.what2wear.member.controller to javafx.fxml;

    exports com.samyukgu.what2wear;
}