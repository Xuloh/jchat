package fr.insa.jchat.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class Message {
    private UUID uuid;

    private String text;

    private List<String> userTags;

    private List<String> links;

    private User sender;

    private User recipient;

    private Calendar date;

    public static Message fromText(String text) {
        return new Message(UUID.randomUUID(), text, null, null, null,null, Calendar.getInstance());
    }

    public Message(UUID uuid, String text, List<String> userTags, List<String> links, User sender, User recipient, Calendar date) {
        this.uuid = uuid;
        this.text = text;
        this.userTags = userTags == null ? new ArrayList<>() : userTags;
        this.links = links == null ? new ArrayList<>() : links;
        this.sender = sender;
        this.recipient = recipient;
        this.date = date;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Message setUuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public Message setText(String text) {
        this.text = text;
        return this;
    }

    public List<String> getUserTags() {
        return this.userTags;
    }

    public Message setUserTags(List<String> userTags) {
        this.userTags = userTags;
        return this;
    }

    public List<String> getLinks() {
        return this.links;
    }

    public Message setLinks(List<String> links) {
        this.links = links;
        return this;
    }

    public User getSender() {
        return this.sender;
    }

    public Message setSender(User sender) {
        this.sender = sender;
        return this;
    }

    public User getRecipient() {
        return this.recipient;
    }

    public Message setRecipient(User recipient) {
        this.recipient = recipient;
        return this;
    }

    public Calendar getDate() {
        return this.date;
    }

    public Message setDate(Calendar date) {
        this.date = date;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null || getClass() != o.getClass())
            return false;

        Message message = (Message)o;

        return uuid.equals(message.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "Message{" + "uuid=" + uuid + ", text='" + text + '\'' + ", userTags=" + userTags + ", links=" + links + ", sender=" + sender + ", recipient=" + recipient + ", date=" + date + '}';
    }
}
