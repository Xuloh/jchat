package fr.insa.jchat.client;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ConnectPane extends VBox {
    private static final String IP_REGEX = "^[12]?\\d{1,2}\\.[12]?\\d{1,2}\\.[12]?\\d{1,2}\\.[12]?\\d{1,2}$";

    private static final String PORT_REGEX = "^|$";

    private Insets margin;

    private InputTextField ipField;

    private InputTextField portField;

    private InputTextField usernameField;

    private InputPasswordField passwordField;

    public ConnectPane() {
        this.margin = new Insets(5, 5, 0, 5);
        this.createServerPane();
        this.createUserPane();
    }

    private void createServerPane() {
        GridPane gridPane = this.newGridPane();

        this.ipField = new InputTextField("The IP address of the server", str -> str.matches(IP_REGEX), "Invalid IP address");
        this.portField = new InputTextField("The port of the server", str -> {
            try {
                int port = Integer.parseInt(str);
                return port >= 0 && port <= 65535;
            }
            catch(NumberFormatException e) {
                return false;
            }
        }, "Port must be a number between 0 and 65535");

        gridPane.addRow(0, new Label("Server address"), ipField, ipField.getErrorLabel());
        gridPane.addRow(1, new Label("Server port"), portField, portField.getErrorLabel());

        TitledPane pane = new TitledPane("Connect to a server", gridPane);
        pane.setCollapsible(false);
        this.getChildren().add(pane);
        setMargin(pane, this.margin);
    }

    private void createUserPane() {
        GridPane gridPane = newGridPane();
        this.usernameField = new InputTextField("Your username");
        this.passwordField = new InputPasswordField("Your totally top secret password");
        gridPane.addRow(0, new Label("Username"), this.usernameField);
        gridPane.addRow(1, new Label("Password"), this.passwordField);
        gridPane.addRow(
            2,
            new ActionButton("Register", "Create a new account on this server", "register"),
            new ActionButton("Login", "Login on this server", "login")
        );

        TitledPane pane = new TitledPane("User", gridPane);
        pane.setCollapsible(false);
        this.getChildren().add(pane);
        setMargin(pane, this.margin);
    }

    private GridPane newGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        return gridPane;
    }
}
