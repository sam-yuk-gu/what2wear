package com.samyukgu.what2wear.member.controller;

import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.model.Member;
import java.io.IOException;

import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.region.Session.RegionWeatherSession;
import com.samyukgu.what2wear.region.model.Region;
import com.samyukgu.what2wear.weather.model.Weather;
import com.samyukgu.what2wear.weather.service.WeatherService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class LoginController {
    @FXML private StackPane root;
    @FXML private static LoginController instance;
    @FXML private TextField inputIdField;
    @FXML private PasswordField inputPasswordField;
    @FXML private Button loginButton;
    @FXML private Label signupLabel;
    @FXML private Label findIdLabel;
    @FXML private Label findPasswordLabel;
    @FXML ImageView loginBanner;

    private MemberService memberService;
    private MemberSession memberSession;
    private RegionWeatherSession  regionWeatherSession;
    private WeatherService  weatherService;

     // 초기화할때 UI 및 DI Container 세팅
    @FXML
    public void initialize(){
        setupUI();
        setupDI();
    }

     // 로그인 버튼 클릭시 회원 검증 후 메인 페이지 이동
    @FXML
    private void handleClickLoginButton(){
        Member member = memberService.login(inputIdField.getText(), inputPasswordField.getText());

        if(member!=null){
            memberSession.setMember(member);

            Region defaultRegion = new Region(1L, "서울특별시", "", 60L, 127L);
            regionWeatherSession.setRegion(defaultRegion);
            Weather weather = weatherService.fetchWeatherFromApi(defaultRegion.getNx().intValue(), defaultRegion.getNy().intValue());
            RegionWeatherSession.setWeather(weather);

            switchScene("/com/samyukgu/what2wear/layout/MainLayout.fxml", "내일 뭐 입지?");
        }else{
            showConfirmationModal();
        }
    }

    // 회원가입 버튼 클릭시 회원가입 scene 이동
    @FXML
    private void handleClickSignupLabel(){
        switchScene("/com/samyukgu/what2wear/member/SignupStep1View.fxml", "회원가입");
    }

    // id찾기 버튼 클릭시 회원가입 scene 이동
    @FXML
    private void handleClickFindIdLabel(){
        switchScene("/com/samyukgu/what2wear/member/FindAccountIdStep1View.fxml", "아이디 찾기");
    }

    // 비밀번호 찾기 버튼 클릭시 회원가입 scene 이동
    @FXML
    private void handleClickFindPasswordLabel(){
        switchScene("/com/samyukgu/what2wear/member/FindPasswordStep1View.fxml", "아이디 찾기");
    }

    // 컨테이너에 있는 인스턴스 멤버로 할당
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
        regionWeatherSession = diContainer.resolve(RegionWeatherSession.class);
        weatherService = diContainer.resolve(WeatherService.class);
    }

    // 초기 로그인 배너 크기 및 모서리 처리
    private void setupUI() {
        int arcWidth = 30, arcHeight = 30;
        Rectangle clip = new Rectangle(loginBanner.getFitWidth(), loginBanner.getFitHeight());
        clip.setArcWidth(arcWidth);
        clip.setArcHeight(arcHeight);
        loginBanner.setClip(clip);
    }

    // scene 이동 메서드
    private void switchScene(String fxmlPath, String title){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) loginBanner.getScene().getWindow();
            Scene scene = new Scene(root, 1280, 768);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showConfirmationModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "로그인 실패",
                    "아이디와 비밀번호를 다시 확인해주세요.",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "확인",
                    () -> root.getChildren().remove(modal)
            );

            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
