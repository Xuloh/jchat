package fr.insa.jchat.client.widgets;

import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

public class InputMessageWidget extends HBox {
    private Insets margin;

    private InputTextField textField;

    private ActionButton sendButton;

    public InputMessageWidget() {
        this.margin = new Insets(5, 5, 5, 0);
        this.textField = new InputTextField("Say something :D");
        this.sendButton = new ActionButton("Send", "Send your message", "send-message");
        this.getChildren().addAll(this.textField, this.sendButton);
        setMargin(this.textField, this.margin);
        setMargin(this.sendButton, this.margin);
    }

    public String getText() {
        return this.textField.getText();
    }
}
