package fr.insa.jchat.server;

import fr.insa.jchat.common.Server;

import java.net.Inet4Address;

public class Config {
    private Server server;

    private Inet4Address bindAddress;

    private int backlog;

    private int maxConnections;

    public Config(Server server, Inet4Address bindAddress, int backlog, int maxConnections) {
        this.server = server;
        this.bindAddress = bindAddress;
        this.backlog = backlog;
        this.maxConnections = maxConnections;
    }

    public Server getServer() {
        return this.server;
    }

    public Config setServer(Server server) {
        this.server = server;
        return this;
    }

    public Inet4Address getBindAddress() {
        return this.bindAddress;
    }

    public Config setBindAddress(Inet4Address bindAddress) {
        this.bindAddress = bindAddress;
        return this;
    }

    public int getBacklog() {
        return this.backlog;
    }

    public Config setBacklog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    public int getMaxConnections() {
        return this.maxConnections;
    }

    public Config setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
        return this;
    }

    @Override
    public String toString() {
        return "Config{" + "server=" + server + ", bindAddress=" + bindAddress + ", backlog=" + backlog + ", maxConnections=" + maxConnections + '}';
    }
}
