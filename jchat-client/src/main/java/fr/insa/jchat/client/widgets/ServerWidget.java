package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.Server;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ServerWidget extends HBox {
    private Server server;

    private String ip;

    private Insets margin;

    public ServerWidget(Server server, String ip) {
        this.server = server;
        this.ip = ip;
        this.margin = new Insets(5, 0, 5, 5);

        Label serverName = new Label(this.server.getName());
        Label connect = new Label("(@" + this.ip + ':' + this.server.getPort() + ')');
        Label description = new Label(this.server.getDescription());

        this.getChildren().addAll(serverName, connect, description);
        setMargin(serverName, margin);
        setMargin(connect, margin);
        setMargin(description, margin);
    }
}
