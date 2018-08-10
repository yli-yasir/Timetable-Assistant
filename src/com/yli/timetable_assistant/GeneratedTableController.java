package com.yli.timetable_assistant;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;


public class GeneratedTableController {

    @FXML
    private HBox generatedTableControlBar;
    @FXML
    private StackPane generatedTableImageViewContainer;
    @FXML
    private ImageView generatedTableImageView;
    @FXML
    private ProgressIndicator progressIndicator;

    private BufferedImage image;

    GeneratedTableController(BufferedImage image){
        this.image = image;
    }

    @FXML
    private void initialize(){
        generatedTableControlBar.getChildren().add(new Button("hi"));
        WritableImage FXImage = new WritableImage(image.getWidth(),image.getHeight());
        generatedTableImageView.setSmooth(true);
        VBox.setVgrow(generatedTableImageViewContainer,Priority.ALWAYS);
        generatedTableImageView.fitWidthProperty().bind(generatedTableImageViewContainer.widthProperty());
        generatedTableImageView.fitHeightProperty().bind(generatedTableImageViewContainer.heightProperty());
        generatedTableImageView.setImage(SwingFXUtils.toFXImage(image,FXImage));

    }


}
