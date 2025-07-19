package com.samyukgu.what2wear.post.controller;

import com.samyukgu.what2wear.post.model.Post;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PostController implements Initializable {

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
    @FXML
    private ImageView backImage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 버튼 이벤트 설정
        button_text.setOnAction(event -> openPostCreate());

        // 1. 컬럼 셀 데이터 설정
        colNo.setCellValueFactory(new PropertyValueFactory<>("no"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colLikes.setCellValueFactory(new PropertyValueFactory<>("likes"));

        // 2. 테스트 데이터 넣기
        ObservableList<Post> postList = FXCollections.observableArrayList(
                new Post(1, "오늘의 OOTD", "코사", "2025-07-15", 13),
                new Post(2, "작성글 test title 2", "패션테러리스트", "2025-07-15", 1004),
                new Post(3, "작성글 test title 3", "재벌집막내아들", "2025-07-15", 5),
                new Post(4, "작성글 test title 4", "서울수경", "2025-07-15", 325),
                new Post(5, "작성글 test title 5", "나형돈", "2025-07-15", 62),
                new Post(6, "작성글 test title 6", "백호두", "2025-07-15", 0),
                new Post(7, "작성글 test title 7", "연홍시", "2025-07-15", 722)
        );

        table_board.setItems(postList);

        // 3. 첫 번째 행 배경 연그레이로 설정
        table_board.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Post item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle(""); // 빈 칸은 스타일 제거
                } else if (getIndex() == 0 && getItem() == table_board.getItems().get(0)) {
                    setStyle("-fx-background-color: #F2F2F2;"); // 첫 번째 줄 연회색
                } else {
                    setStyle("-fx-background-color: white;"); // 나머지는 흰색
                }
            }
        });
    }

    // 글쓰기 버튼 클릭 시 화면 이동하는 openPostCreate() 메서드
    private void openPostCreate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/post/post_create.fxml"));
            Parent postCreateRoot = loader.load();

            Stage stage = (Stage) button_text.getScene().getWindow(); // 현재 스테이지 가져오기
            Scene scene = new Scene(postCreateRoot);

            stage.setScene(scene); // 씬 변경
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}