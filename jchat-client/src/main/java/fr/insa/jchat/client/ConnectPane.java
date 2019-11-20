package fr.insa.jchat.client;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ConnectPane extends VBox {
    private static final String IP_REGEX = "^(localhost|[12]?\\d{1,2}\\.[12]?\\d{1,2}\\.[12]?\\d{1,2}\\.[12]?\\d{1,2})$";

    private Insets margin;

    private InputTextField ipField;

    private InputTextField portField;

    private InputTextField usernameField;

    private InputPasswordField passwordField;

    private Label messageLabel;

    public ConnectPane() {
        this.margin = new Insets(5, 5, 0, 5);
        this.messageLabel = new Label();
        this.messageLabel.setVisible(false);
        this.createServerPane();
        this.createUserPane();
        this.getChildren().add(this.messageLabel);
        setMargin(this.messageLabel, this.margin);
    }

    public String getValue(String input) throws IllegalArgumentException {
        switch(input) {
            case "ip":
                return this.ipField.isValid() ? this.ipField.getText() : null;
            case "port":
                return this.portField.isValid() ? this.portField.getText() : null;
            case "username":
                return this.usernameField.isValid() ? this.usernameField.getText() : null;
            case "password":
                return this.passwordField.isValid() ? this.passwordField.getText() : null;
            default:
                throw new IllegalArgumentException("Invalid argument " + input);
        }
    }

    public void displayMessage(String message) {
        this.displayMessage(message, false);
    }

    public void displayMessage(String message, boolean error) {
        if(message == null || message.length() == 0) {
            this.messageLabel.setText("");
            this.messageLabel.setVisible(false);
        }
        else {
            if(error)
                this.messageLabel.setStyle("-fx-text-fill: #f03434");
            else
                this.messageLabel.setStyle("-fx-text-fill: #5AD419");
            this.messageLabel.setText(message);
            this.messageLabel.setVisible(true);
        }
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
