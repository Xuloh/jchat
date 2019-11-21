package fr.insa.jchat.client;

import fr.insa.jchat.client.widgets.ServerWidget;
import fr.insa.jchat.client.widgets.UserWidget;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import javafx.scene.layout.BorderPane;

public class ServerPane extends BorderPane {
    private Server server;

    private User user;

    public ServerPane(Server server, User user, String ip) {
        this.server = server;
        this.user = user;

        ServerWidget serverWidget = new ServerWidget(server, ip);
        this.setTop(serverWidget);

        UserWidget userWidget = new UserWidget(user);
        this.setBottom(userWidget);
    }
}
