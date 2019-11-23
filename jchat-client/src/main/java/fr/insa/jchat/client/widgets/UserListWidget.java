package fr.insa.jchat.client.widgets;

import fr.insa.jchat.client.ActionController;
import fr.insa.jchat.common.User;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class UserListWidget extends ScrollPane {
    private Map<String, UserWidget> userWidgetMap;

    private Insets margin;

    public UserListWidget() {
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.userWidgetMap = new HashMap<>();
        this.margin = new Insets(0, 5, 5, 5);

        VBox root = new VBox();

        TitledPane onlinePane = new TitledPane();
        onlinePane.setText("Online");
        VBox onlineVBox = new VBox();
        onlinePane.setContent(onlineVBox);
        VBox.setMargin(onlinePane, this.margin);

        TitledPane offlinePane = new TitledPane();
        offlinePane.setText("Offline");
        VBox offlineVBox = new VBox();
        offlinePane.setContent(offlineVBox);
        VBox.setMargin(offlinePane, this.margin);

        root.getChildren().addAll(onlinePane, offlinePane);

        ActionController.users
            .values()
            .forEach(user -> {
                UserWidget userWidget = new UserWidget(user);
                this.userWidgetMap.put(user.getUsername(), userWidget);

                if(user.getStatus() == User.Status.ONLINE)
                    onlineVBox.getChildren().add(userWidget);
                else if(user.getStatus() == User.Status.OFFLINE)
                    offlineVBox.getChildren().add(userWidget);

                VBox.setMargin(userWidget, margin);
            });

        ActionController.users.addListener((MapChangeListener<String, User>)change -> {
            User user = change.getValueAdded();

            if(change.wasAdded()) {
                UserWidget userWidget;

                if(!this.userWidgetMap.containsKey(user.getUsername())) {
                    userWidget = new UserWidget(change.getValueAdded());
                    VBox.setMargin(userWidget, margin);
                }
                else
                    userWidget = this.userWidgetMap.get(user.getUsername());

                if(user.getStatus() == User.Status.OFFLINE) {
                    onlineVBox.getChildren().remove(userWidget);
                    offlineVBox.getChildren().add(userWidget);
                }
                else {
                    offlineVBox.getChildren().remove(userWidget);
                    onlineVBox.getChildren().add(userWidget);
                }
            }
        });

        this.setContent(root);
    }
}
