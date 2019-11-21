package fr.insa.jchat.client.widgets;

import fr.insa.jchat.client.ActionController;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class ActionButton extends Button {
    private String action;

    public ActionButton(String text, String tooltip, String action) {
        super(text);
        this.action = action;
        this.setTooltip(new Tooltip(tooltip));
        this.setOnAction(event -> ActionController.handleAction(action));
    }
}
