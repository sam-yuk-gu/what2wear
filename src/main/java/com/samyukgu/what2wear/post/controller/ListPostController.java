package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.common.controller.MainLayoutController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.post.model.Post;
import com.samyukgu.what2wear.post.service.PostService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ListPostController implements Initializable {

    @FXML private TableView<Post> table_board;
    @FXML private TableColumn<Post, Long> colNo;
    @FXML private TableColumn<Post, String> colTitle;
    @FXML private TableColumn<Post, String> colAuthor;
    @FXML private TableColumn<Post, String> colDate;
    @FXML private TableColumn<Post, Integer> colLikes;

    @FXML private Button button_text;
    @FXML private Button double_left_button, left_button, right_button, double_right_button;
    @FXML private HBox page_button_box;

    private PostService postService;
    private List<Post> allPosts;
    private static final int ROWS_PER_PAGE = 10;
    private static final int MAX_PAGE_BUTTONS = 7;
    private int currentPage = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.postService = DIContainer.getInstance().resolve(PostService.class);
        allPosts = postService.getAllPosts();

        colNo.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("member_id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("create_at"));

        setupPagination();
        showPage(1);

        button_text.setOnAction(event -> handlePostClick());

        table_board.setRowFactory(tv -> {
            TableRow<Post> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openPostDetail(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupPagination() {
        int totalPages = (int) Math.ceil((double) allPosts.size() / ROWS_PER_PAGE);

        double_left_button.setOnAction(e -> showPage(1));
        left_button.setOnAction(e -> showPage(Math.max(1, currentPage - 1)));
        right_button.setOnAction(e -> showPage(Math.min(totalPages, currentPage + 1)));
        double_right_button.setOnAction(e -> showPage(totalPages));

        refreshPageButtons();
    }

    private void refreshPageButtons() {
        page_button_box.getChildren().clear();
        int totalPages = (int) Math.ceil((double) allPosts.size() / ROWS_PER_PAGE);

        int startPage = Math.max(1, currentPage - MAX_PAGE_BUTTONS / 2);
        int endPage = Math.min(totalPages, startPage + MAX_PAGE_BUTTONS - 1);

        if (endPage - startPage + 1 < MAX_PAGE_BUTTONS) {
            startPage = Math.max(1, endPage - MAX_PAGE_BUTTONS + 1);
        }

        for (int i = startPage; i <= endPage; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.getStyleClass().add("page_number_button");
            if (i == currentPage) {
                btn.setStyle("-fx-font-weight: bold; -fx-background-color: #eee;");
            }
            int page = i;
            btn.setOnAction(e -> showPage(page));
            page_button_box.getChildren().add(btn);
        }
    }

    private void showPage(int pageNum) {
        currentPage = pageNum;
        int fromIndex = (pageNum - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allPosts.size());
        ObservableList<Post> postList = FXCollections.observableArrayList(allPosts.subList(fromIndex, toIndex));
        table_board.setItems(postList);
        refreshPageButtons();
    }

    @FXML
    private void handlePostClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/CreatePost.fxml");
    }

    private void openPostDetail(Post selectedPost) {
        MainLayoutController.loadPostDetailView(selectedPost);
    }
}
