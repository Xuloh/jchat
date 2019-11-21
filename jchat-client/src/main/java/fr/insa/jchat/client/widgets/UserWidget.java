package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.User;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UserWidget extends HBox {
    private User user;

    private Insets margin;

    public UserWidget(User user) {
        this.user = user;
        this.margin = new Insets(5, 0, 5, 5);

        Label username = new Label(this.user.getUsername());

        this.getChildren().addAll(username);
        setMargin(username, margin);
    }
}
