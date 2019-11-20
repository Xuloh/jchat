package fr.insa.jchat.client;

import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ActionController {
    private static final Logger LOGGER = LogManager.getLogger(ActionController.class);

    private static ConnectPane connectPane;

    public static void setConnectPane(ConnectPane connectPane) {
        ActionController.connectPane = connectPane;
    }

    public static void handleAction(String action) {
        switch(action) {
            case "register":
                handleRegister();
                break;
            case "login":
                handleLogin();
                break;
        }
    }

    public static void handleRegister() {
        LOGGER.info("Register");
        String ip = connectPane.getValue("ip");
        int port = Integer.parseInt(connectPane.getValue("port"));

        try(
            Socket socket = new Socket(ip, port);
            PrintStream socketOut = new PrintStream(socket.getOutputStream());
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Request request = new Request();
            request.setMethod(Request.Method.REGISTER);
            request.setParam("username", connectPane.getValue("username"));
            request.setParam("password", connectPane.getValue("password"));

            Request.sendRequest(request, socketOut);

            Request response = Request.createRequestFromReader(socketIn);
            LOGGER.info("{}", response);
        }
        catch(IOException | InvalidBodySizeException | InvalidMethodException | InvalidParamValue e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void handleLogin() {
        LOGGER.info("Login");
    }
}
