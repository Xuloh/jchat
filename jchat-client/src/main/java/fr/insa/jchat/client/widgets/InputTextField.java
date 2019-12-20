package fr.insa.jchat.client.widgets;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.util.function.Predicate;

public class InputTextField extends TextField {
    private Label errorLabel;

    private Predicate<String> validate;

    private boolean valid;

    public InputTextField(String tooltip) {
        this(tooltip, null, "");
    }

    public InputTextField(String tooltip, Predicate<String> validate, String errorMessage) {
        this.setTooltip(new Tooltip(tooltip));
        this.valid = true;

        if(validate != null) {
            this.errorLabel = new Label();
            this.errorLabel.setStyle("-fx-text-fill: #f03434");
            this.errorLabel.setVisible(false);
            this.validate = validate;
            this.valid = false;

            this.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) {
                    boolean matches = this.validate.test(this.getText());
                    this.valid = matches;
                    this.errorLabel.setVisible(!matches);
                    this.errorLabel.setText(matches ? "" : errorMessage);
                }
            });
        }
    }

    public boolean isValid() {
        return this.valid;
    }

    public Label getErrorLabel() {
        return this.errorLabel;
    }
}
