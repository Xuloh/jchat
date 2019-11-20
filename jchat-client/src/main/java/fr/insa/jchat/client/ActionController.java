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
import java.util.UUID;

public class ActionController {
    private static final Logger LOGGER = LogManager.getLogger(ActionController.class);

    private static ConnectPane connectPane;

    private static String ip;

    private static int port;

    private static UUID session;

    public static void setConnectPane(ConnectPane connectPane) {
        ActionController.connectPane = connectPane;
    }

    public static void handleAction(String action) {
        switch(action) {
            case "register":
                doRegister();
                break;
            case "login":
                doLogin();
                break;
        }
    }

    public static void doRegister() {
        LOGGER.info("Register");
        ip = connectPane.getValue("ip");
        port = Integer.parseInt(connectPane.getValue("port"));

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

            if(response.getMethod() == Request.Method.OK) {
                connectPane.displayMessage("Successfully registered");
                doLogin();
            }
            else if(response.getMethod() == Request.Method.ERROR)
                connectPane.displayMessage(response.getParam("errorName"), true);
        }
        catch(IOException | InvalidBodySizeException | InvalidMethodException | InvalidParamValue e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void doLogin() {
        LOGGER.info("Login");
        ip = connectPane.getValue("ip");
        port = Integer.parseInt(connectPane.getValue("port"));

        try(
            Socket socket = new Socket(ip, port);
            PrintStream socketOut = new PrintStream(socket.getOutputStream());
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Request request = new Request();
            request.setMethod(Request.Method.LOGIN);
            request.setParam("username", connectPane.getValue("username"));
            request.setParam("password", connectPane.getValue("password"));

            Request.sendRequest(request, socketOut);

            Request response = Request.createRequestFromReader(socketIn);
            LOGGER.info("{}", response);

            if(response.getMethod() == Request.Method.OK) {
                session = UUID.fromString(response.getParam("session"));
                connectPane.displayMessage("Successfully logged in");
            }
            else if(response.getMethod() == Request.Method.ERROR)
                connectPane.displayMessage(response.getParam("errorName"), true);
        }
        catch(IOException | IllegalArgumentException | InvalidBodySizeException | InvalidMethodException | InvalidParamValue e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
