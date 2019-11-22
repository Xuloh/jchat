package fr.insa.jchat.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.deserializer.MessageDeserializer;
import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidRequestException;
import fr.insa.jchat.common.serializer.FileSerializer;
import fr.insa.jchat.common.serializer.MessageSerializer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class ActionController {
    private static final Logger LOGGER = LogManager.getLogger(ActionController.class);

    private static JChatClient jChatClient;

    private static String ip = null;

    private static Integer port = null;

    private static UUID session;

    private static Socket socket;

    private static PrintStream socketOut;

    private static BufferedReader socketIn;

    public static ObservableMap<String, User> users = FXCollections.synchronizedObservableMap(FXCollections.observableHashMap());

    private static Gson gson = new GsonBuilder()
        .registerTypeAdapter(File.class, new FileSerializer())
        .registerTypeAdapter(File.class, new FileDeserializer())
        .registerTypeAdapter(Message.class, new MessageDeserializer(users))
        .registerTypeAdapter(Message.class, new MessageSerializer())
        .create();

    public static void setJChatClient(JChatClient jChatClient) {
        ActionController.jChatClient = jChatClient;
    }

    public static void handleAction(String action) {
        try {
            ensureSocket();
        }
        catch(IOException e) {
            LOGGER.error("An error occurred while trying to connect to {}:{}", ip, port, e);
            jChatClient.getConnectPane().displayMessage("Could not connect to server :(", true);
            return;
        }

        try {
            switch(action) {
                case "register":
                    doRegister();
                    break;
                case "login":
                    doLogin();
                    break;
                case "send-message":
                    doSendMessage();
                    break;
            }
        }
        catch(IOException | InvalidRequestException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void doRegister() throws InvalidRequestException, IOException {
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

    public static void doLogin() throws InvalidRequestException, IOException {
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
            List<User> users = getUserList();
            List<Message> messages = getMessageHistory();
            spawnMulticastListener(server);

            ServerPane serverPane = new ServerPane(server, user, messages, ip);
            jChatClient.setServerPane(serverPane);
        }
        else if(response.getMethod() == Request.Method.ERROR)
            jChatClient.getConnectPane().displayMessage(response.getParam("errorName"), true);
    }

    public static void doSendMessage() throws InvalidRequestException, IOException {
        LOGGER.info("Sending message");

        String messageText = jChatClient.getServerPane().getNewMessageText();
        Message message = new Message(null, messageText, null, null, null, null, null);
        String body = gson.toJson(message);

        Request messageRequest = new Request();
        messageRequest.setMethod(Request.Method.MESSAGE);
        messageRequest.setParam("session", session.toString());
        messageRequest.setParam("length", Integer.toString(body.length()));
        messageRequest.setBody(body);

        Request response = sendRequest(messageRequest);

        LOGGER.debug("Got response : {}", response);
    }

    public static Server getServerInfo() throws InvalidRequestException, IOException {
        Request serverRequest = new Request();
        serverRequest.setMethod(Request.Method.GET);
        serverRequest.setParam("resource", "SERVER_INFO");

        Request response = sendRequest(serverRequest);
        Server server = gson.fromJson(response.getBody(), Server.class);
        return server;
    }

    public static User getUserInfo(String username) throws InvalidRequestException, IOException {
        Request userRequest = new Request();
        userRequest.setMethod(Request.Method.GET);
        userRequest.setParam("session", session.toString());
        userRequest.setParam("resource", "USER_INFO");
        userRequest.setParam("username", username);

        Request response = sendRequest(userRequest);
        User user = gson.fromJson(response.getBody(), User.class);
        return user;
    }

    public static List<User> getUserList() throws InvalidRequestException, IOException {
        Request userListRequest = new Request();
        userListRequest.setMethod(Request.Method.GET);
        userListRequest.setParam("session", session.toString());
        userListRequest.setParam("resource", "USER_LIST");

        Request response = sendRequest(userListRequest);
        Type userListType = new TypeToken<List<User>>() {}.getType();
        List<User> userList = gson.fromJson(response.getBody(), userListType);
        userList.forEach(user -> users.put(user.getUsername(), user));
        return userList;
    }

    public static List<Message> getMessageHistory() throws InvalidRequestException, IOException {
        Request messageHistoryRequest = new Request();
        messageHistoryRequest.setMethod(Request.Method.GET);
        messageHistoryRequest.setParam("session", session.toString());
        messageHistoryRequest.setParam("resource", "MESSAGE_HISTORY");

        Request response = sendRequest(messageHistoryRequest);
        Type messageListType = new TypeToken<List<Message>>() {}.getType();
        List<Message> messages = gson.fromJson(response.getBody(), messageListType);
        return messages;
    }

    public static Request sendRequest(Request request) throws InvalidRequestException, IOException {
        Request.sendRequest(request, socketOut);
        Request response = Request.createRequestFromReader(socketIn);
        LOGGER.info("{}", response);
        return response;
    }

    public static void spawnMulticastListener(Server server) throws IOException {
        var multicastListenerTask = new MulticastListenerTask(server.getMulticastAddress(), server.getMulticastPort(), users);
        Service<Object> multicastListener = new Service<>() {
            @Override
            protected Task<Object> createTask() {
                return multicastListenerTask;
            }
        };
        multicastListener.start();
        multicastListenerTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue instanceof Message)
                jChatClient.getServerPane().addMessages((Message)newValue);
            else if(newValue instanceof User) {
                User user = (User)newValue;
                users.put(user.getUsername(), user);
            }
        });
    }

    public static void ensureSocket() throws IOException {
        if(ip == null)
            ip = jChatClient.getConnectPane().getValue("ip");

        if(port == null)
            port = Integer.parseInt(jChatClient.getConnectPane().getValue("port"));

        if(socket != null && (socket.isInputShutdown() || socket.isOutputShutdown()))
            socket.close();

        if(socket == null || socket.isClosed()) {
            socket = new Socket(ip, port);
            socketOut = new PrintStream(socket.getOutputStream());
            socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
    }
}
