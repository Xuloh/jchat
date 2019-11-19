package fr.insa.jchat.server;

import com.google.gson.Gson;
import fr.insa.jchat.common.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        this.gson = new Gson();
    }

    @Override
    public void run() {
        LOGGER.info("Starting multicast thread");
        while(true) {
            try {
                Message message = this.jChatServer.getMulticastQueue().take();
                String body = gson.toJson(message);
                LOGGER.info(
                    "Sending message to multicast address {} : {}",
                    this.jChatServer.getServer().getMulticastAddress(),
                    message
                );

                String packetMessage = "MESSAGE\n" + "length:" + body.length() + "\n\n" + body;
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
