package fr.insa.jchat.common;

import java.io.File;
import java.util.List;

public class User {
    private String username;

    private String password;

    private File image;

    private String color;

    private List<Message> sentMessages;

    private List<Message> receivedMessages;

    public User(String username, String password, File image, String color, List<Message> sentMessages, List<Message> receivedMessages) {
        this.username = username;
        this.password = password;
        this.image = image;
        this.color = color;
        this.sentMessages = sentMessages;
        this.receivedMessages = receivedMessages;
    }

    public String getUsername() {
        return this.username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return this.password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public File getImage() {
        return this.image;
    }

    public User setImage(File image) {
        this.image = image;
        return this;
    }

    public String getColor() {
        return this.color;
    }

    public User setColor(String color) {
        this.color = color;
        return this;
    }

    public List<Message> getSentMessages() {
        return this.sentMessages;
    }

    public User setSentMessages(List<Message> sentMessages) {
        this.sentMessages = sentMessages;
        return this;
    }

    public List<Message> getReceivedMessages() {
        return this.receivedMessages;
    }

    public User setReceivedMessages(List<Message> receivedMessages) {
        this.receivedMessages = receivedMessages;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        User user = (User)o;

        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\'' + ", password='" + password + '\'' + ", image=" + image + ", color='" + color + '\'' + ", sentMessages=" + sentMessages + ", receivedMessages=" + receivedMessages + '}';
    }
}
