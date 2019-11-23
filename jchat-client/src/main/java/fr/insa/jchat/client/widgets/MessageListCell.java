package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.Message;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

import java.text.DateFormat;

public class MessageListCell extends ListCell<Message> {
    private HBox root;

    private Label usernameLabel;

    private Label messageTextLabel;

    private Label dateLabel;

    private Insets margin;

    private DateFormat dateFormat;

    public MessageListCell() {
        this.margin = new Insets(5, 0, 5, 5);
        this.root = new HBox();
        this.usernameLabel = new Label();
        this.messageTextLabel = new Label();
        messageTextLabel.setWrapText(true);
        this.dateLabel = new Label();
        this.root.getChildren().addAll(this.usernameLabel, this.messageTextLabel, this.dateLabel);
        HBox.setMargin(this.usernameLabel, this.margin);
        HBox.setMargin(this.messageTextLabel, this.margin);
        HBox.setMargin(this.dateLabel, this.margin);
        this.dateFormat = DateFormat.getDateTimeInstance();
    }

    @Override
    protected void updateItem(Message message, boolean empty) {
        super.updateItem(message, empty);

        if(empty || message == null) {
            this.setText(null);
            this.setGraphic(null);
        }
        else {
            if(message.getSender() != null)
                this.usernameLabel.setText(message.getSender().getUsername());
            else
                this.usernameLabel.setText("Server");

            this.messageTextLabel.setText(message.getText());
            this.dateLabel.setText(dateFormat.format(message.getDate().getTime()));
            this.setText(null);
            this.setGraphic(this.root);
        }
    }
}
