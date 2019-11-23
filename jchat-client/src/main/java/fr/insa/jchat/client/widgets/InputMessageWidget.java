package fr.insa.jchat.client.widgets;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class InputMessageWidget extends HBox {
    private InputTextField textField;

    private ActionButton sendButton;

    public InputMessageWidget() {
        this.textField = new InputTextField("Say something :D");
        this.textField.setPromptText("Send a message ...");
        this.sendButton = new ActionButton("Send", "Send your message", "send-message");
        this.textField.addEventHandler(ActionEvent.ACTION, event -> this.sendButton.fire());
        this.getChildren().addAll(this.textField, this.sendButton);
        setHgrow(this.textField, Priority.ALWAYS);
        setMargin(this.textField, new Insets(5, 0, 5, 5));
        setMargin(this.sendButton, new Insets(5, 5, 5, 5));
    }

    public String getText() {
        String text = this.textField.getText();
        this.textField.setText("");
        return text;
    }
}
