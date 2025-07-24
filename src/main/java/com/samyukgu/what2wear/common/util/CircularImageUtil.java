package com.samyukgu.what2wear.common.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;

/**
 * 원형 프로필 이미지를 생성하는 유틸리티 클래스
 */
public class CircularImageUtil {

    /**
     * byte 배열로부터 원형 프로필 이미지를 생성합니다.
     *
     * @param size 이미지 크기 (width, height 동일)
     * @param imageBytes 이미지 데이터
     * @return 원형으로 클립된 ImageView
     */
    public static ImageView createCircularImageFromBytes(double size, byte[] imageBytes) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false); // 정확한 원형을 위해 비율 유지 해제

        try {
            if (imageBytes != null && imageBytes.length > 0) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                Image image = new Image(inputStream);
                imageView.setImage(image);
                inputStream.close();
            } else {
                // 기본 이미지 설정
                setDefaultImage(imageView);
            }
        } catch (Exception e) {
            System.err.println("이미지 로드 실패, 기본 이미지 사용: " + e.getMessage());
            setDefaultImage(imageView);
        }

        // 원형 클립 적용
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);

        return imageView;
    }

    /**
     * 이미지 경로로부터 원형 프로필 이미지를 생성합니다.
     *
     * @param size 이미지 크기 (width, height 동일)
     * @param imagePath 이미지 파일 경로
     * @return 원형으로 클립된 ImageView
     */
    public static ImageView createCircularImageFromPath(double size, String imagePath) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);

        try {
            Image image = new Image(imagePath);
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("이미지 로드 실패, 기본 이미지 사용: " + e.getMessage());
            setDefaultImage(imageView);
        }

        // 원형 클립 적용
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);

        return imageView;
    }

    /**
     * Image 객체로부터 원형 프로필 이미지를 생성합니다.
     *
     * @param size 이미지 크기 (width, height 동일)
     * @param image Image 객체
     * @return 원형으로 클립된 ImageView
     */
    public static ImageView createCircularImageFromImage(double size, Image image) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);

        if (image != null) {
            imageView.setImage(image);
        } else {
            setDefaultImage(imageView);
        }

        // 원형 클립 적용
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);

        return imageView;
    }

    /**
     * 기존 ImageView를 원형으로 변환합니다.
     *
     * @param imageView 변환할 ImageView
     * @param size 원형 크기
     */
    public static void makeImageViewCircular(ImageView imageView, double size) {
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(false);

        // 원형 클립 적용
        Circle clip = new Circle(size / 2, size / 2, size / 2);
        imageView.setClip(clip);
    }

    /**
     * 테두리가 있는 원형 프로필 이미지를 생성합니다.
     *
     * @param size 이미지 크기
     * @param imageBytes 이미지 데이터
     * @param borderWidth 테두리 두께
     * @param borderColor 테두리 색상 (CSS 색상 코드)
     * @return 테두리가 있는 원형 ImageView
     */
    public static ImageView createCircularImageWithBorder(double size, byte[] imageBytes,
                                                          double borderWidth, String borderColor) {
        ImageView imageView = createCircularImageFromBytes(size - (borderWidth * 2), imageBytes);

        // 테두리 스타일 적용
        String style = String.format(
                "-fx-border-radius: %fpx; -fx-border-width: %fpx; -fx-border-color: %s;",
                size / 2, borderWidth, borderColor
        );
        imageView.setStyle(style);

        return imageView;
    }

    /**
     * 기본 프로필 이미지를 설정합니다.
     *
     * @param imageView 이미지를 설정할 ImageView
     */
    private static void setDefaultImage(ImageView imageView) {
        try {
            String defaultImagePath = CircularImageUtil.class.getResource("/assets/icons/defaultProfile.png").toExternalForm();
            Image defaultImage = new Image(defaultImagePath);
            imageView.setImage(defaultImage);
        } catch (Exception ex) {
            System.err.println("기본 이미지도 로드 실패");
            // 완전히 실패한 경우를 위한 폴백 처리
            imageView.setStyle("-fx-background-color: #cccccc;");
        }
    }

    // CircularImageUtil에 추가
    public static void applyCircularImageToExistingImageView(ImageView imageView, double size, byte[] imageBytes) {
        if (imageBytes != null && imageBytes.length > 0) {
            try {
                // 바이트 배열을 Image로 변환
                ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
                Image image = new Image(bis);

                // ImageView 크기 설정
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
                imageView.setPreserveRatio(false);

                // 원형 클립 적용
                Circle clip = new Circle(size / 2);
                clip.setCenterX(size / 2);
                clip.setCenterY(size / 2);
                imageView.setClip(clip);

                // 이미지 설정
                imageView.setImage(image);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            setDefaultImage(imageView);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(false);

            // 원형 클립 적용
            Circle clip = new Circle(size / 2);
            clip.setCenterX(size / 2);
            clip.setCenterY(size / 2);
            imageView.setClip(clip);

        }
    }
}