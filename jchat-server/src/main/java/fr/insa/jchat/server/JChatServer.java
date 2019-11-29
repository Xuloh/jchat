package fr.insa.jchat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.Server;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.MessageDeserializer;
import fr.insa.jchat.common.exception.InvalidParamValue;
import fr.insa.jchat.common.exception.InvalidSessionException;
import fr.insa.jchat.common.exception.MissingParamException;
import fr.insa.jchat.common.serializer.FileSerializer;
import fr.insa.jchat.common.serializer.MessageSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    // map that stores user sessions
    private Map<UUID, String> logins;

    private List<Message> messages;

    // queue used with the MulticastThread
    private BlockingQueue<Request> multicastQueue;

    public JChatServer(Config config, Map<String, User> users, List<Message> messages) throws IOException {
        this.config = config;
        this.serverSocket = new ServerSocket(
            config.getServer().getPort(),
            config.getBacklog(),
            config.getBindAddress()
        );
        this.users = users;
        this.messages = messages;
        this.logins = new ConcurrentHashMap<>();
        this.multicastQueue = new LinkedBlockingQueue<>();
    }

    public void run() {
        // start the MulticastThread to consume multicast requests from the queue
        try {
            new MulticastThread(this).start();
        }
        catch(IOException e) {
            LOGGER.error("Error while trying to start MulticastThread", e);
            return;
        }

        while(true) {
            LOGGER.info("Listening for a new connection");
            try {
                Socket clientSocket = this.serverSocket.accept();
                LOGGER.info("Starting client thread");
                new ClientThread(clientSocket, this).start();
            }
            catch(SocketException e) {
                LOGGER.error("An error occurred while creating worker thread", e);
            }
            catch(IOException e) {
                LOGGER.error("An error occurred while waiting for a connection", e);
            }
        }
    }

    /**
     * Checks that the given request has a valid session param and returns it, if not throws a InvalidSessionException
     */
    public synchronized UUID checkSession(Request request) throws MissingParamException, InvalidParamValue, InvalidSessionException {
        Request.requiredParams(request, "session");
        try {
            UUID sessionUuid = UUID.fromString(request.getParam("session"));
            if(!this.logins.containsKey(sessionUuid))
                throw new InvalidSessionException();
            return sessionUuid;
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

    public BlockingQueue<Request> getMulticastQueue() {
        return this.multicastQueue;
    }

    public Server getServer() {
        return this.config.getServer();
    }

    /**
     * Returns the User associated with the given session
     * or throws an InvalidArgumentException if not user matches the given session
     */
    public User getUserFromSession(UUID session) {
        if(!this.logins.containsKey(session))
            throw new IllegalArgumentException("Invalid session " + session);
        return this.users.get(this.logins.get(session));
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    // saves user and messages data to json files
    public void saveData() throws FileNotFoundException {
        LOGGER.info("Saving server data");
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(Message.class, new MessageSerializer())
            .create();

        File userFile = new File("./users.json");
        File messageFile = new File("./messages.json");

        PrintStream userOut = new PrintStream(userFile);
        PrintStream messageOut = new PrintStream(messageFile);

        List<User> users = new ArrayList<>(this.users.values());

        gson.toJson(users, userOut);
        gson.toJson(this.messages, messageOut);
        LOGGER.info("Server data saved");
    }

    // loads server config from json file
    public static Config loadConfig() throws FileNotFoundException {
        LOGGER.info("Loading config file");

        File configFile = new File("./config.json");
        Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(File.class, new FileSerializer())
            .create();

        Config serverConfig;

        // generate default config
        if(!configFile.exists()) {
            LOGGER.warn("config.json not found, generating default configuration");

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
            Reader configIn = new InputStreamReader(new FileInputStream(configFile));
            serverConfig = gson.fromJson(configIn, Config.class);
        }

        LOGGER.info("Config file loaded");

        return serverConfig;
    }

    // loads user data from json file
    public static Map<String, User> loadUsers() throws FileNotFoundException {
        LOGGER.info("Loading user data");

        File userFile = new File("./users.json");
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileSerializer())
            .create();

        Map<String, User> users = new ConcurrentHashMap<>();

        if(!userFile.exists())
            LOGGER.warn("users.json not found");
        else {
            Reader userIn = new InputStreamReader(new FileInputStream(userFile));
            Type type = new TypeToken<List<User>>() {}.getType();
            List<User> userList = gson.fromJson(userIn, type);
            userList.forEach(user -> users.put(user.getUsername(), user));
            LOGGER.info("User data loaded");
        }

        return users;
    }

    // loads messages data from json file
    public static List<Message> loadMessages(Map<String, User> users) throws FileNotFoundException {
        LOGGER.info("Loading message data");

        File messageFile = new File("./messages.json");
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(Message.class, new MessageSerializer())
            .registerTypeAdapter(Message.class, new MessageDeserializer(users))
            .create();

        List<Message> messages = Collections.synchronizedList(new LinkedList<>());

        if(!messageFile.exists())
            LOGGER.warn("messages.json not found");
        else {
            Reader messageIn = new InputStreamReader(new FileInputStream(messageFile));
            Type type = new TypeToken<List<Message>>() {}.getType();
            List<Message> messagesTmp = gson.fromJson(messageIn, type);
            messages.addAll(messagesTmp);
            LOGGER.info("Message data loaded");
        }

        return messages;
    }

    public static void main(String[] args) {
        LOGGER.info("Starting JChat server ...");
        try {
            Config config = loadConfig();
            Map<String, User> users = loadUsers();
            List<Message> messages = loadMessages(users);
            JChatServer server = new JChatServer(config, users, messages);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.saveData();
                }
                catch(Exception e) {
                    LOGGER.fatal("Could not save server data !", e);
                }
            }, "ServerShutdown"));
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
