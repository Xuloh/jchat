package fr.insa.jchat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidSessionException;
import fr.insa.jchat.common.exception.MissingParamException;
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
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class JChatServer {
    private static final Logger LOGGER = LogManager.getLogger(JChatServer.class);

    private Config config;

    private ServerSocket serverSocket;

    private Map<String, User> users;

    private Map<UUID, String> logins;

    private BlockingQueue<Message> multicastQueue;

    public JChatServer(Config config) throws IOException {
        this.config = config;
        this.serverSocket = new ServerSocket(
            config.getServer().getPort(),
            config.getBacklog(),
            config.getBindAddress()
        );
        this.users = new ConcurrentHashMap<>();
        this.logins = new ConcurrentHashMap<>();
        this.multicastQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        while(true) {
            LOGGER.info("Listening for a new connection");
            try {
                Socket clientSocket = this.serverSocket.accept();
                LOGGER.info("Starting client thread");
                new ClientThread(clientSocket, this).start();
            }
            catch(IOException e) {
                LOGGER.error("An error occurred while waiting for a connection", e);
            }
        }
    }

    public synchronized void checkSession(Request request) throws MissingParamException, InvalidParamValue, InvalidSessionException {
        Request.requiredParams(request, "session");
        try {
            UUID sessionUuid = UUID.fromString(request.getParam("session"));
            if(!this.logins.containsKey(sessionUuid))
                throw new InvalidSessionException();
        }
        catch(IllegalArgumentException e) {
            throw new InvalidParamValue("Invalid value for param session", e,"session");
        }
    }

    public Map<String, User> getUsers() {
        return this.users;
    }

    public Map<UUID, String> getLogins() {
        return this.logins;
    }

    public BlockingQueue<Message> getMulticastQueue() {
        return this.multicastQueue;
    }

    public Server getServer() {
        return this.config.getServer();
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

            try {
                Server server = new Server(
                    UUID.randomUUID(),
                    "My server",
                    null,
                    "Welcome to my chat server",
                    12345,
                    (Inet4Address)Inet4Address.getByName("225.0.0.0"),
                    8712
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
            catch(UnknownHostException e) {
                LOGGER.error(e.getMessage(), e);
                serverConfig = null;
            }
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
