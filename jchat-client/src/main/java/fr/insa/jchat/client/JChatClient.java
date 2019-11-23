package fr.insa.jchat.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JChatClient extends Application {
    private static final Logger LOGGER = LogManager.getLogger(JChatClient.class);

    private Pane root;

    private ConnectPane connectPane;

    private ServerPane serverPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.root = new Pane();

        this.connectPane = new ConnectPane();
        this.connectPane.prefWidthProperty().bind(this.root.widthProperty());
        this.connectPane.prefHeightProperty().bind(this.root.heightProperty());
        this.root.getChildren().add(this.connectPane);
        ActionController.setJChatClient(this);

        Scene scene = new Scene(this.root, 700, 400);

        primaryStage.setTitle("JChat Client");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        ActionController.doDisconnect();
    }

    public ServerPane getServerPane() {
        return this.serverPane;
    }

    public void setServerPane(ServerPane serverPane) {
        this.serverPane = serverPane;
        this.root.getChildren().remove(this.connectPane);
        this.serverPane.prefWidthProperty().bind(this.root.widthProperty());
        this.serverPane.prefHeightProperty().bind(this.root.heightProperty());
        this.root.getChildren().add(this.serverPane);
    }

    public void resetConnectPane() {
        this.root.getChildren().removeAll();
        this.root.getChildren().add(this.connectPane);
    }

    public ConnectPane getConnectPane() {
        return this.connectPane;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        LOGGER.info("Connecting to {}:{}", host, port);
        try(
            Socket echoSocket = new Socket(host, port);
            PrintStream socOut = new PrintStream(echoSocket.getOutputStream());
            BufferedReader socIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            LOGGER.info("Connected :D");
            StringBuilder request = new StringBuilder();
            StringBuilder response = new StringBuilder();

            String stdInLine = stdIn.readLine();

            while(!"exit".equals(stdInLine)) {
                if("send".equals(stdInLine)) {
                    socOut.println(request);
                    LOGGER.info("Sending request : {}", request);
                    request.delete(0, request.length());

                    String socInLine = socIn.readLine();
                    while(socInLine != null) {
                        response.append(socInLine);
                        response.append('\n');
                        socInLine = socIn.readLine();
                    }
                    LOGGER.info("Server response : {}", response);
                }
                else {
                    request.append(stdInLine);
                    request.append('\n');
                }

                stdInLine = stdIn.readLine();
            }
            LOGGER.info("See you ;)");
        }
        catch(UnknownHostException e) {
            LOGGER.error("Don't know about host : {}", host);
            System.exit(1);
        }
        catch(IOException e) {
            LOGGER.error("Couldn't get I/O for the connection to : {}", args[0]);
            System.exit(1);
        }
    }*/
}
