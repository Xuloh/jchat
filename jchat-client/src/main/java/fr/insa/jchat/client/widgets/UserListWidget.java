package fr.insa.jchat.client.widgets;

import fr.insa.jchat.client.ActionController;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.exception.InvalidUsernameException;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class UserListWidget extends TitledPane {
    private Insets margin;

    public UserListWidget() {
        super();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox root = new VBox();

        this.margin = new Insets(0, 5, 5, 5);

        ActionController.users
            .keySet()
            .stream()
            .map(username -> new UserWidget(ActionController.users.get(username)))
            .forEach(userWidget -> {
                root.getChildren().add(userWidget);
                VBox.setMargin(userWidget, margin);
            });

        ActionController.users.addListener((MapChangeListener<String, User>)change -> {
            if(change.wasAdded()) {
                UserWidget widget = new UserWidget(change.getValueAdded());
                root.getChildren().add(widget);
                VBox.setMargin(widget, margin);
            }
        });

        scrollPane.setContent(root);
        this.setContent(scrollPane);
        this.setText("Users");
        this.setCollapsible(false);
    }
}
