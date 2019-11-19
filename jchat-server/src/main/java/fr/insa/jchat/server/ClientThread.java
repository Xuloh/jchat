package fr.insa.jchat.server;

import com.google.gson.Gson;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidLoginException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidUsernameException;
import fr.insa.jchat.common.exception.MissingParamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.UUID;

public class ClientThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);

    private JChatServer jChatServer;

    private Socket clientSocket;

    public ClientThread(Socket clientSocket, JChatServer jChatServer) {
        this.clientSocket = clientSocket;
        this.jChatServer = jChatServer;
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
            catch(InvalidBodySizeException | InvalidParamValue | InvalidMethodException | MissingParamException | InvalidUsernameException | InvalidLoginException e) {
                response = Request.createErrorResponse(e);
            }

            Request.sendResponse(response, socketOut);
        }
        catch(IOException e) {
            LOGGER.error("An error occurred in client thread {}", this.getName(), e);
        }

        this.close();
    }

    private Request handleRequest(Request request) throws MissingParamException, InvalidUsernameException, InvalidLoginException {
        switch(request.getMethod()) {
            case REGISTER:
                return this.handleRegister(request);
            case LOGIN:
                return this.handleLogin(request);
            case GET:
                return this.handleGet(request);
            case MESSAGE:
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

            if(this.jChatServer.getUsers().containsKey(username)) {throw new InvalidUsernameException(username);}

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

    private Request handleGet(Request request) throws MissingParamException {
        Request.requiredParams(request, "resource");

        String resource = request.getParam("resource");

        switch(resource) {
            case "SERVER_INFO":
                return this.handleGetServerInfo(request);
            case "USER_LIST":
                break;
            case "":
                break;
        }
        return null;
    }

    private Request handleGetServerInfo(Request request) {
        Gson gson = new Gson();
        String serverInfo = gson.toJson(this.jChatServer.getServer());
        int length = serverInfo.length();

        Request response = new Request();
        response.setMethod(Request.Method.OK);
        response.setParam("length", Integer.toString(length));
        response.setBody(serverInfo);

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
