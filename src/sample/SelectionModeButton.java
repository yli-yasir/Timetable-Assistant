package sample;

import javafx.scene.control.Button;

 class SelectionModeButton extends Button {

    private SelectionMode step;

    SelectionModeButton(String text, SelectionMode step) {
        super(text);
        this.step= step;
    }


     SelectionMode getStep() {
        return step;
    }

}
