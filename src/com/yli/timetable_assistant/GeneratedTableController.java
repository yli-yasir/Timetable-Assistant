package com.yli.timetable_assistant;

import com.yli.timetable_assistant.res.IntsBundle;
import com.yli.timetable_assistant.res.StringsBundle;
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

    //Will have some instructions and choice box for changing font.
    @FXML
    private HBox generatedTableControlBar;

    private ChoiceBox<Integer> fontSizeChoiceBox;

    //Will hold the progress indicator and the image view.
    @FXML
    private StackPane generatedTableImageViewContainer;
    @FXML
    private ImageView generatedTableImageView;
    @FXML
    private ProgressIndicator progressIndicator;

    //The map we are going to draw the table from.
    private DayToCourseListMap dayToCourseListMap;

    private ResourceBundle stringsBundle = ResourceBundle.getBundle(StringsBundle.class.getCanonicalName());

    private IntsBundle intsBundle =  (IntsBundle) ResourceBundle.getBundle(IntsBundle.class.getCanonicalName());

    private int initialFontSize = intsBundle.getInteger("initialFontSize");

    //Drawn image that might be saved later.
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
        Label changeFontLabel =  new Label(stringsBundle.getString("changeFont"));

        fontSizeChoiceBox = makeChangeFontChoiceBox();

        Label saveImageLabel = new Label(stringsBundle.getString("saveImage"));

        generatedTableControlBar.getChildren().addAll(changeFontLabel,
                fontSizeChoiceBox,saveImageLabel);
    }

    private ChoiceBox<Integer> makeChangeFontChoiceBox(){
         ChoiceBox<Integer> fontSizeChoiceBox = new ChoiceBox<>();
        ObservableList<Integer> fontOptions = FXCollections.observableArrayList();
        for (int i=6; i<=72;i+=2) fontOptions.add(i);
        fontSizeChoiceBox.setItems(fontOptions);
        fontSizeChoiceBox.setValue(initialFontSize);
        fontSizeChoiceBox.setOnAction(event ->
                populateImageView(dayToCourseListMap,fontSizeChoiceBox.getSelectionModel().getSelectedItem()));
        return fontSizeChoiceBox;
    }

    //Make image view the same size as it's container,even when window is resized.
    private void initializeImageView(){
        VBox.setVgrow(generatedTableImageViewContainer,Priority.ALWAYS);
        generatedTableImageView.setSmooth(true);
        generatedTableImageView.fitWidthProperty().bind(generatedTableImageViewContainer.widthProperty());
        generatedTableImageView.fitHeightProperty().bind(generatedTableImageViewContainer.heightProperty());

        generatedTableImageView.setOnMouseClicked(e->{
            FileChooser saveDialog = makeSaveDialog();
            File file = saveDialog.showSaveDialog(generatedTableImageView.getScene().getWindow());
            if (file != null) saveImage(tableImage,file,getExtension(saveDialog));
        });

        populateImageView(dayToCourseListMap,initialFontSize);
    }


    private FileChooser makeSaveDialog(){
        FileChooser saveDialog = new FileChooser();
        saveDialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG", "*.png"));
        return saveDialog;
    }


    /*Since we are saving, there should be only one extension in the list, so
        we just get the one at the first index, and extensions should be specified as
        *.<extension> so we trim the in string starting from the second index until the end
        to get a string extension which can be used in saving the file*/
    private String getExtension(FileChooser saveDialog){
        return saveDialog.getSelectedExtensionFilter().getExtensions()
                .get(0).substring(2);
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
    public void onFailed(Throwable failureType) {

    }

    @Override
    public void onSucceeded(BufferedImage image) {
        this.tableImage = image;
        WritableImage FXImage = new WritableImage(image.getWidth(),image.getHeight());
        generatedTableImageView.setImage(SwingFXUtils.toFXImage(image,FXImage));
        progressIndicator.setVisible(false);
        generatedTableImageView.setVisible(true);
        fontSizeChoiceBox.setDisable(false);
    }
}
