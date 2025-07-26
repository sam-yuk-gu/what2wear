package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.post.service.PostService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class EditPostController {

    @FXML private StackPane root;
    @FXML private VBox mainVBox;
    @FXML private Button cancelButton;
    @FXML private Button confirmButton;
    @FXML private TextField input_title;
    @FXML private TextArea content_input;

    private PostService postService;
    private Post post;

    public void initialize() {
        this.postService = DIContainer.getInstance().resolve(PostService.class);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/BasicHeader.fxml"));
            Parent header = loader.load();
            BasicHeaderController controller = loader.getController();
            controller.setTitle("게시글 상세");
            controller.setOnBackAction(() -> {
                try {
                    Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/post/ListPost.fxml")));
                    root.getChildren().setAll(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            mainVBox.getChildren().add(0, header); // St
        } catch (Exception e) {
            e.printStackTrace();
        }

        cancelButton.setOnAction(e -> handleCancelClick());
        confirmButton.setOnAction(e -> handleConfirmClick());
    }

    private void handleCancelClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
    }

    private void handleConfirmClick() {
        try {
            String updatedTitle = input_title.getText().trim();
            String updatedContent = content_input.getText().trim();

            if (updatedTitle.isEmpty() || updatedContent.isEmpty()) {
                showCustomModal("입력 오류", "제목과 내용을 모두 입력해주세요.", "/assets/icons/redCheck.png", "#FA7B7F");
                return;
            }

            post.setTitle(updatedTitle);
            post.setContent(updatedContent);

            postService.updatePost(post);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글 수정 완료",
                    "정상적으로 게시글이 수정되었습니다.",
                    "/assets/icons/greenCheck.png",
                    "#4CAF50",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        root.getChildren().remove(modal);
                        System.out.println("Updated Title: " + post.getTitle());
                        System.out.println("Updated Content: " + post.getContent());

                        // 수정 완료 후 상세 페이지로 이동
                        MainLayoutController.loadPostDetailView(post);
                    }
            );
            root.getChildren().add(modal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomModal(String title, String message, String iconPath, String color) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    title,
                    message,
                    iconPath,
                    color,
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> root.getChildren().remove(modal)
            );

            root.getChildren().add(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPostData(Post post) {
        this.post = post;

        if (post != null) {
            input_title.setText(post.getTitle());
            content_input.setText(post.getContent());
        }
    }
}
