package fr.insa.jchat.server;

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

public class ClientThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(ClientThread.class);

    private Socket clientSocket;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
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
            catch(InvalidBodySizeException | InvalidParamValue | InvalidMethodException e) {
                response = Request.createErrorResponse(e);
            }

            Request.sendResponse(response, socketOut);
        }
        catch(IOException e) {
            LOGGER.error("An error occurred in client thread {}", this.getName(), e);
        }

        this.close();
    }

    private Request handleRequest(Request request) {
        switch(request.getMethod()) {
            case MESSAGE:
                break;
            default:
                break;
        }
        Request request1 = new Request();
        request1.setMethod(Request.Method.OK);
        request1.setParams("foo", "bar");
        request1.setBody("lorem ipsum dolor sit amet");
        return request1;
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
