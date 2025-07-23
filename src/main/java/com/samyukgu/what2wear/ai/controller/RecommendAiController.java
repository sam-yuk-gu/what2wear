package com.samyukgu.what2wear.ai.controller;

import com.samyukgu.what2wear.ai.service.OpenAiChatService;
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
    private String location;
    private String purpose;

    @FXML
    public void initialize() {
        this.location = IntroduceAiController.selectedLocation;
        this.purpose = IntroduceAiController.selectedPurpose;


        if (location == null || purpose == null) {
            recommendLabel.setText("추천을 위한 정보가 부족해요.");
            return;
        }

        // 상단 멘트
        introLabel1.setText("안녕하세요!");
        introLabel2.setText("내일 " + location + "은 덥고 습한 날씨가 예상돼요.");
        introLabel3.setText(purpose + "할 때 입을 옷을 함께 고민해드릴게요.");
        introLabel4.setText("최고 기온은 33도, 자외선 지수는 높을 예정이에요.");
        introLabel5.setText("시원하고 통기성 좋은 옷차림을 추천해요!");

        recommendLabel.setText("AI가 코디를 준비 중이에요...");

        // AI 요청 보내기 (비동기)
        String prompt = String.format(
                "내일 %s에서 %s할 예정입니다. 날씨에 어울리는 옷을 다음 형식에 맞춰 추천해줘.\n" +
                        "형식은 반드시 아래와 같이 해줘:\n" +
                        "상의: \n하의: \n신발: \n악세사리:",
                location, purpose
        );

        String finalLocation = location;
        String finalPurpose = purpose;
        new Thread(() -> {
            try {
                String aiResponse = aiService.getOutfitRecommendation(finalLocation, finalPurpose);
                applyAiRecommendation(aiResponse);
            } catch (IOException e) {
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
        // 형식: 상의: OOO\n하의: OOO\n신발: OOO\n악세사리: OOO
        Pattern pattern = Pattern.compile("상의: (.*?)\\n하의: (.*?)\\n신발: (.*?)\\n악세사리: (.*)");
        Matcher matcher = pattern.matcher(response);

        Platform.runLater(() -> {
            if (matcher.find()) {
                topLabel.setText("· 상의: " + matcher.group(1));
                bottomLabel.setText("· 하의: " + matcher.group(2));
                shoesLabel.setText("· 신발: " + matcher.group(3));
                accLabel.setText("· 악세사리: " + matcher.group(4));
                recommendLabel.setText("회원님 옷 중 이런 코디는 어떠세요?");
            } else {
                recommendLabel.setText("AI 응답 형식을 인식하지 못했어요.");
            }
        });
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
