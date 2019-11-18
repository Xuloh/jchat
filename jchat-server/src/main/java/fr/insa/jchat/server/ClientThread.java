package fr.insa.jchat.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
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
        try {
            BufferedReader socIn = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintStream socOut = new PrintStream(this.clientSocket.getOutputStream());
            while(true) {
                String line = socIn.readLine();
                socOut.println(line);
            }
        }
        catch(Exception e) {
            LOGGER.error("An error occurred in client thread {}", this.getName(), e);
        }
    }
}
