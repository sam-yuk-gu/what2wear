module com.samyukgu.what2wear {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires jakarta.mail;
    requires java.desktop;
    requires lombok;
    requires okhttp3;
//    requires gradle.api;  // requires로 불러오는 대상 아니라서 주석 처리
    requires com.fasterxml.jackson.databind;    // jackson.databind 모듈 오류 해결을 위해 추가
    requires java.net.http; // http 추가

    opens com.samyukgu.what2wear to javafx.fxml;
    opens com.samyukgu.what2wear.post.controller to javafx.fxml;
    opens com.samyukgu.what2wear.codi.controller to javafx.fxml;
    opens com.samyukgu.what2wear.common.controller to javafx.fxml;
    opens com.samyukgu.what2wear.member.controller to javafx.fxml;
    opens com.samyukgu.what2wear.wardrobe.controller to javafx.fxml;
    opens com.samyukgu.what2wear.friend.controller to javafx.fxml;
    opens com.samyukgu.what2wear.notification.controller to javafx.fxml;

    exports com.samyukgu.what2wear;
    opens com.samyukgu.what2wear.layout.controller to javafx.fxml;
    opens com.samyukgu.what2wear.ai.service to com.fasterxml.jackson.databind; // jackson.databind 모듈 오류 해결을 위해 추가
}