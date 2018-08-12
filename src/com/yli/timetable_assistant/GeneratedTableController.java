package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.Resources;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.image.BufferedImage;
import java.util.ResourceBundle;



public class GeneratedTableController implements TableDrawTask.TaskCallbacks<BufferedImage> {

    @FXML
    private HBox generatedTableControlBar;
    @FXML
    private StackPane generatedTableImageViewContainer;
    @FXML
    private ImageView generatedTableImageView;
    @FXML
    private ProgressIndicator progressIndicator;

    private DayToCourseListMap dayToCourseListMap;

    private ResourceBundle bundle = ResourceBundle.getBundle(Resources.PATH);

    private ChoiceBox<Integer> fontSizeChoiceBox;

    //Should research then consider using a service.
    private TableDrawTask tableDrawTask;


    GeneratedTableController(DayToCourseListMap dayToCourseListMap){
        this.dayToCourseListMap = dayToCourseListMap;
    }


    @FXML
    private void initialize(){
        populateGeneratedTableControlBar();
        initializeImageView();
    }

    private void populateGeneratedTableControlBar(){
        Label changeFontLabel =  new Label(bundle.getString("changeFont"));

        fontSizeChoiceBox = new ChoiceBox<>();
        ObservableList<Integer> fontOptions = FXCollections.observableArrayList();
        for (int i=6; i<=72;i+=2) fontOptions.add(i);
        fontSizeChoiceBox.setItems(fontOptions);
        setDrawOnFontSizeChangedListener(fontSizeChoiceBox);

        Label saveImageLabel = new Label(bundle.getString("saveImage"));

        generatedTableControlBar.getChildren().addAll(changeFontLabel,
                fontSizeChoiceBox,saveImageLabel);
    }

    private void setDrawOnFontSizeChangedListener(ChoiceBox<Integer> fontSizeChoiceBox){
        fontSizeChoiceBox.setOnAction(event ->
            populateImageView(dayToCourseListMap,fontSizeChoiceBox.getSelectionModel().getSelectedItem()));
    }

    private void initializeImageView(){
        VBox.setVgrow(generatedTableImageViewContainer,Priority.ALWAYS);
        generatedTableImageView.setSmooth(true);
        generatedTableImageView.fitWidthProperty().bind(generatedTableImageViewContainer.widthProperty());
        generatedTableImageView.fitHeightProperty().bind(generatedTableImageViewContainer.heightProperty());
        populateImageView(dayToCourseListMap,12);
    }

    private void populateImageView(DayToCourseListMap dayToCourseListMap,int fontSize){
        tableDrawTask = new TableDrawTask(this,dayToCourseListMap,fontSize);
        Thread thread = new Thread(tableDrawTask);
        thread.setDaemon(true);
        thread.start();
    }


    @Override
    public void onLoading() {
        fontSizeChoiceBox.setDisable(true);
        generatedTableImageView.setVisible(false);
        progressIndicator.setVisible(true);
    }

    @Override
    public void onFinishedLoading(BufferedImage image) {
        WritableImage FXImage = new WritableImage(image.getWidth(),image.getHeight());
        generatedTableImageView.setImage(SwingFXUtils.toFXImage(image,FXImage));
        progressIndicator.setVisible(false);
        generatedTableImageView.setVisible(true);
        fontSizeChoiceBox.setDisable(false);
    }
}
