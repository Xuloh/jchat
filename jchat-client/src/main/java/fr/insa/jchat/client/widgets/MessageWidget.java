package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.Message;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.text.DateFormat;

public class MessageWidget extends HBox {
    private Message message;

    private Insets margin;

    public MessageWidget(Message message) {
        this.message = message;
        this.margin = new Insets(5, 0, 5, 5);

        Label username = new Label(this.message.getSender().getUsername());
        Label messageText = new Label(this.message.getText());
        messageText.setWrapText(true);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        Label date = new Label(dateFormat.format(this.message.getDate()));

        this.getChildren().addAll(username, messageText, date);
        setMargin(username, this.margin);
        setMargin(messageText, this.margin);
        setMargin(date, this.margin);
    }
}
