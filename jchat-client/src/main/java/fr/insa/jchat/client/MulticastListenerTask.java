package fr.insa.jchat.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.User;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.deserializer.MessageDeserializer;
import fr.insa.jchat.common.serializer.FileSerializer;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Map;

public class MulticastListenerTask extends Task<Message> {
    private static final Logger LOGGER = LogManager.getLogger(MulticastListenerTask.class);

    private static final int BUFFER_SIZE = 1024 * 1024 * 10;

    private MulticastSocket multicastSocket;

    private Gson gson;

    public MulticastListenerTask(InetAddress address, int port, Map<String, User> users) throws IOException {
        this.multicastSocket = new MulticastSocket(port);
        this.multicastSocket.joinGroup(address);
        this.gson = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(File.class, new FileDeserializer())
            .registerTypeAdapter(Message.class, new MessageDeserializer(users))
            .create();
    }

    @Override
    protected Message call() throws Exception {
        LOGGER.info("Starting multicast background task");
        while(true) {
            if(Thread.interrupted())
                break;

            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            this.multicastSocket.receive(packet);

            String requestStr = new String(packet.getData());
            BufferedReader bufferedReader = new BufferedReader(new StringReader(requestStr));
            Request request = Request.createRequestFromReader(bufferedReader);

            LOGGER.debug("Got multicast message : {}", request);
            switch(request.getMethod()) {
                case MESSAGE:
                    this.handleMessage(request);
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    private void handleMessage(Request request) {
        Message message = this.gson.fromJson(request.getBody(), Message.class);
        this.updateValue(message);
    }
}