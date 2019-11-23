package fr.insa.jchat.client;

import fr.insa.jchat.client.widgets.ServerWidget;
import fr.insa.jchat.client.widgets.UserListWidget;
import fr.insa.jchat.client.widgets.UserWidget;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import javafx.scene.layout.BorderPane;

import java.util.List;

public class ServerPane extends BorderPane {
    private Server server;

    private User user;

    private List<Message> messages;

    private MessagesPane messagesPane;

    private UserListWidget userListWidget;

    public ServerPane(Server server, User user, List<Message> messages, String ip) {
        this.server = server;
        this.user = user;
        this.messages = messages;

        ServerWidget serverWidget = new ServerWidget(server, ip);
        this.setTop(serverWidget);

        UserWidget userWidget = new UserWidget(user);
        this.setBottom(userWidget);

        this.userListWidget = new UserListWidget();
        this.setRight(this.userListWidget);

        this.messagesPane = new MessagesPane();
        this.messagesPane.addMessages(this.messages);
        this.setCenter(this.messagesPane);
    }

    public void addMessages(Message... messages) {
        this.messagesPane.addMessages(messages);
    }

    public String getNewMessageText() {
        return this.messagesPane.getNewMessageText();
    }

    public void updateUserList(User user) {
        this.userListWidget.update(user);
    }
}
