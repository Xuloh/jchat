package fr.insa.jchat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.serializer.FileSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class JChatServer {
    private static final Logger LOGGER = LogManager.getLogger(JChatServer.class);

    private Config config;

    private ServerSocket serverSocket;

    public JChatServer(Config config) throws IOException {
        this.config = config;
        this.serverSocket = new ServerSocket(
            config.getServer().getPort(),
            config.getBacklog(),
            config.getBindAddress()
        );
    }

    public void run() {
        while(true) {
            LOGGER.info("Listening for a new connection");
            try {
                Socket clientSocket = this.serverSocket.accept();
                LOGGER.info("Starting client thread");
                new ClientThread(clientSocket).start();
            }
            catch(IOException e) {
                LOGGER.error("An error occurred while waiting for a connection", e);
            }
        }
    }

    public static Config loadConfig() throws FileNotFoundException {
        LOGGER.info("Loading config file");

        File configFile = new File("./config.json");
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(File.class, new FileDeserializer())
            .create();

        Config serverConfig;

        if(!configFile.exists()) {
            LOGGER.warn("No config file found, creating a new one");

            Server server = new Server(
                UUID.randomUUID(),
                "My server",
                null,
                "Welcome to my chat server",
                12345
            );

            serverConfig = new Config(
                server,
                null,
                50,
                100
            );

            PrintStream configOut = new PrintStream(configFile);

            configOut.println(gson.toJson(serverConfig));

            LOGGER.info("Config file created");
        }
        else {
            BufferedReader configIn = new BufferedReader(new InputStreamReader(new FileInputStream(configFile)));
            serverConfig = gson.fromJson(configIn, Config.class);
        }

        LOGGER.info("Config file loaded");

        return serverConfig;
    }

    public static void main(String[] args) {
        LOGGER.info("Starting JChat server ...");
        try {
            Config config = loadConfig();
            JChatServer server = new JChatServer(config);
            server.run();
        }
        catch(FileNotFoundException e) {
            LOGGER.error("An error occurred while loading config file", e);
        }
        catch(IOException e) {
            LOGGER.error("An error occurred while creating server socket", e);
        }
    }
}