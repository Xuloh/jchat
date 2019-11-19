package fr.insa.jchat.server;

import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.exception.InvalidBodySizeException;
import fr.insa.jchat.common.exception.InvalidMethodException;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidRequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
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
                Request request = this.createRequest(socketIn);
                LOGGER.debug("Got request : {}", request);
                response = this.handleRequest(request);
            }
            catch(InvalidBodySizeException | InvalidParamValue | InvalidMethodException e) {
                response = this.createErrorResponse(e);
            }

            this.sendResponse(response, socketOut);
        }
        catch(IOException e) {
            LOGGER.error("An error occurred in client thread {}", this.getName(), e);
        }

        this.close();
    }

    private Request createRequest(BufferedReader in) throws IOException, InvalidParamValue, InvalidBodySizeException, InvalidMethodException {
        Request request = new Request();

        // read request method
        String line = in.readLine();
        LOGGER.debug("Method : {}", line);
        try {
            request.setMethod(Request.Method.valueOf(line));
        }
        catch(IllegalArgumentException e) {
            throw new InvalidMethodException(line);
        }

        // read request params
        line = in.readLine();
        while(line.length() > 0) {
            String[] param = line.split(":", 2);
            LOGGER.debug("Param : {}", (Object)param);
            request.setParams(param[0], param[1]);
            line = in.readLine();
        }

        // handle body if length param is present
        try {
            if(request.hasParam("length")) {
                // parse length value
                int length = Integer.parseInt(request.getParam("length"));
                if(length < 0)
                    throw new NumberFormatException("For input string : " + length);

                // read that many characters from input
                char[] body = new char[length];
                int nbRead = in.read(body, 0, length);

                // check that enough characters were read
                if(nbRead != length)
                    throw new InvalidBodySizeException(length, nbRead);

                LOGGER.debug("Body : {}", (Object)body);
                request.setBody(new String(body));
            }
        }
        catch(NumberFormatException e) {
            throw new InvalidParamValue("length param must be strictly greater than 0", e, "length");
        }

        return request;
    }

    private Request createErrorResponse(InvalidRequestException e) {
        Request request = new Request();
        request.setMethod(Request.Method.ERROR);
        request.setParams("errorName", e.getErrorName());

        try {
            Field[] fields = e.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                request.setParams(field.getName(), field.get(e).toString());
            }
        }
        catch(IllegalAccessException ex) {
            LOGGER.error("An error occured while creating error response", ex);
        }

        return request;
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

    private void sendResponse(Request response, PrintStream out) {
        out.println(response.getMethod());

        for(String param : response.getParamNames())
            out.println(param + ":" + response.getParam(param));

        out.println();

        out.println(response.getBody());
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
