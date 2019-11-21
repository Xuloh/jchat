package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.User;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserListWidget extends VBox {
    private List<User> users;

    private Insets margin;

    public UserListWidget(List<User> users) {
        this.users = users;
        this.margin = new Insets(0, 5, 5, 5);

        this.users
            .stream()
            .map(UserWidget::new)
            .forEach(userWidget -> {
                this.getChildren().add(userWidget);
                setMargin(userWidget, this.margin);
            });
    }
}
