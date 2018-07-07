package sample;

import javafx.scene.control.Button;

 class StepButton extends Button {

    private SelectionStep step;

    StepButton(String text, SelectionStep step) {
        super(text);
        this.step= step;
    }


     SelectionStep getStep() {
        return step;
    }

}
