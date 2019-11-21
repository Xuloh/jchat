package fr.insa.jchat.client.widgets;

import fr.insa.jchat.common.User;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserListWidget extends TitledPane {
    private List<User> users;

    private Insets margin;

    public UserListWidget(List<User> users) {
        super();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox root = new VBox();

        this.users = users;
        this.margin = new Insets(0, 5, 5, 5);

        this.users
            .stream()
            .map(UserWidget::new)
            .forEach(userWidget -> {
                root.getChildren().add(userWidget);
                VBox.setMargin(userWidget, margin);
            });

        scrollPane.setContent(root);
        this.setContent(scrollPane);
        this.setText("Users");
        this.setCollapsible(false);
    }
}
