package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.Strings;
import com.yli.timetable_assistant.table.DayToCourseListMap;
import com.yli.timetable_assistant.tasks.TableDrawTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;



public class GeneratedTableController implements TableDrawTask.TaskCallbacks<BufferedImage> {

    static final String FXML_PATH = "/com/yli/timetable_assistant/res/generatedTable.fxml";

    @FXML
    private HBox generatedTableControlBar;
    @FXML
    private StackPane generatedTableImageViewContainer;
    @FXML
    private ImageView generatedTableImageView;
    @FXML
    private ProgressIndicator progressIndicator;

    private DayToCourseListMap dayToCourseListMap;

    private ResourceBundle bundle = ResourceBundle.getBundle(Strings.class.getCanonicalName());

    private ChoiceBox<Integer> fontSizeChoiceBox;
    private int initialFontSize = 16;

    private BufferedImage tableImage;





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
        fontSizeChoiceBox.setValue(initialFontSize);
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
        setSaveOnClickListener(generatedTableImageView);
        populateImageView(dayToCourseListMap,initialFontSize);
    }

    private void setSaveOnClickListener(Node node){
        node.setOnMouseClicked(event -> {

            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG", "*.png"));

            File file = chooser.showSaveDialog(node.getScene().getWindow());

            /*Since we are saving, there should be only one extension in the list, so
            we just get the one at the first index, and extensions should be specified as
            *.<extension> so we trim the in string starting from the second index until the end
            to get a string extension which can be used in saving the file*/


            if (file != null) {
                String extension = chooser.getSelectedExtensionFilter().getExtensions()
                        .get(0).substring(2);
                saveImage(tableImage,file,extension);
            }
        });
    }


    private void saveImage(BufferedImage image,File file,String extension){
        try{
            ImageIO.write(image,extension,file);
        }
        catch(IOException e ){
            e.printStackTrace();
        }

    }
    private void populateImageView(DayToCourseListMap dayToCourseListMap,int fontSize){
        TableDrawTask tableDrawTask = new TableDrawTask(this,dayToCourseListMap,fontSize);
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
        this.tableImage = image;
        WritableImage FXImage = new WritableImage(image.getWidth(),image.getHeight());
        generatedTableImageView.setImage(SwingFXUtils.toFXImage(image,FXImage));
        progressIndicator.setVisible(false);
        generatedTableImageView.setVisible(true);
        fontSizeChoiceBox.setDisable(false);
    }
}
