package fr.insa.jchat.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class JChatClient {
    private static final Logger LOGGER = LogManager.getLogger(JChatClient.class);

    public static void main(String[] args) {
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
    }
}
