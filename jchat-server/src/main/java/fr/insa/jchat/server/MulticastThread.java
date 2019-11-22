package fr.insa.jchat.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.Request;
import fr.insa.jchat.common.deserializer.FileDeserializer;
import fr.insa.jchat.common.serializer.FileSerializer;
import fr.insa.jchat.common.serializer.MessageSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MulticastThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(MulticastThread.class);

    private MulticastSocket multicastSocket;

    private JChatServer jChatServer;

    private Gson gson;

    public MulticastThread(JChatServer jChatServer) throws IOException {
        this.jChatServer = jChatServer;
        this.multicastSocket = new MulticastSocket(this.jChatServer.getServer().getMulticastPort());
        this.multicastSocket.joinGroup(this.jChatServer.getServer().getMulticastAddress());
        this.gson = new GsonBuilder()
            .registerTypeAdapter(File.class, new FileSerializer())
            .registerTypeAdapter(File.class, new FileDeserializer())
            .registerTypeAdapter(Message.class, new MessageSerializer())
            .create();
    }

    @Override
    public void run() {
        LOGGER.info("Starting multicast thread");
        while(true) {
            try {
                Request request = this.jChatServer.getMulticastQueue().take();
                LOGGER.info(
                    "Sending message to multicast address {} : {}",
                    this.jChatServer.getServer().getMulticastAddress(),
                    request
                );

                String packetMessage = Request.format(request);
                byte[] packetData = packetMessage.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(
                    packetData,
                    packetData.length,
                    this.jChatServer.getServer().getMulticastAddress(),
                    this.jChatServer.getServer().getMulticastPort()
                );

                try {
                    this.multicastSocket.send(packet);
                }
                catch(IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            catch(InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
                break;
            }
        }
    }
}
