package fr.insa.jchat.client.widgets;

import fr.insa.jchat.client.ActionController;
import fr.insa.jchat.common.User;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class UserListWidget extends ScrollPane {
    private static final Logger LOGGER = LogManager.getLogger(UserListWidget.class);

    private Map<String, UserWidget> userWidgetMap;

    private Insets margin;

    private VBox onlineVBox;

    private VBox offlineVBox;

    public UserListWidget() {
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        this.userWidgetMap = new HashMap<>();
        this.margin = new Insets(0, 5, 5, 5);

        VBox root = new VBox();

        TitledPane onlinePane = new TitledPane();
        onlinePane.setText("Online");
        this.onlineVBox = new VBox();
        onlinePane.setContent(this.onlineVBox);
        VBox.setMargin(onlinePane, this.margin);

        TitledPane offlinePane = new TitledPane();
        offlinePane.setText("Offline");
        this.offlineVBox = new VBox();
        offlinePane.setContent(this.offlineVBox);
        VBox.setMargin(offlinePane, this.margin);

        root.getChildren().addAll(onlinePane, offlinePane);

        ActionController.users
            .values()
            .forEach(user -> {
                UserWidget userWidget = new UserWidget(user);
                this.userWidgetMap.put(user.getUsername(), userWidget);

                if(user.getStatus() == User.Status.ONLINE)
                    this.onlineVBox.getChildren().add(userWidget);
                else if(user.getStatus() == User.Status.OFFLINE)
                    this.offlineVBox.getChildren().add(userWidget);

                VBox.setMargin(userWidget, margin);
            });

        ActionController.users.addListener((MapChangeListener<String, User>)change -> {
            User user = change.getValueAdded();
            if(change.wasAdded())
                this.update(user);
        });

        this.setContent(root);
    }

    public synchronized void update(User user) {
        LOGGER.debug("Updating user list : {}", user);
        UserWidget userWidget;

        if(!this.userWidgetMap.containsKey(user.getUsername())) {
            userWidget = new UserWidget(user);
            this.userWidgetMap.put(user.getUsername(), userWidget);
            VBox.setMargin(userWidget, margin);
        }
        else
            userWidget = this.userWidgetMap.get(user.getUsername());

        if(user.getStatus() == User.Status.OFFLINE)
            this.offlineVBox.getChildren().add(userWidget);
        else
            this.onlineVBox.getChildren().add(userWidget);
    }
}
