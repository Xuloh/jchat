package fr.insa.jchat.common.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import fr.insa.jchat.common.Message;
import fr.insa.jchat.common.User;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageDeserializer implements JsonDeserializer<Message> {
    private Map<String, User> users;

    public MessageDeserializer(Map<String, User> users) {
        this.users = users;
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject src = json.getAsJsonObject();

        UUID uuid = UUID.fromString(src.get("uuid").getAsString());
        String text = src.get("text").getAsString();

        List<String> userTags = StreamSupport
            .stream(src.get("userTags").getAsJsonArray().spliterator(), false)
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());

        List<String> links = StreamSupport
            .stream(src.get("links").getAsJsonArray().spliterator(), false)
            .map(JsonElement::getAsString)
            .collect(Collectors.toList());

        JsonElement senderElement = src.get("sender");
        String senderName = senderElement.isJsonNull() ? null : senderElement.getAsString();
        User sender = null;
        if(senderName != null && this.users.containsKey(senderName))
            sender = this.users.get(senderName);

        JsonElement receiverElement = src.get("receiver");
        String receiverName = receiverElement.isJsonNull() ? null : receiverElement.getAsString();
        User receiver = null;
        if(receiverName != null && this.users.containsKey(receiverName))
            receiver = this.users.get(receiverName);

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(src.get("date").getAsLong());

        return new Message(uuid, text, userTags, links, sender, receiver, date);
    }
}
