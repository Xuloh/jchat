package fr.insa.jchat.client.widgets;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

public class InputMessageWidget extends HBox {
    private Insets margin;

    private InputTextField textField;

    private ActionButton sendButton;

    public InputMessageWidget() {
        this.margin = new Insets(5, 5, 5, 0);
        this.textField = new InputTextField("Say something :D");
        this.sendButton = new ActionButton("Send", "Send your message", "send-message");
        this.sendButton.addEventHandler(ActionEvent.ACTION, event -> this.textField.setText(""));
        this.textField.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if(event.getCode() == KeyCode.ENTER)
                this.sendButton.fire();
        });
        this.getChildren().addAll(this.textField, this.sendButton);
        setMargin(this.textField, this.margin);
        setMargin(this.sendButton, this.margin);
    }

    public String getText() {
        return this.textField.getText();
    }
}
