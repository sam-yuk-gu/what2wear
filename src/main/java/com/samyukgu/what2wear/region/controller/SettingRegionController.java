package com.samyukgu.what2wear.region.controller;

import com.samyukgu.what2wear.common.controller.BasicHeaderController;
import com.samyukgu.what2wear.di.DIContainer;
import com.samyukgu.what2wear.layout.controller.MainLayoutController;
import com.samyukgu.what2wear.region.Session.RegionWeatherSession;
import com.samyukgu.what2wear.region.model.Region;
import com.samyukgu.what2wear.weather.model.Weather;
import com.samyukgu.what2wear.weather.service.WeatherService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SettingRegionController {
    @FXML private ComboBox<Region> parentComboBox;
    @FXML private ComboBox<Region> childComboBox;
    @FXML private StackPane root;
    @FXML private VBox container;

    private List<Region> regionList = new ArrayList<>();
    private RegionWeatherSession regionWeatherSession;
    private WeatherService weatherService;

    private void setupDI() {
        DIContainer diContainer = DIContainer.getInstance();
        regionWeatherSession = diContainer.resolve(RegionWeatherSession.class);
        weatherService = diContainer.resolve(WeatherService.class);
    }

    @FXML
    public void initialize() throws IOException {
        setupDI();
        // 1. 헤더 동적 삽입
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/samyukgu/what2wear/common/BasicHeader.fxml"));
            HBox header = loader.load();

            BasicHeaderController controller = loader.getController();
            controller.setTitle("지역 설정");
            controller.setOnBackAction(() -> {
                try {
                    Parent view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/samyukgu/what2wear/codi/CodiMainView.fxml")));
                    root.getChildren().setAll(view);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            container.getChildren().add(0, header); // StackPane 맨 위에 삽입
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/region.json");
            ObjectMapper mapper = new ObjectMapper();
            regionList = mapper.readValue(inputStream, new TypeReference<List<Region>>() {});

            // 1단계 지역(중복 제거)
            Set<String> parentNames = regionList.stream()
                    .map(Region::getRegionParent)
                    .filter(p -> p != null && !p.isBlank())
                    .collect(Collectors.toCollection(TreeSet::new));

            List<Region> parentRegions = parentNames.stream()
                    .map(name -> regionList.stream()
                            .filter(r -> r.getRegionParent().equals(name))
                            .findFirst().orElse(null))  // 대표 Region 하나만 사용
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            parentComboBox.getItems().addAll(parentRegions);

            parentComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    updateChildComboBox(newVal.getRegionParent());
                } else {
                    childComboBox.getItems().clear();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateChildComboBox(String selectedParent) {
        childComboBox.getItems().clear();
        List<Region> children = regionList.stream()
                .filter(r -> r.getRegionParent().equals(selectedParent))
                .filter(r -> r.getRegionChild() != null && !r.getRegionChild().isBlank())
                .sorted(Comparator.comparing(Region::getRegionChild))
                .collect(Collectors.toList());

        childComboBox.getItems().addAll(children);
    }

    @FXML
    public void handleSettingRegion(MouseEvent event) {
        Region selectedParent = parentComboBox.getValue();
        Region selectedChild = childComboBox.getValue();

        if (selectedChild != null) {
            Long id = selectedChild.getId();
            String parent = selectedParent.getRegionParent();
            String child = selectedChild.getRegionChild();
            Long nx = selectedChild.getNx();
            Long ny = selectedChild.getNy();

            regionWeatherSession.setRegion(new Region(id, parent, child, nx, ny));
            System.out.println("선택된 ID: " + id + ", nx: " + nx + ", ny: " + ny);
            Weather weather = weatherService.fetchWeatherFromApi(nx.intValue(), ny.intValue());
            regionWeatherSession.setWeather(weather);
            MainLayoutController.loadView("/com/samyukgu/what2wear/codi/CodiMainView.fxml");
        } else {
            // 그냥 모달 띄우기
            // 둘다 선택안했을때
        }
    }
}
