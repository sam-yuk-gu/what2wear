module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires jakarta.mail;
    requires java.desktop;
    requires lombok;


    opens com.samyukgu.what2wear to javafx.fxml;
    opens com.samyukgu.what2wear.post.controller to javafx.fxml;
    opens com.samyukgu.what2wear.codi.controller to javafx.fxml;
    opens com.samyukgu.what2wear.common.controller to javafx.fxml;
    opens com.samyukgu.what2wear.member.controller to javafx.fxml;
    opens com.samyukgu.what2wear.wardrobe.controller to javafx.fxml;

    exports com.samyukgu.what2wear;
}