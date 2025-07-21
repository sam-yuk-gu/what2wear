package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.layout.MainLayoutController;
import com.samyukgu.what2wear.post.model.Post;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class ListPostController implements Initializable {

    @FXML
    private TableView<Post> table_board;
    @FXML
    private TableColumn<Post, Integer> colNo;
    @FXML
    private TableColumn<Post, String> colTitle;
    @FXML
    private TableColumn<Post, String> colAuthor;
    @FXML
    private TableColumn<Post, String> colDate;
    @FXML
    private TableColumn<Post, Integer> colLikes;
    @FXML
    private Button button_text;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 버튼 이벤트 설정
        button_text.setOnAction(event -> handlePostClick());

        // 컬럼 셀 데이터 설정
        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colLikes.setCellValueFactory(new PropertyValueFactory<>("likes"));

        // 테스트 데이터 삽입
        ObservableList<Post> listPost = FXCollections.observableArrayList(
                new Post(1, "오늘의 OOTD", "코사", "오늘의 OOTD 입니다~~", "2025-07-15", 13),
                new Post(2, "작성글 test title 2", "패션테러리스트", "작성글 test title 2 입니다~~", "2025-07-15", 1004),
                new Post(3, "작성글 test title 3", "재벌집막내아들", "작성글 test title 3 입니다~~", "2025-07-15", 5),
                new Post(4, "작성글 test title 4", "서울수경", "작성글 test title 4 입니다~~", "2025-07-15", 325),
                new Post(5, "작성글 test title 5", "나형돈", "작성글 test title 5 입니다~~", "2025-07-15", 62),
                new Post(6, "작성글 test title 6", "백호두", "작성글 test title 6 입니다~~", "2025-07-15", 0),
                new Post(7, "작성글 test title 7", "연홍시", "작성글 test title 7 입니다~~", "2025-07-15", 722)
        );

        table_board.setItems(listPost);

        // 테이블 클릭 시 이벤트 추가
        table_board.setRowFactory(tv -> {
            TableRow<Post> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Post selectedPost = row.getItem();
                    openPostDetail(selectedPost);
                }
            });
            return row;
        });

    }

    // 글쓰기 버튼 클릭 시 화면 이동하는 메서드
    @FXML
    private void handlePostClick() {
        MainLayoutController.loadView("/com/samyukgu/what2wear/post/CreatePost.fxml");
    }

    // 게시글 클릭 후 상세 조회 화면 이동
    private void openPostDetail(Post selectedPost) { MainLayoutController.loadPostDetailView(selectedPost);}
}