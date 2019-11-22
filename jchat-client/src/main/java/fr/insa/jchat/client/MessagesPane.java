package fr.insa.jchat.client;

import fr.insa.jchat.client.widgets.InputMessageWidget;
import fr.insa.jchat.client.widgets.MessageWidget;
import fr.insa.jchat.common.Message;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;

public class MessagesPane extends BorderPane {
    private ScrollPane scrollMessages;

    private InputMessageWidget inputMessageWidget;

    private VBox root;

    private Insets margin;

    public MessagesPane() {
        this.scrollMessages = new ScrollPane();
        this.scrollMessages.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.scrollMessages.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.margin = new Insets(0, 5, 5, 5);


        this.root = new VBox();
        this.scrollMessages.setContent(this.root);
        this.setCenter(this.scrollMessages);

        this.inputMessageWidget = new InputMessageWidget();
        this.setBottom(this.inputMessageWidget);
    }

    public void addMessages(Message... messages) {
        Arrays.stream(messages)
              .map(MessageWidget::new)
              .forEach(messageWidget -> {
                  this.root.getChildren().add(messageWidget);
                  VBox.setMargin(messageWidget, this.margin);
              });
    }

    public void addMessages(List<Message> messages) {
        messages.stream()
                .map(MessageWidget::new)
                .forEach(messageWidget -> {
                    this.root.getChildren().add(messageWidget);
                    VBox.setMargin(messageWidget, this.margin);
                });
    }

    public String getNewMessageText() {
        return this.inputMessageWidget.getText();
    }
}
