package fr.insa.jchat.client;

import fr.insa.jchat.client.util.NoSelectionModel;
import fr.insa.jchat.client.widgets.InputMessageWidget;
import fr.insa.jchat.client.widgets.MessageListCell;
import fr.insa.jchat.common.Message;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

import java.util.List;

public class MessagesPane extends BorderPane {
    private ListView<Message> listView;

    private InputMessageWidget inputMessageWidget;

    public MessagesPane() {
        this.listView = new ListView<>();
        this.listView.setEditable(false);
        this.listView.setSelectionModel(new NoSelectionModel<>());
        this.listView.setCellFactory(param -> new MessageListCell());
        this.setCenter(this.listView);

        this.inputMessageWidget = new InputMessageWidget();
        this.inputMessageWidget.prefWidthProperty().bind(this.widthProperty());
        this.setBottom(this.inputMessageWidget);
    }

    public void addMessages(Message... messages) {
        for(Message message : messages) {
            this.listView.getItems().add(message);
        }
        this.listView.scrollTo(this.listView.getItems().size() - 1);
    }

    public void addMessages(List<Message> messages) {
        for(Message message : messages) {
            this.listView.getItems().add(message);
        }
        this.listView.scrollTo(this.listView.getItems().size() - 1);
    }

    public String getNewMessageText() {
        return this.inputMessageWidget.getText();
    }
}
