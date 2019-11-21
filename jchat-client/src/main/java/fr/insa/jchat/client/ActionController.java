package fr.insa.jchat.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.serializer.FileSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.UUID;

public class ActionController {
    private static final Logger LOGGER = LogManager.getLogger(ActionController.class);

    private static JChatClient jChatClient;

    private static String ip;

    private static int port;

    private static UUID session;

    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(File.class, new FileSerializer())
        .registerTypeAdapter(File.class, new FileDeserializer())
        .create();

    public static void setJChatClient(JChatClient jChatClient) {
        ActionController.jChatClient = jChatClient;
    }

    public static void handleAction(String action) {
        try {
            switch(action) {
                case "register":
                    doRegister();
                    break;
                case "login":
                    doLogin();
                    break;
            }
        }
        catch(IOException | InvalidParamValue | InvalidBodySizeException | InvalidMethodException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void doRegister() throws InvalidMethodException, InvalidParamValue, InvalidBodySizeException, IOException {
        LOGGER.info("Register");

        Request request = new Request();
        request.setMethod(Request.Method.REGISTER);
        request.setParam("username", jChatClient.getConnectPane().getValue("username"));
        request.setParam("password", jChatClient.getConnectPane().getValue("password"));

        Request response = sendRequest(request);

        if(response.getMethod() == Request.Method.OK) {
            jChatClient.getConnectPane().displayMessage("Successfully registered");
            doLogin();
        }
        else if(response.getMethod() == Request.Method.ERROR)
            jChatClient.getConnectPane().displayMessage(response.getParam("errorName"), true);
    }

    public static void doLogin() throws InvalidMethodException, InvalidParamValue, InvalidBodySizeException, IOException {
        LOGGER.info("Login");

        String username = jChatClient.getConnectPane().getValue("username");
        String password = jChatClient.getConnectPane().getValue("password");

        Request loginRequest = new Request();
        loginRequest.setMethod(Request.Method.LOGIN);
        loginRequest.setParam("username", username);
        loginRequest.setParam("password", password);

        Request response = sendRequest(loginRequest);

        if(response.getMethod() == Request.Method.OK) {
            session = UUID.fromString(response.getParam("session"));
            jChatClient.getConnectPane().displayMessage("Successfully logged in");
            Server server = getServerInfo();
            User user = getUserInfo(username);

            ServerPane serverPane = new ServerPane(server, user, ip);
            jChatClient.setServerPane(serverPane);
        }
        else if(response.getMethod() == Request.Method.ERROR)
            jChatClient.getConnectPane().displayMessage(response.getParam("errorName"), true);
    }

    public static Server getServerInfo() throws InvalidMethodException, InvalidParamValue, InvalidBodySizeException, IOException {
        Request serverRequest = new Request();
        serverRequest.setMethod(Request.Method.GET);
        serverRequest.setParam("resource", "SERVER_INFO");

        Request response = sendRequest(serverRequest);
        Server server = gson.fromJson(response.getBody(), Server.class);
        return server;
    }

    public static User getUserInfo(String username) throws InvalidMethodException, InvalidParamValue, InvalidBodySizeException, IOException {
        Request userRequest = new Request();
        userRequest.setMethod(Request.Method.GET);
        userRequest.setParam("session", session.toString());
        userRequest.setParam("resource", "USER_INFO");
        userRequest.setParam("username", username);

        Request response = sendRequest(userRequest);
        User user = gson.fromJson(response.getBody(), User.class);
        return user;
    }

    public static Request sendRequest(Request request) throws InvalidMethodException, InvalidParamValue, InvalidBodySizeException, IOException {
        ip = jChatClient.getConnectPane().getValue("ip");
        port = Integer.parseInt(jChatClient.getConnectPane().getValue("port"));

        try(
            Socket socket = new Socket(ip, port);
            PrintStream socketOut = new PrintStream(socket.getOutputStream());
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            Request.sendRequest(request, socketOut);
            Request response = Request.createRequestFromReader(socketIn);
            LOGGER.info("{}", response);
            return response;
        }
    }
}
