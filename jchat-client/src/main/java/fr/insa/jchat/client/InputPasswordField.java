package fr.insa.jchat.client;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;

import java.util.function.Function;

public class InputPasswordField extends PasswordField {
    private Label errorLabel;

    private Function<String, Boolean> validate;

    private boolean valid;

    public InputPasswordField(String tooltip) {
        this(tooltip, null, "");
    }

    public InputPasswordField(String tooltip, Function<String, Boolean> validate, String errorMessage) {
        this.setTooltip(new Tooltip(tooltip));
        this.valid = true;

        if(validate != null) {
            this.errorLabel = new Label(errorMessage);
            this.errorLabel.setStyle("-fx-text-fill: #f03434");
            this.valid = false;

            this.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if(!newValue) {
                    boolean matches = this.validate.apply(this.getText());
                    this.valid = matches;
                    this.errorLabel.setVisible(!matches);
                }
            });
        }
    }

    public boolean valid() {
        return this.valid;
    }

    public Label getErrorLabel() {
        return this.errorLabel;
    }
}
