package fr.insa.jchat.client;

import fr.insa.jchat.client.widgets.MessageWidget;
import fr.insa.jchat.common.Message;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Arrays;

public class MessagesPane extends ScrollPane {
    private VBox root;

    private Insets margin;

    public MessagesPane() {
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        this.margin = new Insets(0, 5, 5, 5);


        this.root = new VBox();
        this.setContent(this.root);
    }

    public void addMessages(Message... messages) {
        Arrays.stream(messages)
              .map(MessageWidget::new)
              .forEach(messageWidget -> {
                  this.root.getChildren().add(messageWidget);
                  VBox.setMargin(messageWidget, this.margin);
              });
    }
}
