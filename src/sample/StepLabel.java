package sample;

import javafx.scene.control.Label;

 class StepLabel extends Label {

    private SelectionStep step;

    StepLabel(String text,SelectionStep step) {
        super(text);
        this.step= step;
    }


    public SelectionStep getStep() {
        return step;
    }

}
