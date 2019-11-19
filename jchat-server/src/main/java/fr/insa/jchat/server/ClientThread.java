package fr.insa.jchat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.exception.InvalidLoginException;
import fr.insa.jchat.common.exception.InvalidMessageException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidRequestException;
import fr.insa.jchat.common.exception.InvalidSessionException;
import fr.insa.jchat.common.exception.InvalidUsernameException;
import fr.insa.jchat.common.exception.MissingBodyException;
import fr.insa.jchat.common.exception.MissingParamException;
import fr.insa.jchat.common.serializer.FileSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);

    private JChatServer jChatServer;

    private Socket clientSocket;

    private Gson gson;

    public ClientThread(Socket clientSocket, JChatServer jChatServer) {
        this.clientSocket = clientSocket;
        this.jChatServer = jChatServer;
        this.gson = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(File.class, new FileDeserializer())
            .create();
    }

    @Override
    public void run() {
        // try catch to handle I/O errors
        try(
            BufferedReader socketIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintStream socketOut = new PrintStream(this.clientSocket.getOutputStream())
        ) {
            Request response;

            // try catch to handle request errors that uses the I/O streams
            try {
                Request request = Request.createRequestFromReader(socketIn);
                LOGGER.debug("Got request : {}", request);
                response = this.handleRequest(request);
            }
            catch(InvalidRequestException e) {
                response = Request.createErrorResponse(e);
            }

            Request.sendResponse(response, socketOut);
        }
        catch(IOException e) {
            LOGGER.error("An error occurred in client thread {}", this.getName(), e);
        }

        this.close();
    }

    private Request handleRequest(Request request) throws InvalidRequestException {
        switch(request.getMethod()) {
            case REGISTER:
                return this.handleRegister(request);
            case LOGIN:
                return this.handleLogin(request);
            case GET:
                return this.handleGet(request);
            case MESSAGE:
                return this.handleMessage(request);
            default:
                LOGGER.error("Unsupported request method : {}", request);
                return null;
        }
    }

    private Request handleRegister(Request request) throws MissingParamException, InvalidUsernameException {
        Request.requiredParams(request, "username", "password");
        String username = request.getParam("username");
        String password = request.getParam("password");

        synchronized(this.jChatServer.getUsers()) {

            if(this.jChatServer.getUsers().containsKey(username))
                throw new InvalidUsernameException(username);

            User user = new User(username, password, null, "#4F87FF");
            this.jChatServer.getUsers().put(username, user);
            LOGGER.debug("Users : {}", this.jChatServer.getUsers());
        }


        Request response = new Request();
        response.setMethod(Request.Method.OK);
        return response;
    }

    private Request handleLogin(Request request) throws MissingParamException, InvalidLoginException {
        Request.requiredParams(request, "username", "password");
        String username = request.getParam("username");
        String password = request.getParam("password");

        synchronized(this.jChatServer.getUsers()) {
            if(!this.jChatServer.getUsers().containsKey(username) ||
            !this.jChatServer.getUsers().get(username).getPassword().equals(password))
                throw new InvalidLoginException(username);
        }

        UUID uuid = UUID.randomUUID();
        this.jChatServer.getLogins().put(uuid, username);

        Request response = new Request();
        response.setMethod(Request.Method.OK);
        response.setParam("session", uuid.toString());
        return response;
    }

    private Request handleGet(Request request) throws MissingParamException, InvalidSessionException, InvalidParamValue {
        Request.requiredParams(request, "resource");

        String resource = request.getParam("resource");

        switch(resource) {
            case "SERVER_INFO":
                return this.handleGetServerInfo(request);
            case "USER_LIST":
                return this.handleGetServerUserList(request);
            case "":
                break;
        }
        return null;
    }

    private Request handleGetServerInfo(Request request) {
        String serverInfo = this.gson.toJson(this.jChatServer.getServer());
        int length = serverInfo.length();

        Request response = new Request();
        response.setMethod(Request.Method.OK);
        response.setParam("length", Integer.toString(length));
        response.setBody(serverInfo);

        return response;
    }

    private Request handleGetServerUserList(Request request) throws InvalidSessionException, MissingParamException, InvalidParamValue {
        this.jChatServer.checkSession(request);

        List<User> users = new ArrayList<>(this.jChatServer.getUsers().values());
        String userList = this.gson.toJson(users);

        Request response = new Request();
        response.setMethod(Request.Method.OK);
        response.setParam("length", Integer.toString(userList.length()));
        response.setBody(userList);

        return response;
    }

    private Request handleMessage(Request request) throws MissingBodyException, InvalidSessionException, MissingParamException, InvalidParamValue, InvalidMessageException {
        Request.requireBody(request);
        this.jChatServer.checkSession(request);
        try {
            Message message = this.gson.fromJson(request.getBody(), Message.class);
            this.jChatServer.getMulticastQueue().put(message);
        }
        catch(JsonParseException e) {
            throw new InvalidMessageException("Invalid message : " + request.getBody(), e, request.getBody());
        }
        catch(InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        Request response = new Request();
        response.setMethod(Request.Method.OK);
        return response;
    }

    private void close() {
        try {
            this.clientSocket.close();
        }
        catch(IOException e) {
            LOGGER.error("An error occurred while closing client socket", e);
        }
    }
}
