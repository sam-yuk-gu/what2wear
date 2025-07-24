package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.ai.service.OpenAiChatService;
import com.samyukgu.what2wear.ai.service.WeatherAiService;
import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.config.ConfigUtil;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecommendAiController {

    @FXML private StackPane root;
    @FXML private Button recommendBtn;

    // 텍스트 영역
    @FXML private Label introLabel1;
    @FXML private Label introLabel2;
    @FXML private Label introLabel3;
    @FXML private Label introLabel4;
    @FXML private Label introLabel5;

    @FXML private Label recommendLabel;
    @FXML private Label topLabel;
    @FXML private Label bottomLabel;
    @FXML private Label shoesLabel;
    @FXML private Label accLabel;

    @FXML private Label endingLabel1;
    @FXML private Label endingLabel2;
    @FXML private Label endingLabel3;

    private final OpenAiChatService aiService = new OpenAiChatService(ConfigUtil.get("openai.api.key"));
    private final WeatherAiService weatherService = new WeatherAiService();

    private String location;
    private String purpose;

    @FXML
    public void initialize() {
        this.location = IntroduceAiController.selectedLocation;
        this.purpose = IntroduceAiController.selectedPurpose;

        if (location == null || purpose == null || location.isBlank() || purpose.isBlank()) {
            recommendLabel.setText("추천을 위한 정보가 부족해요.");
            return;
        }

        introLabel1.setText("안녕하세요!");
        recommendLabel.setText("AI가 코디를 준비 중이에요...");

        // 날씨 정보 받아오기
        new Thread(() -> {
            try {
                var weatherInfo = weatherService.getWeatherInfo(location);
                String high = weatherInfo.get("high");
                String low = weatherInfo.get("low");
                String feel = weatherInfo.get("sensible");
                String uv = weatherInfo.get("uv");

                Platform.runLater(() -> {
                    introLabel2.setText("안녕하세요!");
                    introLabel2.setText(purpose + "할 때 입을 옷을 추천해드릴게요 :)");
                    introLabel3.setText("오늘 " + location + "의 날씨 정보는");
                    introLabel4.setText("최고기온은 " + high + "°C, " + "최저기온은 " + low + "°C입니다.");
                    introLabel5.setText("자외선 지수는 높음으로 외출 시 주의해주세요.");     // 추후 공공데이터포털의 "자외선지수예보조회" API를 사용

                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    introLabel1.setText("오늘 " + location + "은 덥고 습한 날씨가 예상돼요.");
                    introLabel2.setText(purpose + "할 때 입을 옷을 함께 고민해드릴게요.");
                    introLabel3.setText("최고 기온은 33도, 자외선 지수는 높을 예정이에요.");
                    introLabel4.setText("자외선 지수는 높음으로 외출 시 주의해주세요.");
                });
            }
        }).start();

        // OpenAI 프롬프트 생성 및 요청
        String prompt = String.format(
                "내일 %s에서 %s할 예정입니다. 날씨에 어울리는 옷을 아래와 같은 정확한 형식으로 추천해줘. 절대 다른 말 하지 말고 형식을 꼭 지켜줘.\n" +
                        "형식:\n상의: (여기에 상의 옷)\n하의: (여기에 하의 옷)\n신발: (여기에 신발)\n악세사리: (여기에 악세사리)\n",
                location, purpose
        );

        new Thread(() -> {
            try {
                String aiResponse = aiService.getOutfitRecommendation(location, purpose);
                applyAiRecommendation(aiResponse);
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    recommendLabel.setText("AI 응답 실패 😥");
                    topLabel.setText("· 상의: 없음");
                    bottomLabel.setText("· 하의: 없음");
                    shoesLabel.setText("· 신발: 없음");
                    accLabel.setText("· 악세사리: 없음");
                });
            }
        }).start();

        // 하단 마무리 멘트
        endingLabel1.setText("더위에 대비해");
        endingLabel2.setText("쿨링 재킷이나 모자를 챙기면 좋아요.");
        endingLabel3.setText("오늘도 멋진 하루 보내세요!");
    }

    private void applyAiRecommendation(String response) {
        System.out.println("[GPT 응답 결과] \n" + response);

        Pattern pattern = Pattern.compile(
                "상의:\\s*(.*?)\\s*\\n" +
                        "하의:\\s*(.*?)\\s*\\n" +
                        "신발:\\s*(.*?)\\s*\\n" +
                        "(?:악세사리|악세서리|액세서리):\\s*(.*)",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            String top = matcher.group(1).trim();
            String bottom = matcher.group(2).trim();
            String shoes = matcher.group(3).trim();
            String acc = matcher.group(4).trim();

            Platform.runLater(() -> {
                topLabel.setText("· 상의: " + top);
                bottomLabel.setText("· 하의: " + bottom);
                shoesLabel.setText("· 신발: " + shoes);
                accLabel.setText("· 악세사리: " + acc);
                recommendLabel.setText("회원님 옷 중 이런 코디는 어떠세요?");
            });
        } else {
            Platform.runLater(() -> {
                recommendLabel.setText("AI 응답 포맷을 이해하지 못했어요.");
                topLabel.setText("· 상의: (확인 필요)");
                bottomLabel.setText("· 하의: (확인 필요)");
                shoesLabel.setText("· 신발: (확인 필요)");
                accLabel.setText("· 악세사리: (확인 필요)");
            });
        }
    }

    @FXML
    private void handleRetryClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/ai/IntroduceAi.fxml");
    }

    @FXML
    private void handleSaveClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
        StackPane modal = loader.load();

        CustomModalController controller = loader.getController();
        controller.configure(
                "코디 저장 완료",
                "정상적으로 코디가 저장되었습니다.",
                "/assets/icons/greenCheck.png",
                "#79C998",
                "취소",
                "확인",
                () -> root.getChildren().remove(modal),
                () -> {
                    root.getChildren().remove(modal);
                    MainLayoutController.loadView("/com/samyukgu/what2wear/ai/LoadingAi.fxml");
                }
        );

        root.getChildren().add(modal);
    }
}
