package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.common.controller.CustomModalController;
import com.samyukgu.what2wear.common.util.CircularImageUtil;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.member.Session.MemberSession;
import com.samyukgu.what2wear.member.service.MemberService;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.post.service.PostService;
import com.samyukgu.what2wear.postcomment.controller.CommentItemController;
import com.samyukgu.what2wear.postcomment.dao.PostCommentDAO;
import com.samyukgu.what2wear.postcomment.model.PostComment;
import com.samyukgu.what2wear.postcomment.service.PostCommentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

// 작성자 : 오수경
public class DetailPostController {

    @FXML private StackPane root;
    @FXML private Label titleLabel;
    @FXML private Label contentLabel;
    @FXML private Label authorLabel;
    @FXML private Label dateLabel;
    @FXML private Button likeButton;
    @FXML private ImageView likeIcon;
    @FXML private Label likesLabel;
    @FXML private VBox comment_vbox;
    @FXML private Button editPostButton;
    @FXML private Button deletePostButton;
    @FXML private TextField commentField;
    @FXML private ImageView profileImg;
    @FXML private VBox container;
    @FXML private Label commentCountLabel;

    // 회원 세션
    private MemberService memberService;
    private MemberSession memberSession;

    private Long postId;
    private int likeCount;
    private boolean isLiked = false;
    private Post currentPost;
    private final PostCommentService commentService = new PostCommentService();

    @FXML
    private void initialize() {
        // 회원 정보 불러오기
        setupDI();
        hideEditDeleteButtons();

        // 헤더 동적 삽입
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

            container.getChildren().add(0, header); // StackPane 맨 위에 삽입
        } catch (IOException e) {
            e.printStackTrace();
        }

        editPostButton.setOnAction(event -> handlePostEditClick());
        deletePostButton.setOnAction(event -> handlePostDeleteClick());

        likeButton.setOnMouseEntered(e -> likeButton.setStyle("-fx-scale-x: 1.1; -fx-scale-y: 1.1;"));
        likeButton.setOnMouseExited(e -> likeButton.setStyle("-fx-scale-x: 1.0; -fx-scale-y: 1.0;"));
    }

    // 회원 정보 불러오기
    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        memberService = diContainer.resolve(MemberService.class);
        memberSession = diContainer.resolve(MemberSession.class);
    }

    public void setPostData(Post post) {
        this.currentPost = post;
        this.postId = post.getId(); // postId 저장
        this.isLiked = post.isLiked();  // 초기 좋아요 상태 가져오기
        this.likeCount = post.getLike_count(); // 초기 좋아요 수 가져오기

        displayPostContent(post);
        checkAndShowButtons(post);
        loadComments();

        updateCommentCountLabel(postId);    // 댓글 갯수 카운트

        updateLikeIcon();   // 초기 좋아요 아이콘 설정
    }

    // 댓글 조회하기
    private void loadComments() {
        if (currentPost == null) {
            System.err.println("loadComments: currentPost가 null입니다.");
            return;
        }

        PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
        List<PostComment> comments = commentDAO.findByPostId(currentPost.getId());

        comment_vbox.getChildren().removeIf(node -> node instanceof HBox);

        // 댓글 갯수
        for (PostComment comment : comments) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/postcomment/CommentItem.fxml"));
                Parent commentItem = loader.load();

                CommentItemController controller = loader.getController();
                controller.setComment(
                        comment.getId(),
                        comment.getCreatedAt().toString(),
                        comment.getContent().toString(),
                        comment.getMemberId()
                );

                comment_vbox.getChildren().add(commentItem);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void displayPostContent(Post post) {
        byte[] imgByte = memberService.getMember(post.getMember_id()).getProfile_img();
        CircularImageUtil.applyCircularImageToExistingImageView(profileImg, 48.0, imgByte);
        titleLabel.setText(post.getTitle());
        contentLabel.setText(post.getContent());
        authorLabel.setText(post.getWriter_name());
        dateLabel.setText(post.getCreate_at().toString());
        likesLabel.setText(String.valueOf(post.getLike_count()));
    }

    /*
     * 게시글 수정하기/삭제하기 버튼 유무 확인
     * 작성자와 로그인한 유저의 아이디가 동일할 경우 보임
     */
    private void checkAndShowButtons(Post post) {
        if (post.getMember_id() != null && post.getMember_id() == memberSession.getMember().getId()) {
            showEditDeleteButtons();
        } else {
            hideEditDeleteButtons();
        }
    }

    // 게시글 수정하기/삭제하기 버튼 안 숨김 처리
    private void showEditDeleteButtons() {
        editPostButton.setVisible(true);
        deletePostButton.setVisible(true);
        editPostButton.setManaged(true);
        deletePostButton.setManaged(true);
    }

    // 게시글 수정하기/삭제하기 버튼 숨김 처리
    private void hideEditDeleteButtons() {
        editPostButton.setVisible(false);
        deletePostButton.setVisible(false);
        editPostButton.setManaged(false);
        deletePostButton.setManaged(false);
    }

    // 내가 쓴 게시글 수정하기 버튼 클릭 시
    public void handlePostEditClick() {
        // 데이터 불러와서 수정 화면으로 전환
        MainLayoutController.loadEditPostView(currentPost);
    }


    // 내가 쓴 게시글 삭제하기 버튼 클릭 시
    public void handlePostDeleteClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/CustomModal.fxml"));
            StackPane modal = loader.load();

            CustomModalController controller = loader.getController();
            controller.configure(
                    "게시글을 삭제하시겠습니까?",
                    "삭제된 게시글 정보는 저장되지 않습니다.",
                    "/assets/icons/redCheck.png",
                    "#FA7B7F",
                    "취소",
                    "확인",
                    () -> root.getChildren().remove(modal),
                    () -> {
                        PostService postService = DIContainer.getInstance().resolve(PostService.class);
                        postService.deletePost(currentPost.getId());
                        root.getChildren().remove(modal);
                        MainLayoutController.loadView("/com/samyukgu/what2wear/post/ListPost.fxml");
                    }
            );

            root.getChildren().add(modal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 게시글 등록하기 모달창에서 확인 버튼 클릭 시
    @FXML
    private void handleRegisterCommentClick() {
        String commentContent = commentField.getText().trim();
        if (commentContent.isEmpty()) return;

        Long currentUserId = memberSession.getMember().getId();
        Long postId = currentPost.getId();

        PostComment newComment = new PostComment(
                null,
                postId,
                currentUserId,
                commentContent,
                new Date()
        );

        PostCommentDAO commentDAO = DIContainer.getInstance().resolve(PostCommentDAO.class);
        commentDAO.create(newComment);
        commentField.clear();
        addCommentToUI(newComment);
        updateCommentCountLabel(currentPost.getId());   //  댓글 등록 후 댓글 수 갱신
    }

    private void updateCommentCountLabel(Long postId) {
        int count = commentService.countByPostId(postId);
        commentCountLabel.setText("댓글(" + count + ")");
    }

    // 댓글 추가 후 바로 업데이트
    private void addCommentToUI(PostComment comment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/postcomment/CommentItem.fxml"));
            Parent commentItem = loader.load();

            CommentItemController controller = loader.getController();
            controller.setComment(
                    comment.getId(),
                    comment.getCreatedAt().toString(),
                    comment.getContent().toString(),
                    comment.getMemberId()
            );
            comment_vbox.getChildren().add(commentItem);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 좋아요 아이콘 UI 업데이트
    private void updateLikeIcon() {
        String iconPath = !isLiked
                ? "/assets/icons/emptyHeart.png"
                : "/assets/icons/fillHeart.png";
        likeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
    }

    // 좋아요 클릭 시 색상 및 숫자 변화
    @FXML
    public void handleLikeButtonClick(ActionEvent actionEvent) {
        if (currentPost == null || memberSession.getMember() == null) return;

        Long postId = currentPost.getId();
        Long memberId = memberSession.getMember().getId();

        // 상태 전환
        isLiked = !isLiked; // 초기값: false

        // 좋아요 클릭 시 1 증가
        if (isLiked) {
            likeCount++;
        } else {    // 그렇지 않은 경우 1 감소
            likeCount--;
        }

        // UI 반영
        updateLikeIcon();
        likesLabel.setText(String.valueOf(likeCount));

        // DB 반영
        PostService postService = DIContainer.getInstance().resolve(PostService.class);
        postService.likePost(postId, memberId);

        // Post 객체에도 업데이트
        currentPost.setLike_count(likeCount);
        currentPost.setLiked(isLiked);
    }
}