package com.samyukgu.what2wear.common.util;

import javafx.animation.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class FxStyleUtil {

    private FxStyleUtil() {}

    // ease-out 유사 효과
    public static void applyHoverTransition(Button button, Color from, Color to) {
        button.setOnMouseEntered(e -> animateBackgroundColor(button, from, to, 200));
        button.setOnMouseExited(e -> animateBackgroundColor(button, to, from, 200));
    }

    private static void animateBackgroundColor(Region region, Color from, Color to, int durationMillis) {
        final ObjectProperty<Color> color = new SimpleObjectProperty<>(from);

        color.addListener((obs, oldColor, newColor) -> {
            region.setStyle("-fx-background-color: " + toRgba(newColor) + ";");
        });

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(color, from, Interpolator.EASE_OUT)),
            new KeyFrame(Duration.millis(durationMillis), new KeyValue(color, to, Interpolator.EASE_OUT))
        );

        timeline.play();
    }

    private static String toRgba(Color c) {
        return String.format("rgba(%d, %d, %d, %.2f)",
            (int) (c.getRed() * 255),
            (int) (c.getGreen() * 255),
            (int) (c.getBlue() * 255),
            c.getOpacity()
        );
    }
}
